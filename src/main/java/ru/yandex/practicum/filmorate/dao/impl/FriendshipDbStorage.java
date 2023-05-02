package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = "insert into friendship (user_id, friend_id, status) values(?, ?, false)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "delete from friendship where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getAllFriendsById(int userId) {
        String sql = "select * from users where user_id in (select friend_id from friendship where user_id = ?)";
        List<User> friends = jdbcTemplate.query(sql, (rs, rowNum) -> User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate()).build(), userId);
        return friends;
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        List<User> commonFriends = new ArrayList<>();
        List<User> friendsOfUser = getAllFriendsById(userId);
        List<User> friendsOfFriend = getAllFriendsById(friendId);
        for (User user: friendsOfUser) {
            if (friendsOfFriend.contains(user)) {
                commonFriends.add(user);
            }
        }
        return commonFriends;
    }
}
