package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    public UserServiceImpl(@Qualifier("inMemoryUserStorage") UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        validateUserById(userId);
        validateUserById(friendId);
        Set<Integer> userFriends = getUserFriendsIds(userId);
        userFriends.add(friendId);
        Set<Integer> friendsOfFriend = getUserFriendsIds(friendId);
        friendsOfFriend.add(userId);
        log.info("Пользователи {} и {} добавились друг к другу в друзья", storage.getUserById(userId),
                storage.getUserById(friendId));
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        validateUserById(userId);
        validateUserById(friendId);
        Set<Integer> userFriends = getUserFriendsIds(userId);
        userFriends.remove(friendId);
        Set<Integer> friendsOfFriend = getUserFriendsIds(friendId);
        friendsOfFriend.remove(userId);
        log.info("Пользователи {} и {} удалены из друзей друг у друга", storage.getUserById(userId),
                storage.getUserById(friendId));
    }

    @Override
    public User getUserById(int userId) {
        validateUserById(userId);
        log.info("Нашли пользователя с id = {}", userId);
        return storage.getUserById(userId);
    }

    @Override
    public List<User> getAllFriendsById(int userId) {
        validateUserById(userId);
        log.info("Получили список друзей пользователя id={}", userId);
        return getUserFriendsIds(userId).stream()
                .map(storage::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        validateUserById(userId);
        validateUserById(friendId);
        Set<Integer> userFriends = getUserFriendsIds(userId);
        Set<Integer> friendsOfFriend = getUserFriendsIds(friendId);
        List<User> commonFriends = new ArrayList<>();
        for (int commonFriendId : userFriends) {
            if (friendsOfFriend.contains(commonFriendId)) {

                commonFriends.add(storage.getUserById(commonFriendId));
            }
        }
        log.info("Получили список общих друзей пользователей id={} и id={}", userId, friendId);
        return commonFriends;
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
        if (storage.getUserById(user.getId()) == null) {
            log.error("Пользователя с id={} не существует", user.getId());
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
    }
    private void validateUserById(int userId) {
        if (storage.getUserById(userId) == null) {
            log.error("Пользователя с id={} не существует", userId);
            throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
        }

    }
    private Set<Integer> getUserFriendsIds(int userId) {
        return storage.getUserById(userId).getFriendsIds();
    }
}
