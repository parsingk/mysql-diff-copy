package com.lib.dbdiffcopy.schema.vo;

import com.lib.dbdiffcopy.utils.ResultSetUtil;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class RoutineVO {

    private String routine_name;
    private String routine_type;
    private String routine_definition;

    public static RoutineVO create(ResultSet rs, int rowCount) throws SQLException {
        RoutineVO vo = new RoutineVO();
        vo.setRoutine_name(ResultSetUtil.getString(rs, "routine_name"));
        vo.setRoutine_type(ResultSetUtil.getString(rs, "routine_type"));
        vo.setRoutine_definition(ResultSetUtil.getString(rs, "routine_definition"));

        return vo;
    }

    public static boolean isProcedure(String routine_type) {
        return routine_type.equalsIgnoreCase("PROCEDURE");
    }

    public static boolean isFunction(String routine_type) {
        return routine_type.equalsIgnoreCase("FUNCTION");
    }
}
