package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;


@Repository
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    @Override
    public Film createFilm(Film film) {
        String sql = "insert into films (name, description, release_date, duration, mpa_id) values(?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        film.getGenres().forEach(genre -> addGenreToFilm(film.getId(), genre.getId()));
        log.info("Фильм {} сохранен", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                "where film_id = ?";
        if (jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId()) > 0) {
            deleteAllGenresFromFilm(film.getId());
            Set<Genre> genres = film.getGenres();
            for (Genre genre : genres) {
                addGenreToFilm(film.getId(), genre.getId());
            }
            genres.clear();
            List<Genre> genresToAdd = genreDao.getGenreByFilmId(film.getId());
            genres.addAll(genresToAdd);
            return film;
        }
        log.debug("Фильм с id={} не найден", film.getId());
        throw new ObjectNotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "select f.*, m.name from films as f join mpa as m on f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs));
        for (Film film: films) {
            Set<Genre> filmGenres = film.getGenres();
            List<Genre> genresToAdd = genreDao.getGenreByFilmId(film.getId());
            filmGenres.addAll(genresToAdd);
        }
        return films;
    }

    @Override
    public Film getFilmById(int filmId) {
        String sql = "select f.*, m.name from films as f join mpa as m on f.mpa_id = m.mpa_id where f.film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToFilm(rs), filmId);
            if (film != null) {
                Set<Genre> filmGenres = film.getGenres();
                List<Genre> genresToAdd = genreDao.getGenreByFilmId(filmId);
                filmGenres.addAll(genresToAdd);
            }
            return film;
        } catch (EmptyResultDataAccessException e) {
            log.debug("Фильм с id={} не найден", filmId);
            throw new ObjectNotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    private void addGenreToFilm(int filmId, int genreId) {
        String sql = "insert into film_genre (film_id, genre_id) values(?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    private void deleteAllGenresFromFilm(int filmId) {
        String sql = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id")).name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder().id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa.name")).build()).build();
    }
}
