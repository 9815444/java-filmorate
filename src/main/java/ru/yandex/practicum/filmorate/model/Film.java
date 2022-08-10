package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFound;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Primary
@Slf4j
@Builder
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private List<Genre> genres;
    private float rate;
    private Mpa mpa;

    public Film(int id, String name, String description, LocalDate releaseDate, Integer duration, List<Genre> genres, float rate, Mpa mpa, Set<Integer> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.rate = rate;
        this.mpa = mpa;
        this.likes = likes;
    }

    private Set<Integer> likes = new HashSet<>();

    public void addLike(Integer id) {
        likes.add(id);
    }

    public void removeLike(Integer id) {
        if (likes.contains(id)) {
            likes.remove(id);
        } else {
            throw new LikeNotFound();
        }
    }

    public Set<Integer> getLikes() {
        return likes;
    }
}
