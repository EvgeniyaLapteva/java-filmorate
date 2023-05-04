package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    private UserStorage storage;
    private UserService service;

    @BeforeEach
    public void beforeEach() {
        storage = new InMemoryUserStorage();
        service = new UserServiceImpl(storage);
    }

    private User createUser() {
        return User.builder().email("user@mail.ru").login("login")
                .birthday(LocalDate.of(1989, 7, 7)).build();
    }

    @Test
    public void shouldCreateUserWithAllData() {
        User user = createUser();
        user.setName("Name");

        User userResult = service.createUser(user);
        user.setId(1);

        assertEquals(user, userResult, "Пользователи не совпадают");
    }

    @Test
    public void shouldCreateUserWithEmptyName() {
        User user = createUser();

        User userResult = service.createUser(user);
        user.setName(user.getLogin());
        user.setId(1);

        assertEquals(user, userResult, "Пользователи не совпадают");
        assertEquals(userResult.getName(), "login", "Имя пользователя не создано из логина");
    }

    @Test
    public void shouldUpdateUser() {
        service.createUser(createUser());
        User userForUpdate = User.builder().email("update@mail.com").login("update")
                .birthday(LocalDate.of(1990, 10, 10)).build();
        userForUpdate.setId(1);

        User result = service.updateUser(userForUpdate);
        userForUpdate.setName("update");

        assertEquals(userForUpdate, result, "Пользователь не обновлен");
    }

    @Test
    public void shouldThrowExceptionWhenUpdateWithWrongID() {
        service.createUser(createUser());
        User userForUpdate = User.builder().email("update@mail.com").login("update")
                .birthday(LocalDate.of(1990, 10, 10)).build();
        userForUpdate.setId(2);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> service.updateUser(userForUpdate));

        assertEquals("Пользователя с id=" + userForUpdate.getId() + " еще не существует",
                exception.getMessage());
    }

    @Test
    public void shouldGetAllUsers() {
        User user = createUser();
        User user1 = User.builder().email("second@mail.ru").login("second")
                .birthday(LocalDate.of(2000, 1, 1)).build();
        service.createUser(user);
        service.createUser(user1);

        List<User> allUsers = service.getAllUsers();

        assertEquals(2, allUsers.size(), "Список всех пользователей недоступен");
    }

    @Test
    public void shouldAddFriends() {
        User user = createUser();
        User user1 = User.builder().email("second@mail.ru").login("second")
                .birthday(LocalDate.of(2000, 1, 1)).build();
        User userResult1 = service.createUser(user);
        User userResult2 = service.createUser(user1);

        service.addFriend(userResult1.getId(), userResult2.getId());

        assertEquals(1, userResult1.getFriendsIds().size(), "Список друзей не совпадает");
        assertEquals(1, userResult2.getFriendsIds().size(), "Список друзей не совпадает");
    }

    @Test
    public void shouldDeleteFriends() {
        User user = createUser();
        User user1 = User.builder().email("second@mail.ru").login("second")
                .birthday(LocalDate.of(2000, 1, 1)).build();
        User userResult1 = service.createUser(user);
        User userResult2 = service.createUser(user1);
        service.addFriend(userResult1.getId(), userResult2.getId());

        service.deleteFriend(userResult1.getId(), userResult2.getId());

        assertEquals(0, userResult1.getFriendsIds().size(), "Список друзей не совпадает");
        assertEquals(0, userResult2.getFriendsIds().size(), "Список друзей не совпадает");
    }

    @Test
    public void shouldGetUserById() {
        User user = createUser();
        User userExpected = service.createUser(user);

        User userResult = service.getUserById(userExpected.getId());

        assertEquals(userExpected, userResult, "По id выбран не верный пользователь");
    }

    @Test
    public void shouldGetAllFriendsById() {
        User user = createUser();
        User user1 = User.builder().email("second@mail.ru").login("second")
                .birthday(LocalDate.of(2000, 1, 1)).build();
        User user2 = User.builder().email("third@mail.ru").login("third")
                .birthday(LocalDate.of(2010, 5, 5)).build();
        User userResult1 = service.createUser(user);
        User userResult2 = service.createUser(user1);
        User userResult3 = service.createUser(user2);
        service.addFriend(userResult1.getId(), userResult2.getId());
        service.addFriend(userResult1.getId(), userResult3.getId());

        List<User> friendsById = service.getAllFriendsById(userResult1.getId());

        assertEquals(2, friendsById.size(), "Список друзей не верный");
    }

    @Test
    public void shouldGetCommonFriends() {
        User user = createUser();
        User user1 = User.builder().email("second@mail.ru").login("second")
                .birthday(LocalDate.of(2000, 1, 1)).build();
        User user2 = User.builder().email("third@mail.ru").login("third")
                .birthday(LocalDate.of(2010, 5, 5)).build();
        User userResult1 = service.createUser(user);
        User userResult2 = service.createUser(user1);
        User userResult3 = service.createUser(user2);
        service.addFriend(userResult1.getId(), userResult2.getId());
        service.addFriend(userResult1.getId(), userResult3.getId());
        service.addFriend(userResult2.getId(), userResult3.getId());
        List<User> expected = new ArrayList<>();
        expected.add(userResult3);

        List<User> commonFriends = service.getCommonFriends(userResult1.getId(), userResult2.getId());

        assertEquals(expected, commonFriends, "Списки общих друзей не совпадают");
    }
}