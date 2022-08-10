package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;

import static ru.yandex.practicum.filmorate.service.Validator.checkUser;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {

        checkUser(user);

        String sqlQuery = "INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        Timestamp timestampBirthday = Timestamp.valueOf(user.getBirthday().atTime(LocalTime.MIDNIGHT));

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setTimestamp(4, timestampBirthday);

            return stmt;
        }, keyHolder);

        Integer newUserId = keyHolder.getKey().intValue();
        user.setId(newUserId);
        return user;

    }

    @Override
    public User update(User user) {

        User foundedUser = getUser(user.getId());

        String sqlQuery = "UPDATE USERS set " +
                          "EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                          "WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
        return user;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getTimestamp("birthday").toLocalDateTime().toLocalDate())
                .build();
    }

    @Override
    public User getUser(Integer id) {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM USERS WHERE USER_ID= ?";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
            return user;
        } catch (DataAccessException e) {
            throw new UserNotFound();
        }
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        User user = getUser(id);
        User friend = getUser(friendId);

        String sqlQuery = "insert into FRIENDSHIPS (user_id, friend_id) select #1, #2 where NOT EXISTS(SELECT USER_ID FROM FRIENDSHIPS WHERE USER_ID = #1 AND FRIEND_ID = #2)";
        sqlQuery = sqlQuery
                .replace("#1", String.valueOf(id))
                .replace("#2", String.valueOf(friendId));

        jdbcTemplate.update(sqlQuery);
        log.info("Добавили друга {}", sqlQuery);

    }

    @Override
    public void removeFriend(Integer id, Integer friendId) {
        String sqlQuery =
                "delete from FRIENDSHIPS where USER_ID = ? AND FRIEND_ID =?";
        jdbcTemplate.update(sqlQuery, id, friendId);
        log.info("Удалили друга {}", sqlQuery);
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        User user = getUser(id);
        String sqlQuery = "select * from USERS where USER_ID in (select FRIEND_ID from FRIENDSHIPS where USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        String sqlQuery =
                "select * from USERS where USER_ID in (select FRIEND_ID from FRIENDSHIPS where USER_ID = ? and FRIEND_ID in (select FRIEND_ID from FRIENDSHIPS where USER_ID = ?))";
        log.info("Получаем общий друзей {}", sqlQuery);
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, otherId);
    }


}
