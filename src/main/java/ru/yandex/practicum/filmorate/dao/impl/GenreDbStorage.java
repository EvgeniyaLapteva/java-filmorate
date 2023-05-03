package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sql = "select * from genres order by genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("genre_id")).name(rs.getString("name")).build());
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sql = "select * from genres where genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> Genre.builder()
                    .id(rs.getInt("genre_id")).name(rs.getString("name")).build(), genreId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Жанр с id={} не найден",genreId);
            throw new ObjectNotFoundException("Жанр с id = " + genreId + "не найден");
        }
    }

    @Override
    public List<Genre> getGenreByFilmId(int filmId) {
        String sql = "select g.* from film_genre as fg join genres as g on fg.genre_id = g.genre_id where " +
                "fg.film_id = ? order by g.genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("genre_id")).name(rs.getString("name")).build(), filmId);
    }
}
