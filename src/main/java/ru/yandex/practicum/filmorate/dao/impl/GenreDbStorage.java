package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sql = "select * from genres";
        List<Genre> allGenres = jdbcTemplate.query(sql, (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("genre_id")).name(rs.getString("name")).build());
        return allGenres.stream().sorted(Comparator.comparingInt(Genre::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sql = "select * from genres where genre_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("genre_id")).name(rs.getString("name")).build(), genreId);
    }
}
