package ru.yandex.practicum.filmorate.validation;

import java.time.LocalDate;

public class UserValidation {

    public boolean isEmailIsBlankOrNotContainsSpecSymbol(String email) {
        return email.isBlank() || !email.contains("@");
    }

    public boolean isLoginIsBlankOrContainsWhitespaces(String login) {
        return login.isBlank() || login.contains(" ");
    }

    public boolean isNameIsBlank(String name) {
        return name == null || name.equals("");
    }

    public boolean isBirthdayInFuture(LocalDate birthday) {
        return birthday.isAfter(LocalDate.now());
    }
}
