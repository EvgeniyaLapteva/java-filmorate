package ru.yandex.practicum.filmorate.validation;

import java.time.LocalDate;

public class FilmValidation {

    public boolean isNameIsEmpty(String name) {
        return name.isBlank();
    }

    public boolean iSDescriptionNoMoreThan200Symbols(String description) {
        return description.length() > 200;
    }

    public boolean isReleaseDateIsBeforeFirstFilm(LocalDate releaseDate) {
        return releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }

    public boolean isDurationLessThan0(long duration) {
        return duration < 0;
    }
}
