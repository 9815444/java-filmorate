package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class GenresController {

    private GenreStorage genreStorage;

    @Autowired
    public GenresController(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @GetMapping(value = "/genres")
    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    @GetMapping(value = "/genres/{id}")
    public Genre getGenre(@PathVariable String id) {
        return genreStorage.getGenre(Integer.valueOf(id));
    }
}
