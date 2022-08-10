package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmStorage {

    public Film create(Film film);

    public Film update(Film film);

    public List<Film> getAll();

    public Film getFilm(Integer id);

    public void addLike(Integer id, Integer userId);

    public void removeLike(Integer id, Integer userId);

    public List<Film> findPopularFilms(Integer count);

    public List<Genre> getFilmGenres(Integer id);
}
