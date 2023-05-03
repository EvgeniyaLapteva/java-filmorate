package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "select * from mpa order by mpa_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Mpa.builder().id(rs.getInt("mpa_id"))
                .name(rs.getString("name")).build());
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        String sql = "select * from mpa where mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> Mpa.builder()
                    .id(rs.getInt("mpa_id")).name(rs.getString("name")).build(), mpaId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Mpa с id={} не найден", mpaId);
            throw new ObjectNotFoundException("Mpa с id = " + mpaId + " не найден");
        }
    }
}
