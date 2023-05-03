package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreDao {

    List<Genre> getAllGenres();

    Genre getGenreById(int genreId);

    List<Genre> getGenreByFilmId(int filmId);
}
