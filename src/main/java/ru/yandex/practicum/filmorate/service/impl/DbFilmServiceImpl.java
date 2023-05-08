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
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);

    public DbFilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                             @Qualifier("userDbStorage") UserStorage userStorage, LikesDbStorage likesStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesStorage = likesStorage;
    }

    @Override
    public void addLike(int filmId, int userId) {
        validateFilmById(filmId);
        validateUserById(userId);
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        likesStorage.addLikeToFilm(filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        validateFilmById(filmId);
        validateUserById(userId);
        log.info("Пользователь id={} удалил лайк с фильма id={}", userId, filmId);
        likesStorage.deleteLikeFromFilm(filmId, userId);
    }

    @Override
    public Film getFilmById(int filmId) {
        Film film;
        try {
            film = filmStorage.getFilmById(filmId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Фильм с id={} не найден", filmId);
            throw new ObjectNotFoundException("Фильм с id = " + filmId + " не найден");
        }
        log.info("Получили фильм по id={}", filmId);
        return film;
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
        validateFilmById(film.getId());
        log.info("Обновлен фильм: {}", film);
        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("На данный момент сохранено фильмов: {}", filmStorage.getAllFilms().size());
        return filmStorage.getAllFilms();
    }

    private void validation(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            log.error("Дата релиза не может быть раньше {}", FIRST_FILM_RELEASE);
            throw new ValidationException("Дата релиза должна быть не раньше " + FIRST_FILM_RELEASE);
        }
        List<Film> filmsFromDB = filmStorage.getAllFilms();
        for (Film film1 : filmsFromDB) {
            if (film.getName().equals(film1.getName()) && film.getReleaseDate().equals(film1.getReleaseDate())
                    && film.getDuration() == film1.getDuration()) {
                log.error("Фильм с name={}, releaseDate={}, duration={}, уже существует", film.getName(),
                        film.getReleaseDate(), film.getDuration());
                throw new ValidationException("Фильм с name=" + film.getName() + ", releaseDate=" +
                        film.getReleaseDate() + ", duration= " + film.getDuration() + ", уже существует");
            }
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
