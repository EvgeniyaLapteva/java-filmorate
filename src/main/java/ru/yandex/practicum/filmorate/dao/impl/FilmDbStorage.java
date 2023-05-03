package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
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
        return Optional.ofNullable(keyHolder.getKey())
                .map(id -> {
                    film.setId(id.intValue());
                    if (film.getGenres() != null) {
                        addGenres(film, keyHolder);
                    }
                    return film;
                }).orElse(null);
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId());
        if (film.getGenres() != null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            addGenres(film, keyHolder);
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String queryForAllFilms = "select f.*, m.name from films as f join mpa as m on f.mpa_id = m.mpa_id";
        List<Film> filmsFromQuery = jdbcTemplate.query(queryForAllFilms, (rs, rowNum) -> Film.builder()
                .id(rs.getInt("film_id")).name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa.name")).build())
                .build());
        for (Film film : filmsFromQuery) {
            if (film != null) {
                setGenresToFilm(film);
            }
        }
        return filmsFromQuery;
    }

    @Override
    public Film getFilmById(int filmId) {
        String sql = "select f.*, m.name from films as f join mpa as m on f.mpa_id = m.mpa_id where f.film_id = ?";
        Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> Film.builder()
                .id(rs.getInt("film_id")).name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa.name")).build())
                .build(), filmId);
        if (film != null) {
            setGenresToFilm(film);
            String queryForLikes = "select user_id from likes where film_id = ?";
            List<Integer> likes = jdbcTemplate.query(queryForLikes, new Object[]{filmId},
                    new int[]{Types.INTEGER}, (rs, rowNum) -> rs.getInt("user_id"));
            Set<Integer> likesForFilm = new HashSet<>(likes);
            film.setLikes(likesForFilm);
        }
        return film;
    }

    private Film addGenres(Film film, KeyHolder keyHolder) {
        String deleteGenres = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(deleteGenres, film.getId());
        if (film.getGenres().isEmpty()) {
            return film;
        }
        String addGenres = "insert into film_genre (film_id, genre_id) values(?, ?)";
        List<Genre> allFilmGenres = film.getGenres().stream().distinct().collect(Collectors.toList());
        for (Genre genre : allFilmGenres) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(addGenres);
                ps.setInt(1, film.getId());
                ps.setInt(2, genre.getId());
                return ps;
            }, keyHolder);
        }
        film.setGenres(allFilmGenres);
        return film;
    }

    private void setGenresToFilm(Film film) {
        String genresForFilm = "select fg.genre_id, g.name from film_genre as fg join genres as g " +
                "on fg.genre_id = g.genre_id where fg.film_id = ?";
        List<Genre> genresFromQuery = jdbcTemplate.query(genresForFilm, new Object[]{film.getId()},
                new int[]{Types.INTEGER}, (rs, rowNum) -> Genre.builder()
                        .id(rs.getInt("genre_id"))
                        .name(rs.getString("name")).build());
        film.setGenres(genresFromQuery);
    }
}
