package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {

    private int genreId;
    private final String name;
}