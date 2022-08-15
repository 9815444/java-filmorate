package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFound;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Primary
@Slf4j
public class MpaDbStorage implements MpaStorage{

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        String sqlQuery = "SELECT MPA_ID, MPA_NAME FROM MPA";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }

    @Override
    public Mpa getMpa(Integer id) {
        String sqlQuery = "SELECT MPA_ID, MPA_NAME FROM MPA WHERE MPA_ID= ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
            return mpa;
        } catch (DataAccessException e) {
            throw new MpaNotFound();
        }
    }
}
