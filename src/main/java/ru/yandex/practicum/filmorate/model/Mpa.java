package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Mpa {
    private int mpaId;
    private final String name;
    private final String description;
}
