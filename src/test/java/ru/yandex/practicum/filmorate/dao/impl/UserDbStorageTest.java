package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void shouldCreateUserWithId() {
        User userForTest = createTestUser();
        userStorage.createUser(userForTest);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void shouldCreateUserWithEmail() {
        User userForTest = createTestUser();
        userStorage.createUser(userForTest);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "email@mail.ru")
                );
    }

    @Test
    void shouldCreateUserWithLogin() {
        User userForTest = createTestUser();
        userStorage.createUser(userForTest);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "login")
                );
    }

    @Test
    void shouldCreateUserWithName() {
        User userForTest = createTestUser();
        userStorage.createUser(userForTest);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "name")
                );
    }

    @Test
    void shouldCreateUserWithBirthday() {
        User userForTest = createTestUser();
        userStorage.createUser(userForTest);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday",
                                LocalDate.of(1989, 7, 7))
                );
    }

    @Test
    void shouldUpdateUserWithId() {
        User userForTest = createTestUser();
        userStorage.createUser(userForTest);
        User forUpdate  = userForUpdate();
        userStorage.updateUser(forUpdate);
        Optional<User> updateUser = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(updateUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void shouldUpdateUserWithEmail() {
        User userForTest = createTestUser();
        userStorage.createUser(userForTest);
        User forUpdate  = userForUpdate();
        userStorage.updateUser(forUpdate);
        Optional<User> updateUser = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(updateUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "updated@mail.ru")
                );
    }

    @Test
    void shouldUpdatedUserByLogin() {
        User userForTest = createTestUser();
        userStorage.createUser(userForTest);
        User forUpdate  = userForUpdate();
        userStorage.updateUser(forUpdate);
        Optional<User> updateUser = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(updateUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "loginUpdated")
                );
    }

    @Test
    void shouldUpdateUserWithName() {
        User userForTest = createTestUser();
        userStorage.createUser(userForTest);
        User forUpdate  = userForUpdate();
        userStorage.updateUser(forUpdate);
        Optional<User> updateUser = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(updateUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "nameNew")
                );
    }

    @Test
    void shouldUpdateUserWithBirthday() {
        User userForTest = createTestUser();
        userStorage.createUser(userForTest);
        User forUpdate  = userForUpdate();
        userStorage.updateUser(forUpdate);
        Optional<User> updateUser = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(updateUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday",
                                LocalDate.of(2000, 5, 5))
                );
    }

    @Test
    void shouldGetAllUsers() {
        User user = createTestUser();
        User user1 = userForUpdate();
        user1.setId(2);
        userStorage.createUser(user);
        userStorage.createUser(user1);

        List<User> allUsers = userStorage.getAllUsers();

        assertEquals(2, allUsers.size(), "Список пользователей не соответствует истине");
    }

    private User createTestUser() {
        return User.builder().id(1).email("email@mail.ru").login("login").name("name")
                .birthday(LocalDate.of(1989, 7, 7)).build();
    }

    private User userForUpdate() {
        return User.builder().id(1).email("updated@mail.ru").login("loginUpdated").name("nameNew")
                .birthday(LocalDate.of(2000, 5, 5)).build();
    }
}