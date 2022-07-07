package com.lib.dbdiffcopy.schema.vo;

import com.lib.dbdiffcopy.utils.ResultSetUtil;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class RoutineTextVO {

    private String routine_definition;

    public static RoutineTextVO create(ResultSet rs, int rowCount) throws SQLException {
        RoutineTextVO vo = new RoutineTextVO();
        vo.setRoutine_definition(ResultSetUtil.getString(rs, "routine_definition"));

        return vo;
    }
}
