package ru.yandex.practicum.filmorate.service;

import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.LocalDate;

public class Validator {

    private static void checkUserEmail(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            throw new UserValidationException("Email is not correct");
        }
    }

    private static void checkUserLogin(String login) {
        if (!(StringUtils.hasLength(login))
            || StringUtils.containsWhitespace(login)) {
            throw new UserValidationException("Login is not correct");
        }
    }

    private static void checkUserName(User user) {
        if (!(StringUtils.hasLength(user.getName()))) {
            user.setName(user.getLogin());
        }
    }

    private static void checkFilmName(Film film) {
        String name = film.getName();
        if (!StringUtils.hasLength(name)) {
            throw new FilmValidationException("Name of the film is not correct");
        }
    }

    private static void checkFilmDescription(Film film) {
        String description = film.getDescription();
        if (description.length() > 200) {
            throw new FilmValidationException("Description the film is not correct");
        }
    }

    private static void checkFilmReleaseDate(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate == null
            || releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmValidationException("Release date is not correct");
        }
    }

    private static void checkFilmDuration(Film film) {
        if (film.getDuration() < 0) {
            throw new FilmValidationException("Duration of the film cannot be negative");
        }
    }

    private static void checkFilmMpa(Film film) {
        if (film.getMpa() == null) {
            throw new FilmValidationException("Mpa is null");
        }
    }

    private static void checkUserBirthday(User user) {
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new UserValidationException("Birthday is not correct");
        }
    }

    public static void checkUser(User user) {
        checkUserEmail(user.getEmail());
        checkUserLogin(user.getLogin());
        checkUserName(user);
        checkUserBirthday(user);
    }

    public static void checkFilm(Film film) {
        checkFilmName(film);
        checkFilmDescription(film);
        checkFilmReleaseDate(film);
        checkFilmDuration(film);
        checkFilmMpa(film);
    }

}
