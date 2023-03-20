package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private int id = 0;
    private final Map<Integer, User> users = new HashMap<>();
    UserValidation validation = new UserValidation();
    private  int generateId() {
        return ++id;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (validation.isEmailIsBlankOrNotContainsSpecSymbol(user.getEmail())) {
            log.error("Введен неверный формат электронной почты");
            throw new ValidationException("Введен неверный формат электронной почты");
        } else if (validation.isLoginIsBlankOrContainsWhitespaces(user.getLogin())) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        } else if (validation.isBirthdayInFuture(user.getBirthday())) {
            log.error("День рождения не может быть в будущем");
            throw new ValidationException("День рождения не может быть в будущем");
        }
        user.setId(generateId());
        if (validation.isNameIsBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавили пользователя: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (validation.isEmailIsBlankOrNotContainsSpecSymbol(user.getEmail())) {
            log.error("Введен неверный формат электронной почты");
            throw new ValidationException("Введен неверный формат электронной почты");
        } else if (validation.isLoginIsBlankOrContainsWhitespaces(user.getLogin())) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        } else if (validation.isBirthdayInFuture(user.getBirthday())) {
            log.error("День рождения не может быть в будущем");
            throw new ValidationException("День рождения не может быть в будущем");
        }
        if (validation.isNameIsBlank(user.getName())) {
            user = User.builder().email(user.getEmail()).id(user.getId()).login(user.getLogin())
                    .birthday(user.getBirthday()).name(user.getLogin()).build();
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновили пользователя с id={}", user.getId());
        } else {
            log.error("Пользователя с id={} еще не существует", user.getId());
            throw new ValidationException("Пользователя с id=" + user.getId() + " еще не существует");
        }
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("На данный момент сохранено пользователей: {}", users.size());
        return users.values();
    }
}
