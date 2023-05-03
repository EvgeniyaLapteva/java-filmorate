package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void addLike(int id) {
        likes.add(id);
    }

    public boolean deleteLike(int id) {
        return likes.remove(id);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public boolean deleteGenre(Genre genre) {
        return genres.remove(genre);
    }

    public List<Genre> getFilmGenres() {
        return genres.stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toList());
    }
}
