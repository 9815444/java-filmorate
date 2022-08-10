package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFound;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.service.Validator.checkFilm;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, UserStorage userStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Film create(Film film) {

        checkFilm(film);

        String sqlQuery = "insert into FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION_MIN, RATE, MPA_ID)" +
                          "values (?, ?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(film.getReleaseDate().atTime(LocalTime.MIDNIGHT)));
            stmt.setInt(4, film.getDuration());
            stmt.setFloat(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());

            return stmt;
        }, keyHolder);

        Integer newFilmId = keyHolder.getKey().intValue();
        film.setId(newFilmId);

        if (film.getMpa() != null) {
            film.setMpa(mpaStorage.getMpa(film.getMpa().getId()));
        }

        List<Genre> genres = new ArrayList<>();

        if (film.getGenres() != null) {
            String genreIds = String.join(","
                    , film.getGenres().stream().map((p) -> (String.valueOf(p.getId()))).collect(Collectors.toList())
            );
            String sqlGenreQuery = "insert into FILMS_GENRES " +
                                   "(FILM_ID, GENRE_ID) " +
                                   "select #FILM_ID, GENRE_ID from GENRES where GENRE_ID in (#GENRE_IDS)";
            sqlGenreQuery = sqlGenreQuery
                    .replace("#FILM_ID", String.valueOf(newFilmId))
                    .replace("#GENRE_IDS", genreIds);

            jdbcTemplate.update(sqlGenreQuery);
            genres.addAll(film.getGenres().stream().map((p) -> (genreStorage.getGenre(p.getId()))).collect(Collectors.toList()));
        }
        film.setGenres(genres);
        return film;
    }

    @Override
    public Film update(Film film) {

        Film foundedUser = getFilm(film.getId());

        String sqlQuery = "UPDATE FILMS set " +
                          "NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION_MIN = ?, " +
                          "RATE = ?, MPA_ID = ?" +
                          "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRate()
                , film.getMpa().getId()
                , film.getId()
        );

        if (film.getMpa() != null) {
            film.setMpa(mpaStorage.getMpa(film.getMpa().getId()));
        }

        String sqlQueryDeleteGenres =
                "delete\n" +
                "from FILMS_GENRES\n" +
                "where FILM_ID = ?";
        jdbcTemplate.update(sqlQueryDeleteGenres, film.getId());

        List<Genre> genres = new ArrayList<>();
        if (film.getGenres() != null) {

            String genreIds = String.join(","
                    , film.getGenres().stream().map((p) -> (String.valueOf(p.getId()))).collect(Collectors.toList())
            );
            String sqlGenreQuery = "insert into FILMS_GENRES " +
                                   "(FILM_ID, GENRE_ID) " +
                                   "select #FILM_ID, GENRE_ID from GENRES where GENRE_ID in (#GENRE_IDS)";
            sqlGenreQuery = sqlGenreQuery
                    .replace("#FILM_ID", String.valueOf(film.getId()))
                    .replace("#GENRE_IDS", genreIds);

            jdbcTemplate.update(sqlGenreQuery);

            genres.addAll(getFilmGenres(film.getId()));

        }

        film.setGenres(genres);

        return film;

    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "select " +
                          "FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION_MIN, RATE, MPA_ID " +
                          "from FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getTimestamp("RELEASE_DATE").toLocalDateTime().toLocalDate())
                .duration(resultSet.getInt("DURATION_MIN"))
                .rate(resultSet.getInt("RATE"))
                .mpa(mpaStorage.getMpa(resultSet.getInt("MPA_ID")))
                .genres(getFilmGenres(resultSet.getInt("FILM_ID")))
                .build();

    }

    @Override
    public Film getFilm(Integer id) {
        String sqlQuery = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            return film;
        } catch (DataAccessException e) {
            throw new FilmNotFound();
        }
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        String sqlQuery = "insert into LIKES (FILM_ID, USER_ID) " +
                          "values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public void removeLike(Integer id, Integer userId) {
        Film film = getFilm(id);
        User user = userStorage.getUser(userId);
        String sqlQuery = "delete from LIKES where FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {

        String sqlQuery =
                "select FILMS.FILM_ID, ifnull(likes, 0) as LikeCount\n" +
                "from FILMS\n" +
                "         left join\n" +
                "         (select FILM_ID, count(USER_ID) as likes from LIKES group by FILM_ID) as lks\n" +
                "         on FILMS.FILM_ID = lks.FILM_ID\n" +
                "order by ifnull(likes, 0) desc, FILMS.FILM_ID desc \n" +
                "limit ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilmById, count);
    }

    @Override
    public List<Genre> getFilmGenres(Integer id) {
        String sqlQuery = "select GENRE_ID from FILMS_GENRES where FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return genreStorage.getGenre(resultSet.getInt("GENRE_ID"));
    }

    private Film mapRowToFilmById(ResultSet resultSet, int rowNum) throws SQLException {
        return getFilm(resultSet.getInt("FILM_ID"));
    }
}
