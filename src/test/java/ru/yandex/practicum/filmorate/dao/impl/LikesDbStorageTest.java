package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LikesDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final LikesDao likesDao;
    private final MpaDao mpaDao;

    @Test
    void shouldAddLikeToFilm() {
        Film filmTest = createFirstFilm();
        filmStorage.createFilm(filmTest);
        User user = createFirstUser();
        userStorage.createUser(user);
        likesDao.addLikeToFilm(1, 1);

        List<Integer> likes = likesDao.getFilmsLikes(1);

        assertEquals(1, likes.size(), "Списки лайков не совпадают");
    }

    @Test
    void shouldDeleteLikesFromFilm() {
        Film filmTest = createFirstFilm();
        filmStorage.createFilm(filmTest);
        User user = createFirstUser();
        userStorage.createUser(user);
        likesDao.addLikeToFilm(1, 1);
        likesDao.deleteLikeFromFilm(1, 1);

        List<Integer> likes = likesDao.getFilmsLikes(1);
        assertEquals(0, likes.size(), "Списки лайков не совпадают");
    }

    @Test
    void shouldGetPopularFilm() {
        Film firstFilm = createFirstFilm();
        Film secondFilm = createSecondFilm();
        Film testFilm1 = filmStorage.createFilm(firstFilm);
        Film testFilm2 = filmStorage.createFilm(secondFilm);
        User user = createFirstUser();
        User user1 = createSecondUser();
        userStorage.createUser(user);
        userStorage.createUser(user1);
        likesDao.addLikeToFilm(2, 1);
        likesDao.addLikeToFilm(2, 2);

        List<Film> popularFilms =likesDao.getPopularFilm(10);

        List<Film> testList = new ArrayList<>();
        testList.add(testFilm2);
        testList.add(testFilm1);

        assertEquals(testList, popularFilms, "Списки не равны");
    }

    @Test
    void shouldGetPopularFilmsWithoutFilms() {
        List<Film> popularFilms = likesDao.getPopularFilm(10);
        assertThat(popularFilms)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }


    private User createFirstUser() {
        return User.builder().id(1).email("email@mail.ru").login("login").name("name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
    }

    private User createSecondUser() {
        return User.builder().id(2).email("updated@mail.ru").login("loginUpdated").name("nameNew")
                .birthday(LocalDate.of(2000, 5, 5)).build();
    }

    private Film createFirstFilm() {
        Mpa mpa = mpaDao.getMpaById(1);
        return Film.builder().id(1).name("TestName").description("description")
                .releaseDate(LocalDate.of(2022, 10, 10)).duration(180)
                .mpa(mpa).build();
    }

    private Film createSecondFilm() {
        Mpa mpa = mpaDao.getMpaById(2);
        return Film.builder().id(1).name("NameAfterUpdate").description("afterUpdate")
                .releaseDate(LocalDate.of(2023, 1, 1)).duration(130).mpa(mpa).build();
    }
}