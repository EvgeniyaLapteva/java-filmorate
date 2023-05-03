package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.LikesDbStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class DbFilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesDbStorage likesStorage;

    public DbFilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                             @Qualifier("userDbStorage") UserStorage userStorage, LikesDbStorage likesStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesStorage = likesStorage;
    }

    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);

    @Override
    public void addLike(int filmId, int userId) {
        validateFilmById(filmId);
        validateUserById(userId);
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        likesStorage.addLikeToFilm(filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        log.info("Пользователь id={} удалил лайк с фильма id={}", userId, filmId);
        likesStorage.deleteLikeFromFilm(filmId, userId);
    }

    @Override
    public Film getFilmById(int filmId) {
        log.info("Получили фильм по id={}", filmId);
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        log.info("Получили список самых популярных фильмов");
        return likesStorage.getPopularFilm(count);
    }

    @Override
    public Film createFilm(Film film) {
        validation(film);
        Film createdFilm = filmStorage.createFilm(film);
        log.info("Добавлен фильм: {}", createdFilm);
        return createdFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        validation(film);
        log.info("Обновлен фильм: {}", film);
        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("На данный момент сохранено фильмов: {}", filmStorage.getAllFilms().size());
        return filmStorage.getAllFilms();
    }

    private List<Integer> getLikesById(int filmId) {
        return likesStorage.getFilmsLikes(filmId);
    }

    private void validation(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            log.error("Дата релиза не может быть раньше {}", FIRST_FILM_RELEASE);
            throw new ValidationException("Дата релиза должна быть не раньше " + FIRST_FILM_RELEASE);
        }
    }

    private void validateFilmById(int filmId) {
        try {
            if (filmStorage.getFilmById(filmId) == null) {
                log.error("Фильма с id={} не существует", filmId);
                throw new ObjectNotFoundException("Фильма с id=" + filmId + " не существует");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Фильма с id=" + filmId + " не существует");
        }
    }

    private void validateUserById(int userId) {
        try {
            if (userStorage.getUserById(userId) == null) {
                log.error("Пользователя с id={} не существует", userId);
                throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
        }
    }
}
