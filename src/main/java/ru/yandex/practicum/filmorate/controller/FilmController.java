package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@Slf4j
public class FilmController {

    private Map<Integer, Film> films = new HashMap<>();
    private int lastId = 0;
    private FilmStorage filmStorage;
    private FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        return filmStorage.update(film);
    }

    @GetMapping(value = "/films")
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable String id) {
        return filmStorage.getFilm(Integer.valueOf(id));
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable Map<String, String> pathVarsMap) {
        String id = pathVarsMap.get("id");
        String userId = pathVarsMap.get("userId");
        if ((id != null) && (userId != null)) {
            filmService.addLike(Integer.valueOf(id), Integer.valueOf(userId));
        }
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable Map<String, String> pathVarsMap) {
        String id = pathVarsMap.get("id");
        String userId = pathVarsMap.get("userId");
        if ((id != null) && (userId != null)) {
            filmService.removeLike(Integer.valueOf(id), Integer.valueOf(userId));
        }
    }

    @GetMapping("/films/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") String count) {
        return filmService.findPopularFilms(Integer.valueOf(count));
    }
}
