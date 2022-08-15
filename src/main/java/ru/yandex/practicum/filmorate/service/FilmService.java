package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Component
public class FilmService {
    final private FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {

        this.filmStorage = filmStorage;
    }

    public void addLike(Integer id, Integer userId) {
        filmStorage.addLike(id, userId);
    }

    public void removeLike(Integer id, Integer userId) {
        filmStorage.removeLike(id, userId);
    }

    public List<Film> findPopularFilms(Integer count) {
        return filmStorage.findPopularFilms(count);
    }
}
