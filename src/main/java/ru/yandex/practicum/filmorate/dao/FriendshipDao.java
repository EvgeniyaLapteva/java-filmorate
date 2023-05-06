package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipDao {

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getAllFriendsById(int userId);

    List<User> getCommonFriends(int userId, int friendId);

    void updateFriendship(int userId, int friendId, boolean status);
}
