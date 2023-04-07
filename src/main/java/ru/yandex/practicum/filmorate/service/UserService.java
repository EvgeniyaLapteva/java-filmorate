package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    User getUserById(int userId);

    List<User> getAllFriendsById(int userId);

    List<User> getCommonFriends(int userId, int friendId);

    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();
}
