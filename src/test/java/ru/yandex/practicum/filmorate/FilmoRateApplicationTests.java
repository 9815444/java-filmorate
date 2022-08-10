package ru.yandex.practicum.filmorate;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import org.junit.jupiter.api.Assertions;

import java.security.PublicKey;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Data
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private UserController userController;

    public void clearData() {
        jdbcTemplate.update("delete from FRIENDSHIPS");
        jdbcTemplate.update("delete from USERS");
    }

    //Users

    @Test
    public void testCreateUser() {

        User user = new User(0, "email@email.ru", "login", "name", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user);

        User obtainedUser = userStorage.getUser(1);
        assertEquals(1, obtainedUser.getId());
    }

    @Test
    public void testGetUser() {

        User user = new User(0, "email@email.ru", "login", "name", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user);

        User obtainedUser = userStorage.getUser(1);
        assertEquals(1, obtainedUser.getId());
    }

    @Test
    public void testUpdateUser() {

        clearData();

        User user = new User(0, "email@email.ru", "login", "name", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user);

        User updatedUser = new User(1, "email@email.ru", "login2", "name2", LocalDate.of(1980, 1, 10), null);
        userStorage.update(updatedUser);

        User obtainedUser = userStorage.getUser(1);
        assertEquals(1, obtainedUser.getId());
        assertEquals("email@email.ru", obtainedUser.getEmail());
        assertEquals("login2", obtainedUser.getLogin());
    }

    @Test
    public void restGetAll() {
        User user1 = new User(0, "email@email.ru", "login", "name", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user1);

        User user2 = new User(0, "email2@email.ru", "login2", "name2", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user2);

        List<User> userList = userStorage.getAll();

        assertEquals(1, userList.get(0).getId());
        assertEquals(2, userList.get(1).getId());

        assertEquals("login", userList.get(0).getLogin());
        assertEquals("login2", userList.get(1).getLogin());

        assertEquals("name", userList.get(0).getName());
        assertEquals("name2", userList.get(1).getName());
    }

    @Test
    public void testAddFriend() {

        User user1 = new User(0, "email@email.ru", "login", "name", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user1);

        User user2 = new User(0, "email2@email.ru", "login2", "name2", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user2);

        userStorage.addFriend(1, 2);

        List<User> user1Friends = userStorage.getUserFriends(1);

        assertEquals(2, user1Friends.get(0).getId());

    }

    @Test
    public void testRemoveFriend() {

        User user1 = new User(0, "email@email.ru", "login", "name", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user1);

        User user2 = new User(0, "email2@email.ru", "login2", "name2", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user2);

        userStorage.addFriend(1, 2);

        List<User> user1Friends = userStorage.getUserFriends(1);

        assertEquals(2, user1Friends.get(0).getId());

        userStorage.removeFriend(1, 2);

        List<User> user1FriendsBeforeRemove = userStorage.getUserFriends(1);

        assertTrue(user1FriendsBeforeRemove.isEmpty());

    }

    @Test
    public void testGetUserFriends() {

        User user1 = new User(0, "email@email.ru", "login", "name", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user1);

        User user2 = new User(0, "email2@email.ru", "login2", "name2", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user2);

        userStorage.addFriend(1, 2);

        List<User> user1Friends = userStorage.getUserFriends(1);

        assertEquals(2, user1Friends.get(0).getId());
        assertEquals(1, user1Friends.size());

    }

    @Test
    public void testGetCommonFriends() {

        User user1 = new User(0, "email@email.ru", "login", "name", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user1);

        User user2 = new User(0, "email2@email.ru", "login2", "name2", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user2);

        User user3 = new User(0, "email2@email.ru", "login3", "name2", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user3);

        userStorage.addFriend(1, 2);
        userStorage.addFriend(3, 2);

        List<User> user1Friends = userStorage.getCommonFriends(1, 3);

        assertEquals(2, user1Friends.get(0).getId());
        assertEquals(1, user1Friends.size());

    }

    //MPA

    @Test
    public void testGetMpa() {
        Mpa mpa = mpaStorage.getMpa(1);
        assertEquals("G", mpa.getName());
    }

    @Test
    public void testGetAllMpa() {
        assertEquals("G", mpaStorage.getAll().get(0).getName());
    }

    @Test
    public void testGetGenre() {
        assertEquals("Комедия", genreStorage.getGenre(1).getName());
    }

    @Test
    public void testGetAllGenre() {
        assertEquals("Комедия", genreStorage.getAll().get(0).getName());
    }

    //Films
    @Test
    public void testCreateFilm() {
        Film film = new Film(0, "name", "description", LocalDate.of(1998, 1, 1), 100, null, 7, mpaStorage.getMpa(1), null);
        filmDbStorage.create(film);
        assertEquals("name", filmDbStorage.getFilm(1).getName());
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film(0, "name", "description", LocalDate.of(1998, 1, 1), 100, null, 7, mpaStorage.getMpa(1), null);
        filmDbStorage.create(film);
        Film updatedFilm = new Film(1, "name1", "description", LocalDate.of(1998, 1, 1), 100, null, 7, mpaStorage.getMpa(1), null);
        filmDbStorage.update(updatedFilm);
        assertEquals("name1", filmDbStorage.getFilm(1).getName());
    }

    @Test
    public void testGetAllFilms() {
        Film film = new Film(0, "name", "description", LocalDate.of(1998, 1, 1), 100, null, 7, mpaStorage.getMpa(1), null);
        filmDbStorage.create(film);
        assertEquals("name", filmDbStorage.getAll().get(0).getName());
    }

    @Test
    public void testGetFilm() {
        Film film = new Film(0, "name", "description", LocalDate.of(1998, 1, 1), 100, null, 7, mpaStorage.getMpa(1), null);
        filmDbStorage.create(film);
        assertEquals("name", filmDbStorage.getFilm(1).getName());
    }

    @Test
    public void testAddLike() {
        User user1 = new User(0, "email@email.ru", "login", "name", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user1);
        Film film = new Film(0, "name1", "description", LocalDate.of(1998, 1, 1), 100, null, 7, mpaStorage.getMpa(1), null);
        filmDbStorage.create(film);
        film = new Film(0, "name2", "description", LocalDate.of(1998, 1, 1), 100, null, 7, mpaStorage.getMpa(1), null);
        filmDbStorage.create(film);
        filmDbStorage.addLike(2, 1);

        assertEquals("name2", filmDbStorage.findPopularFilms(10).get(0).getName());
    }

    @Test
    public void testRemoveLike() {

        User user1 = new User(0, "email@email.ru", "login", "name", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user1);
        Film film = new Film(0, "name1", "description", LocalDate.of(1998, 1, 1), 100, null, 7, mpaStorage.getMpa(1), null);
        filmDbStorage.create(film);
        film = new Film(0, "name2", "description", LocalDate.of(1998, 1, 1), 100, null, 7, mpaStorage.getMpa(1), null);
        filmDbStorage.create(film);
        filmDbStorage.addLike(2, 1);
        filmDbStorage.removeLike(2, 1);

    }

    @Test
    public void testfindPopularFilms() {
        User user1 = new User(0, "email@email.ru", "login", "name", LocalDate.of(1980, 1, 10), null);
        userStorage.create(user1);
        Film film = new Film(0, "name1", "description", LocalDate.of(1998, 1, 1), 100, null, 7, mpaStorage.getMpa(1), null);
        filmDbStorage.create(film);
        film = new Film(0, "name2", "description", LocalDate.of(1998, 1, 1), 100, null, 7, mpaStorage.getMpa(1), null);
        filmDbStorage.create(film);
        filmDbStorage.addLike(2, 1);

        assertEquals("name2", filmDbStorage.findPopularFilms(10).get(0).getName());
    }

    @Test
    public void testGetFilmGenres() {
        Film film = new Film(0, "name1", "description", LocalDate.of(1998, 1, 1), 100, List.of(new Genre(1, "Комедия")), 7, mpaStorage.getMpa(1), null);
        filmDbStorage.create(film);

        assertEquals("Комедия", filmDbStorage.getFilmGenres(1).get(0).getName());
    }

}
