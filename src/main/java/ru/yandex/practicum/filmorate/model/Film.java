package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
public class Film {

    private int id;
    @NotBlank
    private final String name;
    @NotNull
    @Size(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private int duration;
    private final Set<Integer> likes = new HashSet<>();
    @NotNull
    private Mpa mpa;
    private final Set<Genre> genres = new HashSet<>();
}
