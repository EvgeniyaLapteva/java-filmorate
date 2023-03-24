package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    public void shouldCreateFilm() {
        Film film = Film.builder().name("Name").description("description").
                releaseDate(LocalDate.of(2005, 1, 2)).duration(30).build();
        Film filmResult = filmController.createFilm(film);
        film.setId(1);

        assertEquals(film, filmResult, "Фильмы не совпадают");
    }

    @Test
    public void shouldThrowExceptionWithNameIsEmptyWhenPost() {
        Film film = Film.builder().name("").description("description").
                releaseDate(LocalDate.of(2005, 1, 2)).duration(30).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Введено пустое название фильма", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithDescriptionMoreThan200WhenPost() {
        Film film = Film.builder().name("Film").description("description, very long and boring, it is nothing useful, " +
                        "just bla-bla-bla and other words that full an empty space in description to throw exception " +
                        "and pass this test. Some description, it is difficult to write it. Some text. Other text").
                releaseDate(LocalDate.of(2005, 1, 2)).duration(30).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Описание фильма должно содержать не более 200 символов", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithReleaseDateIsBeforeFirstFilmWhenPost() {
        Film film = Film.builder().name("Name").description("description").
                releaseDate(LocalDate.of(1805, 1, 2)).duration(30).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Дата релиза должна быть не раньше 28.12.1895 г.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithDurationIsNotPositiveWhenPost() {
        Film film = Film.builder().name("Name").description("description").
                releaseDate(LocalDate.of(2005, 1, 2)).duration(-20).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = Film.builder().name("Name").description("description").
                releaseDate(LocalDate.of(2005, 1, 2)).duration(30).build();
        filmController.createFilm(film);

        Film expectedFilm = Film.builder().id(1).name("Updated").description("updatedDescription")
                .releaseDate(LocalDate.of(1955, 1, 10)).duration(180).build();
        Film filmResult = filmController.updateFilm(expectedFilm);

        assertEquals(expectedFilm, filmResult, "Фильм не обновлен");
    }

    @Test
    public void shouldThrowExceptionWithEmptyNameWhenPut() {
        Film film = Film.builder().name("Name").description("description").
                releaseDate(LocalDate.of(2005, 1, 2)).duration(30).build();
        filmController.createFilm(film);
        Film filmWithEmptyName = Film.builder().id(1).name("").description("updatedDescription")
                .releaseDate(LocalDate.of(1955, 1, 10)).duration(180).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(filmWithEmptyName));

        assertEquals("Введено пустое название фильма", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithDescriptionMoreThan200WhenPut() {
        Film film = Film.builder().name("Name").description("description").
                releaseDate(LocalDate.of(2005, 1, 2)).duration(30).build();
        filmController.createFilm(film);
        Film filmWithEmptyName = Film.builder().id(1).name("updated").description("description, very long and boring, " +
                        "it is nothing useful, " +
                        "just bla-bla-bla and other words that full an empty space in description to throw exception " +
                        "and pass this test. Some description, it is difficult to write it. Some text. Other text")
                .releaseDate(LocalDate.of(1955, 1, 10)).duration(180).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(filmWithEmptyName));

        assertEquals("Описание фильма должно содержать не более 200 символов", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithReleaseDateIsBeforeFirstFilmWhenPut() {
        Film film = Film.builder().name("Name").description("description").
                releaseDate(LocalDate.of(2005, 1, 2)).duration(30).build();
        filmController.createFilm(film);
        Film filmWithEmptyName = Film.builder().id(1).name("updated").description("updatedDescription")
                .releaseDate(LocalDate.of(1755, 1, 10)).duration(180).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(filmWithEmptyName));

        assertEquals("Дата релиза должна быть не раньше 28.12.1895 г.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithDurationIsNotPositiveWhenPut() {
        Film film = Film.builder().name("Name").description("description").
                releaseDate(LocalDate.of(2005, 1, 2)).duration(30).build();
        filmController.createFilm(film);
        Film filmWithEmptyName = Film.builder().id(1).name("updated").description("updatedDescription")
                .releaseDate(LocalDate.of(1955, 1, 10)).duration(-30).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(filmWithEmptyName));

        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    public void shouldNotUpdateFilmWithWrongId() {
        Film film = Film.builder().name("Name").description("description").
                releaseDate(LocalDate.of(2005, 1, 2)).duration(30).build();
        filmController.createFilm(film);
        Film film1 = Film.builder().id(999).name("Name").description("wrong id")
                .releaseDate(LocalDate.of(1999, 12, 5)).duration(120).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(film1));

        assertEquals("Фильма с id=" + film1.getId() + " еще не было создано", exception.getMessage());
    }

    @Test
    public void shouldGetAllFilms() {
        Film film = Film.builder().name("Name").description("description").
                releaseDate(LocalDate.of(2005, 1, 2)).duration(30).build();
        filmController.createFilm(film);

        Collection<Film> films = filmController.getAllFilms();

        assertEquals(1, films.size(), "Список фильмов не возвращается");
    }
}