package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.service.impl.DbFilmServiceImpl;
import ru.yandex.practicum.filmorate.service.impl.DbUserServiceImpl;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {

	private final UserDbStorage userDbStorage;
	private final FilmDbStorage filmDbStorage;
	private final FriendshipDao friendshipDao;
	private final GenreDao genreDao;
	private final LikesDao likesDao;
	private final MpaDao mpaDao;
	private final JdbcTemplate jdbcTemplate;



	@Test
	void contextLoads() {
	}



}
