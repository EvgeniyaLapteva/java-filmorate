package ru.yandex.practicum.filmorate.validation;

import java.time.LocalDate;

public class UserValidation {

    public static boolean isEmailIsInCorrect(String email) {
        return email == null || email.isBlank() || !email.contains("@");
    }

    public static boolean isLoginIsIncorrect(String login) {
        return  login == null || login.isBlank() || login.contains(" ");
    }

    public static boolean isNameIsBlank(String name) {
        return name == null || name.equals("");
    }

    public static boolean isBirthdayInFuture(LocalDate birthday) {
        return birthday.isAfter(LocalDate.now());
    }
}
