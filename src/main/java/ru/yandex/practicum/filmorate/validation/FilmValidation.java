package ru.yandex.practicum.filmorate.validation;

import java.time.LocalDate;

public class FilmValidation {

    public static boolean isNameIsEmpty(String name) {
        return name == null || name.isBlank();
    }

    public static boolean iSDescriptionIsInCorrect(String description) {
        return description.length() > 200;
    }

    public static boolean isReleaseDateIsBeforeFirstFilm(LocalDate releaseDate) {
        return releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }

    public static boolean isDurationLessThan0(long duration) {
        return duration < 0;
    }
}
