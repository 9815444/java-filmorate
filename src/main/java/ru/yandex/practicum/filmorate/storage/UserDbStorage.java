package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;

@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {

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

//        String sql = "INSERT INTO \"users\" ('email', 'login', 'name', 'birthday') VALUES ("
//                + "'" + user.getEmail() + "'" + ", "
//                + "'" + user.getLogin() + "'" + ", "
//                + "'" + user.getName() + "'" + ", "
//                + "'" + user.getBirthday() + "'" + ")";
//
////        jdbcTemplate.execute(sql);
////        jdbcTemplate.update("insert into \"users\" values (?, ?, ?, ?, ?)", 10, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
//        String sql2 = "INSERT INTO \"users\"\n" +
//                "(\"email\", \"login\", \"name\", \"birthday\")\n" +
//                "VALUES(?, ?, ?, ?)";
//        jdbcTemplate.update(sql2, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
//
//        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public User getUser(Integer id) {
        return null;
    }
}
