package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);

    @Override
    public void addLike(int filmId, int userId) {
        validateFilmById(filmId);
        validateUserById(userId);
        Set<Integer> likes = getLikesById(filmId);
        likes.add(userId);
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        validateFilmById(filmId);
        validateUserById(userId);
        Set<Integer> likes = getLikesById(filmId);
        likes.remove(userId);
        log.info("Пользователь id={} удалил лайк с фильма id={}", userId, filmId);
    }

    @Override
    public Film getFilmById(int filmId) {
        validateFilmById(filmId);
        log.info("Получили фильм по id={}", filmId);
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        log.info("Получили список самых популярных фильмов");
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
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
        if (filmStorage.getFilmById(film.getId()) == null) {
            log.error("Фильма с id={} еще не было создано", film.getId());
            throw new ObjectNotFoundException("Фильма с id=" + film.getId() + " еще не было создано");
        }
        validation(film);
        log.info("Обновлен фильм: {}", film);
        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("На данный момент сохранено фильмов: {}", filmStorage.getAllFilms().size());
        return filmStorage.getAllFilms();
    }

    private Set<Integer> getLikesById(int filmId) {
        return filmStorage.getFilmById(filmId).getLikes();
    }

    private void validation(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            log.error("Дата релиза не может быть раньше {}", FIRST_FILM_RELEASE);
            throw new ValidationException("Дата релиза должна быть не раньше " + FIRST_FILM_RELEASE);
        }
    }

    private void validateFilmById(int filmId) {
        if (filmStorage.getFilmById(filmId) == null) {
            log.error("Фильма с id={} не существует", filmId);
            throw new ObjectNotFoundException("Фильма с id=" + filmId + " не существует");
        }
    }

    private void validateUserById(int userId) {
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователя с id={} не существует", userId);
            throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
        }
    }
}
