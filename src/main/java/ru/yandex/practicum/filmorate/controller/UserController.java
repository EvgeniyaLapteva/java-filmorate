package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    public UserController(@Qualifier("dbUserServiceImpl") UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;

    @PostMapping
    public User createUser(@Valid  @RequestBody User user) {
        log.info("Запрос на создание пользователя: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя: {}", user);
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос на добавление в друзья");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос на удаление из друзей");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Запрос на получение пользователя по id={}", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriendsById(@PathVariable int id) {
        log.info("Запрос на получение списка всех друзей пользователя id={}", id);
        return userService.getAllFriendsById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFiends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Запрос на получение списка общих друзей пользователей id={} и id={}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
