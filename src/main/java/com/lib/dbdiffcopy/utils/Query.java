package com.lib.dbdiffcopy.utils;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

public class Query {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public Query(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(Objects.requireNonNull(jdbcTemplate.getJdbcTemplate().getDataSource()));
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public <T> List<T> selectList(String sql, RowMapper<T> rowMapper, SqlParameterSource args) {
        return jdbcTemplate.query(sql, args, rowMapper);
    }

    public <T> T selectOne(String sql, RowMapper<T> rowMapper, SqlParameterSource args) {
        return jdbcTemplate.queryForObject(sql, args, rowMapper);
    }

    public <T> T selectOne(String sql, Class<T> classType, SqlParameterSource args) {
        return jdbcTemplate.queryForObject(sql, args, classType);
    }

    public List<Map<String, Object>> selectList(String sql, SqlParameterSource params) {
        return jdbcTemplate.queryForList(sql, params);
    }

    public int update(String sql, SqlParameterSource args) {
        return jdbcTemplate.update(sql, args);
    }

    public TransactionTemplate transaction() {
        return this.transactionTemplate;
    }
}
