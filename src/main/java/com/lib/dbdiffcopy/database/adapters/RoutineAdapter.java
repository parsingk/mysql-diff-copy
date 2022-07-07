package com.lib.dbdiffcopy.database.adapters;

import com.lib.dbdiffcopy.schema.vo.RoutineTextVO;
import com.lib.dbdiffcopy.schema.vo.RoutineVO;
import com.lib.dbdiffcopy.utils.Query;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class RoutineAdapter {

    private final Query db;

    public RoutineAdapter(NamedParameterJdbcTemplate jdbc) {
        this.db = new Query(jdbc);
    }

    public List<RoutineVO> getRoutines(String databaseName, List<String> excludeRoutines) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT ROUTINE_NAME, ROUTINE_TYPE, MD5(ROUTINE_DEFINITION) AS routine_definition")
                .append(" FROM information_schema.ROUTINES")
                .append(" WHERE ROUTINE_SCHEMA = :databaseName");

        if (excludeRoutines != null && !excludeRoutines.isEmpty()) {
            buffer.append(" AND ROUTINE_NAME NOT IN(:excludes)");
        }

        buffer.append(" ORDER BY ROUTINE_NAME");

        return this.db.selectList(buffer.toString(), RoutineVO::create, new MapSqlParameterSource()
                        .addValue("databaseName", databaseName)
                        .addValue("excludes", excludeRoutines));
    }

    public RoutineTextVO getRoutineText(String databaseName, String routineName) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT ROUTINE_DEFINITION AS routine_definition")
                .append(" FROM information_schema.ROUTINES")
                .append(" WHERE ROUTINE_SCHEMA = :databaseName AND ROUTINE_NAME = :routineName");

        return this.db.selectOne(buffer.toString(), RoutineTextVO::create, new MapSqlParameterSource()
                .addValue("databaseName", databaseName)
                .addValue("routineName", routineName));
    }
}
