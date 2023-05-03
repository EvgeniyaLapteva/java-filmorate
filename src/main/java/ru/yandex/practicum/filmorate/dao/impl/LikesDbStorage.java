package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

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
        String sql = "delete from likes where (film_id = ? and user_id = ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Integer> getFilmsLikes(int filmId) {
        String sql = "select user_id from likes where film_id =?";
        return jdbcTemplate.query(sql, (rs, rowNun) -> rs.getInt("user_id"), filmId);
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        String sql = "select f.*, m.name as mpa_name from films as f join mpa as m on f.mpa_id = m.mpa_id" +
                " left join (select film_id, count(user_id) as likes_count from likes group by film_id order by " +
                "likes_count) as popular on f.film_id = popular.film_id order by popular.likes_count desc limit ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> Film.builder()
                .id(rs.getInt("film_id")).name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder().id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa.name")).build()).build()), count);
    }
}
