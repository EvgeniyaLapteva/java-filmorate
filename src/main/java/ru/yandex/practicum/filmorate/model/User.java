package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {

    private int id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
}
