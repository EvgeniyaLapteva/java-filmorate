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
        getUserById(userId);
        getUserById(friendId);
        friendshipDao.addFriend(userId, friendId);
        log.info("Пользователи id = {} и id = {} добавились друг к другу в друзья", userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        friendshipDao.deleteFriend(userId, friendId);
        log.info("Пользователи id = {} и id = {} удалены из друзей друг у друга", userId, friendId);
    }

    @Override
    public User getUserById(int userId) {
        validateUserById(userId);
        log.info("Нашли пользователя с id = {}", userId);
        return storage.getUserById(userId);
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
        User createdUser = storage.createUser(user);
        log.info("Добавили пользователя: {}", createdUser);
        return createdUser;
    }

    @Override
    public User updateUser(User user) {
        try {
            if (storage.getUserById(user.getId()) == null) {
                log.error("Пользователя с id={} не существует", user.getId());
                throw new ObjectNotFoundException("Пользователя с id=" + user.getId() + " еще не существует");
            }
            Set<Integer> friendsOfUser = user.getFriendsIds();
            for (Integer friendId : friendsOfUser) {
                try {
                    if (storage.getUserById(friendId) == null) {
                        log.error("Пользователя с id={} не существует", friendId);
                    }
                } catch (EmptyResultDataAccessException e) {
                    throw new ObjectNotFoundException("Пользователя с id=" + friendId + " не существует");
                }
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Пользователя с id=" + user.getId() + " еще не существует");
        }
        validation(user);
        log.info("Обновили пользователя с id={}", user.getId());
        return storage.updateUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("На данный момент сохранено пользователей: {}", storage.getAllUsers().size());
        return storage.getAllUsers();
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
