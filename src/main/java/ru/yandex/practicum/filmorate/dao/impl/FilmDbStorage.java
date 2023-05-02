package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    @Override
    public Film createFilm(Film film) {
        String sql = "insert into films (name, description, release_date, duration, mpa_id) values(?, ?, ?, ?, ?)";
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public List<Film> getAllFilms() {
        return null;
    }

    @Override
    public Film getFilmById(int filmId) {
        return null;
    }
}
