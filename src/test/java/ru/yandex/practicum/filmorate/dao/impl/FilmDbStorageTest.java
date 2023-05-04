package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    @Test
    void shouldCreateFilmWithId() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void shouldCreateFilmWithName() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "TestName")
                );
    }

    @Test
    void shouldCreateFilmWithDescription() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description",
                                "description")
                );
    }

    @Test
    void shouldCreateFilmWithReleaseDate() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("releaseDate",
                                LocalDate.of(2022, 10, 10))
                );
    }

    @Test
    void shouldCreateFilmWithDuration() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("duration", 180)
                );
    }

    @Test
    void shouldCreateFilmWithMpa() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Mpa mpa = mpaDao.getMpaById(1);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film.getMpa()).isEqualTo(mpa)
                );
    }

    @Test
    void shouldCreateFilmWithGenre() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Set<Genre> genres = new HashSet<>();
        genres.add(genreDao.getGenreById(1));

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film.getGenres()).isEqualTo(genres)
                );
    }

    @Test
    void shouldUpdateFilmById() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = filmForUpdate();

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void shouldUpdateFilmWithName() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = filmForUpdate();

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "NameAfterUpdate")
                );
    }

    @Test
    void shouldUpdateFilmWithDescription() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = filmForUpdate();

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description",
                                "afterUpdate")
                );
    }

    @Test
    void shouldUpdateFilmWithDuration() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = filmForUpdate();

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("duration", 130)
                );
    }

    @Test
    void shouldUpdateFilmWithMpa() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = filmForUpdate();
        Mpa mpa = mpaDao.getMpaById(2);

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film.getMpa()).isEqualTo(mpa)
                );
    }

    @Test
    void shouldUpdateFilmWithGenre() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = filmForUpdate();
        Set<Genre> testGenres = new HashSet<>();
        testGenres.add(genreDao.getGenreById(2));

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film.getGenres()).isEqualTo(testGenres)
                );
    }

    @Test
    void shouldGetAllFilms() {
        Film film = createTestFilm();
        Film film1 = filmForUpdate();
        film1.setId(2);
        filmStorage.createFilm(film);
        filmStorage.createFilm(film1);

        List<Film> allFilms = filmStorage.getAllFilms();

        assertEquals(2, allFilms.size(), "Список фильмов не соответствует истине");
    }

    private Film createTestFilm() {
        Mpa mpa = mpaDao.getMpaById(1);
        Film testFilm = Film.builder().id(1).name("TestName").description("description")
                .releaseDate(LocalDate.of(2022, 10, 10)).duration(180)
                .mpa(mpa).build();
        Set<Genre> genres = testFilm.getGenres();
        genres.add(genreDao.getGenreById(1));
        return testFilm;
    }

    private Film filmForUpdate() {
        Mpa mpa = mpaDao.getMpaById(2);
        Film filmForUpdate = Film.builder().id(1).name("NameAfterUpdate").description("afterUpdate")
                .releaseDate(LocalDate.of(2023, 1, 1)).duration(130).mpa(mpa).build();
        Set<Genre> testGenres = filmForUpdate.getGenres();
        testGenres.add(genreDao.getGenreById(2));
        return filmForUpdate;
    }
}