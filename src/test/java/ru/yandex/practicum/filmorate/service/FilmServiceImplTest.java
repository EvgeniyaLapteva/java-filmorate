package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceImplTest {

    private FilmService filmService;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmServiceImpl(filmStorage, userStorage);
    }

    private Film createFilm() {
        Film film = Film.builder().name("Name").description("description")
                .releaseDate(LocalDate.of(2020, 9, 9)).duration(180).build();
        return film;
    }

    private User createUser() {
        userService = new UserServiceImpl(userStorage);
        User user = userService.createUser(User.builder().email("user@mail.ru").login("login")
                .birthday(LocalDate.of(1995, 6, 6)).build());
        return user;
    }

    @Test
    public void shouldCreateFilm() {
        Film film = createFilm();

        Film filmResult = filmService.createFilm(film);
        film.setId(filmResult.getId());

        assertEquals(filmResult, filmResult, "Фильмы не совпадают");
    }

    @Test
    public void shouldUpdateFilm() {
        filmService.createFilm(createFilm());
        Film filmForUpdate = Film.builder().name("Update").description("Updated")
                .releaseDate(LocalDate.of(2020, 1, 5)).duration(120).build();
        filmForUpdate.setId(1);

        Film result = filmService.updateFilm(filmForUpdate);

        assertEquals(filmForUpdate, result, "Фильм не обновлен");
    }

    @Test
    public void shouldThrowException() {
        filmService.createFilm(createFilm());
        Film filmForUpdate = Film.builder().name("Update").description("Updated")
                .releaseDate(LocalDate.of(2020, 1, 5)).duration(120).build();
        filmForUpdate.setId(2);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> filmService.updateFilm(filmForUpdate));

        assertEquals("Фильма с id=" + filmForUpdate.getId() + " еще не было создано", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithWrongReleaseDate() {
        Film film = Film.builder().name("name").description("description")
                .releaseDate(LocalDate.of(1800, 10, 10)).duration(100).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmService.createFilm(film));
        assertEquals("Дата релиза должна быть не раньше " + LocalDate.of(1895, 12, 28),
                exception.getMessage());
    }

    @Test
    public void shouldGetAllFilms() {
        filmService.createFilm(createFilm());
        filmService.createFilm(Film.builder().name("second").description("description")
                .releaseDate(LocalDate.of(1999, 10, 11)).duration(160).build());

        List<Film> allFilms = filmService.getAllFilms();

        assertEquals(2, allFilms.size(), "Список всех фильмов не верный");
    }

    @Test
    public void shouldAddLike() {
        Film film = filmService.createFilm(createFilm());
        User user = createUser();

        filmService.addLike(film.getId(), user.getId());

        assertEquals(1, film.getLikes().size(), "Количество лайков не совпадает");
    }

    @Test
    public void shouldDeleteLike() {
        Film film = filmService.createFilm(createFilm());
        User user = createUser();
        filmService.addLike(film.getId(), user.getId());

        filmService.deleteLike(film.getId(), user.getId());

        assertEquals(0, film.getLikes().size(), "Количество лайков не совпадает");
    }

    @Test
    public void shouldGetFilmById() {
        Film filmExpected = filmService.createFilm(createFilm());

        Film filmResult = filmService.getFilmById(filmExpected.getId());

        assertEquals(filmExpected, filmResult, "Неверно выбран фильм по id");

    }

    @Test
    public void shouldGetPopularFilm() {
        Film film = filmService.createFilm(createFilm());
        Film film1 = filmService.createFilm(Film.builder().name("second").description("description")
                .releaseDate(LocalDate.of(2020, 6, 8)).duration(30).build());
        User user = createUser();
        User user1 = userService.createUser(User.builder().email("other@mail.com").login("other")
                .birthday(LocalDate.of(1999, 12, 12)).build());
        filmService.addLike(film.getId(), user.getId());
        filmService.addLike(film.getId(), user1.getId());
        filmService.addLike(film1.getId(), user.getId());
        List<Film> expected = new ArrayList<>();
        expected.add(film);
        expected.add(film1);

        List<Film> result = filmService.getPopularFilm(2);

        assertEquals(expected, result, "Список популярных фильмов не верный");
    }

}