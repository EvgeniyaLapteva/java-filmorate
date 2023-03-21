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
    private  int generateId() {
        return ++id;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        validation(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Добавили пользователя: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validation(user);
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

    public void validation(User user) {
        if (UserValidation.isEmailIsInCorrect(user.getEmail())) {
            log.error("Введен неверный формат электронной почты");
            throw new ValidationException("Введен неверный формат электронной почты");
        } else if (UserValidation.isLoginIsIncorrect(user.getLogin())) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        } else if (UserValidation.isBirthdayInFuture(user.getBirthday())) {
            log.error("День рождения не может быть в будущем");
            throw new ValidationException("День рождения не может быть в будущем");
        }
        if (UserValidation.isNameIsBlank(user.getName())) {
            user.setName(user.getLogin());
        }
    }
}
