package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private FilmValidation validation = new FilmValidation();
    private Map<Integer, Film> films = new HashMap<>();
    private int id = 0;
    private int generateId() {
        return ++id;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        if (validation.isNameIsEmpty(film.getName())) {
            log.error("Введено пустое название фильма");
            throw new ValidationException("Введено пустое название фильма");
        } else if (validation.iSDescriptionNoMoreThan200Symbols(film.getDescription())) {
            log.error("Превышено допустимое количество символов в описании - текущий размер {} символов",
                    film.getDescription().length());
            throw new ValidationException("Описание фильма должно содержать не более 200 символов");
        } else if (validation.isReleaseDateIsBeforeFirstFilm(film.getReleaseDate())) {
            log.error("Дата релиза не может быть раньше 28.12.1895 г.");
            throw new ValidationException("Дата релиза должна быть не раньше 28.12.1895 г.");
        } else if (validation.isDurationLessThan0(film.getDuration())) {
            log.error("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (validation.isNameIsEmpty(film.getName())) {
            log.error("Введено пустое название фильма");
            throw new ValidationException("Введено пустое название фильма");
        } else if (validation.iSDescriptionNoMoreThan200Symbols(film.getDescription())) {
            log.error("Превышено допустимое количество символов в описании - текущий размер {} символов",
                    film.getDescription().length());
            throw new ValidationException("Описание фильма должно содержать не более 200 символов");
        } else if (validation.isReleaseDateIsBeforeFirstFilm(film.getReleaseDate())) {
            log.error("Дата релиза не может быть раньше 28.12.1895 г.");
            throw new ValidationException("Дата релиза должна быть не раньше 28.12.1895 г.");
        } else if (validation.isDurationLessThan0(film.getDuration())) {
            log.error("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Добавлен фильм: {}", film);
        } else {
            throw new ValidationException("Фильма с id=" + film.getId() + " еще не было создано");
        }
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("На данный момент сохранено фильмов: {}", films.size());
        return films.values();
    }
}
