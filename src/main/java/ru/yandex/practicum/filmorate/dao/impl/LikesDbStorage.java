package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LikesDbStorage implements LikesDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLikeToFilm(int filmId, int userId) {
        String sql = "insert into likes (film_id, user_id) values(?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLikeFromFilm(int filmId, int userId) {
        String sql = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Set<Integer> getFilmsLikes(int filmId) {
        String sql = "select * from users where user_id in (select user_id from likes where film_id = ?)";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate()).build(), filmId);
        return users.stream().map(User::getId).collect(Collectors.toSet());
    }
}
