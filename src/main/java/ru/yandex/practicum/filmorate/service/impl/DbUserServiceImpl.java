package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DbUserServiceImpl implements UserService {

    private final UserStorage storage;
    private final FriendshipDao friendshipDao;

    public DbUserServiceImpl(@Qualifier("userDbStorage") UserStorage storage, FriendshipDao friendshipDao) {
        this.storage = storage;
        this.friendshipDao = friendshipDao;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            log.error("Самого себя в друзья добавить нельзя");
            throw new ValidationException("Самого себя в друзья добавить нельзя");
        }
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Set<Integer> usersFriends = user.getFriendsIds();
        Set<Integer> friendsFriends = friend.getFriendsIds();
        boolean isUserHasFriend = usersFriends.contains(friendId);
        boolean isFriendHasUser = friendsFriends.contains(userId);
        if (!isUserHasFriend && !isFriendHasUser) {
            friendshipDao.addFriend(userId, friendId);
            usersFriends.add(friendId);
            log.info("Пользователь id = {} добавил в друзья пользователя id = {}", userId, friendId);
        } else if (!isUserHasFriend && isFriendHasUser) {
            friendshipDao.addFriend(userId, friendId);
            friendshipDao.updateFriendship(userId, friendId, true);
            friendshipDao.updateFriendship(friendId, userId, true);
            log.info("Пользователь id = {} подтвердил дружбу с пользователем id = {}", userId, friendId);
            usersFriends.add(friendId);
        } else {
            log.info("Пользователь id = {} уже в друзьях у пользователя id = {}", friendId, userId);
            throw new ValidationException("Пользователь id = " + friendId + " уже в друзьях у пользователя id = " +
                    userId);
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Set<Integer> usersFriends = user.getFriendsIds();
        Set<Integer> friendsFriends = friend.getFriendsIds();
        boolean isUserHasFriend = usersFriends.contains(friendId);
        boolean isFriendHasUser = friendsFriends.contains(userId);
        if (!isUserHasFriend) {
            log.error("Пользователь id = {} не в друзьях у пользователя id = {}", friendId, userId);
            throw new ObjectNotFoundException("Пользователь id = " + friendId +
                    " не в друзьях у пользователя id = " + userId);
        } else if (!isFriendHasUser) {
            friendshipDao.deleteFriend(userId, friendId);
            log.info("Пользователь id = {} удалил из друзей пользователя id = {}", userId, friendId);
        } else {
            friendshipDao.deleteFriend(userId, friendId);
            friendshipDao.updateFriendship(friendId, userId, false);
            log.info("Пользователь id = {} удалил из друзей пользователя id = {}, статус дружбы обновлен",
                    userId, friendId);
        }
    }

    @Override
    public User getUserById(int userId) {
        validateUserById(userId);
        log.info("Нашли пользователя с id = {}", userId);
        User user = storage.getUserById(userId);
        Set<Integer> usersFriends = user.getFriendsIds();
        usersFriends.addAll(friendshipDao.getAllFriendsById(user.getId()).stream().map(User::getId)
                .collect(Collectors.toSet()));
        return user;
    }

    @Override
    public List<User> getAllFriendsById(int userId) {
        getUserById(userId);
        log.info("Получили список друзей пользователя id={}", userId);
        return friendshipDao.getAllFriendsById(userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        log.info("Получили список общих друзей пользователей id={} и id={}", userId, friendId);
        return friendshipDao.getCommonFriends(userId, friendId);
    }

    @Override
    public User createUser(User user) {
        validation(user);
        Set<Integer> friendsOfUser = user.getFriendsIds();
        for (Integer friendsId : friendsOfUser) {
            if (!getAllUsers().contains(getUserById(friendsId))) {
                log.error("Пользователя с id = {} еще не существует", friendsId);
                friendsOfUser.remove(friendsId);
                throw new ObjectNotFoundException("Пользователя с id = " + friendsId + " еще не существует");
            }
        }
        User createdUser = storage.createUser(user);
        Set<Integer> friendsOfCreatedUser = user.getFriendsIds();
        for (Integer friendsId : friendsOfCreatedUser) {
            addFriend(createdUser.getId(), friendsId);
        }
        log.info("Добавили пользователя: {}", createdUser);
        return createdUser;
    }

    @Override
    public User updateUser(User user) {
        try {
            if (storage.getUserById(user.getId()) == null) {
                log.debug("Пользователя с id={} не существует", user.getId());
                throw new ObjectNotFoundException("Пользователя с id=" + user.getId() + " еще не существует");
            }
            Set<Integer> friendsOfUser = user.getFriendsIds();
            for (Integer friendsId : friendsOfUser) {
                if (!getAllUsers().contains(getUserById(friendsId))) {
                    log.error("Пользователя с id = {} еще не существует", friendsId);
                    friendsOfUser.remove(friendsId);
                    throw new ObjectNotFoundException("Пользователя с id = " + friendsId + " еще не существует");
                }
                addFriend(user.getId(), friendsId);
            }
        } catch (EmptyResultDataAccessException e) {
            log.error("Пользователя с id=" + user.getId() + " не существует");
            throw new ObjectNotFoundException("Пользователя с id=" + user.getId() + " еще не существует");
        }
        validation(user);
        log.info("Обновили пользователя с id={}", user.getId());
        return storage.updateUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("На данный момент сохранено пользователей: {}", storage.getAllUsers().size());
        List<User> allUsers = storage.getAllUsers();
        for (User user : allUsers) {
            Set<Integer> usersFriends = user.getFriendsIds();
            usersFriends.addAll(friendshipDao.getAllFriendsById(user.getId()).stream().map(User::getId)
                    .collect(Collectors.toSet()));
        }
        return allUsers;
    }

    private void validation(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        List<User> usersFromDB = storage.getAllUsers();
        for (User user1 : usersFromDB) {
            if (user1.getEmail().equals(user.getEmail())) {
                log.error("Пользователь с email = {} уже существует", user.getEmail());
                throw new ValidationException("Пользователь с email = " + user.getEmail() + " уже существует");
            }
            if (user1.getLogin().equals(user.getLogin())) {
                log.error("Пользователь с login = {} уже существует", user.getLogin());
                throw new ValidationException("Пользователь с login = " + user.getLogin() + " уже существует");
            }
        }
    }

    private void validateUserById(int userId) {
        try {
            if (storage.getUserById(userId) == null) {
                log.error("Пользователя с id={} не существует", userId);
                throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
        }
    }
}
