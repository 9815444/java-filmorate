package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    List<Film> getAll();

    Film getFilm(Integer id);

    void addLike(Integer id, Integer userId);

    void removeLike(Integer id, Integer userId);

    List<Film> findPopularFilms(Integer count);

    List<Genre> getFilmGenres(Integer id);
}
