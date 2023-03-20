package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
    }

    @Test
    public void shouldCreateUser() {
        User user = User.builder().email("login@mail.ru").login("login").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
        User userResult = userController.createUser(user);
        user.setId(1);

        assertEquals(user, userResult, "Пользователи не совпадают");
    }

    @Test
    public void shouldThrowExceptionWithEmailIsBlankWhenPost() {
        User user = User.builder().email("").login("login").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Введен неверный формат электронной почты", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithEmailWithoutSpicSymbolWhenPost() {
        User user = User.builder().email("mail.ru").login("login").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Введен неверный формат электронной почты", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithLoginIsEmptyWhenPost() {
        User user = User.builder().email("login@mail.ru").login("").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithLoginWithWhiteSpacesWhenPost() {
        User user = User.builder().email("login@mail.ru").login("login name").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithBirthdateInFutureWhenPost() {
        User user = User.builder().email("login@mail.ru").login("login").name("Name")
                .birthday(LocalDate.of(2057, 7, 7)).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("День рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    public void shouldChangeNameWhenNameIsEmpty() {
        User user = User.builder().email("login@mail.ru").login("login")
                .birthday(LocalDate.of(1989, 7, 7)).build();

        User userResult = userController.createUser(user);
        user.setName(user.getLogin());

        assertEquals(user, userResult, "Имя пользователя не заменилось на логин");
    }

    @Test
    public void shouldUpdateUser() {
        User user = User.builder().email("login@mail.ru").login("login").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
        userController.createUser(user);

        User userExpected = User.builder().id(1).email("updated@mail.ru").login("loginUpdated").name("NameUpdated")
                .birthday(LocalDate.of(1989, 7, 7)).build();
        User userResult = userController.updateUser(userExpected);

        assertEquals(userExpected, userResult, "Пользователи не обновляются");
    }

    @Test
    public void shouldThrowExceptionWhenUpdateUserWithWrongId() {
        User user = User.builder().email("login@mail.ru").login("login").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
        userController.createUser(user);
        User userForUpdate = User.builder().id(555).email("updated@mail.ru").login("loginUpdated").name("NameUpdated")
                .birthday(LocalDate.of(1989, 7, 7)).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(userForUpdate));

        assertEquals("Пользователя с id=" + userForUpdate.getId() + " еще не существует",
                exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithEmailIsBlankWhenPut() {
        User user = User.builder().email("login@mail.ru").login("login").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
        userController.createUser(user);
        User userForUpdate = User.builder().id(1).email("").login("updated").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(userForUpdate));
        assertEquals("Введен неверный формат электронной почты", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithEmailWithoutSpicSymbolWhenPut() {
        User user = User.builder().email("login@mail.ru").login("login").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
        userController.createUser(user);
        User userForUpdate = User.builder().id(1).email("mail.ru").login("updated").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(userForUpdate));
        assertEquals("Введен неверный формат электронной почты", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithLoginIsEmptyWhenPut() {
        User user = User.builder().email("login@mail.ru").login("login").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
        userController.createUser(user);
        User userForUpdate = User.builder().id(1).email("login@mail.ru").login("").name("updated")
                .birthday(LocalDate.of(1989, 7, 7)).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(userForUpdate));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithLoginWithWhitespacesWhenPut() {
        User user = User.builder().email("login@mail.ru").login("login").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
        userController.createUser(user);
        User userForUpdate = User.builder().id(1).email("login@mail.ru").login("login login").name("updated")
                .birthday(LocalDate.of(1989, 7, 7)).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(userForUpdate));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithBirthdateInFutureWhenPut() {
        User user = User.builder().email("login@mail.ru").login("login").name("Name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
        userController.createUser(user);
        User userForUpdate = User.builder().id(1).email("login@mail.ru").login("login").name("updated")
                .birthday(LocalDate.of(2057, 7, 7)).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(userForUpdate));
        assertEquals("День рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    public void shouldChangeNameWhenNameIsEmptyWhenPut() {
        User user = User.builder().email("login@mail.ru").login("login").name("name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
        userController.createUser(user);
        User expectedUser = User.builder().id(1).email("login@mail.ru").login("updated")
                .birthday(LocalDate.of(1989, 7, 7)).build();

        User userResult = userController.updateUser(expectedUser);
        expectedUser.setName(expectedUser.getLogin());

        assertEquals(expectedUser, userResult, "Имя пользователя не заменилось на логин");
    }

    @Test
    public void shouldGetAllUsers() {
        User user = User.builder().email("login@mail.ru").login("login").name("name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
        User user1 = User.builder().email("email@mail.ru").login("second").name("second name")
                .birthday(LocalDate.of(2010, 10, 1)).build();
        userController.createUser(user);
        userController.createUser(user1);
        user.setId(1);
        user1.setId(2);

        Collection<User> usersResult = userController.getAllUsers();

        assertEquals(2, usersResult.size(), "Список пользователей не возвращается");
    }
}