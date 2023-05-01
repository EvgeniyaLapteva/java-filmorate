package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean isAddFriend(int userId, int friendId) {
        String sql = "insert into friendship (user_id, friend_id, status) values(?, ?, false)";
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public boolean isUpdateFriend(int userId, int friendId, boolean status) {
        String sql = "update friendship set status = ? where user_id = ? and friend_id = ?";
        return jdbcTemplate.update(sql, status, userId, friendId) > 0;
    }

    @Override
    public boolean isDeleteFriend(int userId, int friendId) {
        String sql = "delete from friendship where user_id = ? and friendId = ?";
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public List<Integer> getAllFriendsByUserId(int userId) {
        String sql = "select friend_id from friendship where user_id = ? and status = true union select user_id " +
                "from friendship where friend_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId, userId);
    }
}
