package ru.yandex.practicum.filmorate.dao;

import java.util.Set;

public interface LikesDao {
    void addLikeToFilm(int filmId, int userId);

    void deleteLikeFromFilm(int filmId, int userId);

    Set<Integer> getFilmsLikes(int filmId);
}
