package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesDao {
    boolean addLikeToFilm(int filmId, int userId);

    boolean deleteLikeFromFilm(int filmId, int userId);

    List<Integer> getFilmsLikes(int filmId);

    List<Film> getPopularFilm(int count);
}
