package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface FriendshipStorage {

    boolean isAddFriend(int userId, int friendId);

    boolean isUpdateFriend(int userId, int friendId, boolean status);

    boolean isDeleteFriend(int userId, int friendId);

    List<Integer> getAllFriendsByUserId(int userId);
}
