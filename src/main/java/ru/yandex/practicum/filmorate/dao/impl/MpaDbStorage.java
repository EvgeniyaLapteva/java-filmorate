package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "select * from mpa";
        List<Mpa> allMpa = jdbcTemplate.query(sql, (rs, rowNum) -> Mpa.builder().mpaId(rs.getInt("mpa_id"))
                .name(rs.getString("name")).build());
        return allMpa.stream().sorted(Comparator.comparingInt(Mpa::getMpaId))
                .collect(Collectors.toList());
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        String sql = "select * from mpa where mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> Mpa.builder()
                .mpaId(rs.getInt("mpa_id")).name(rs.getString("name")).build(), mpaId);
    }
}
