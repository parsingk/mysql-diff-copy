package com.lib.dbdiffcopy.differ;

import com.lib.dbdiffcopy.DatabaseDiffer;
import com.lib.dbdiffcopy.DiffCopyConfig;
import com.lib.dbdiffcopy.schema.dto.Routines;
import com.lib.dbdiffcopy.schema.dto.Tables;
import com.lib.dbdiffcopy.schema.vo.RoutineTextVO;
import com.lib.dbdiffcopy.utils.TextDiffUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class RoutineDataBaseTest {

    private DatabaseDiffer differ;
    private List<String> excludes = new ArrayList<>();

    @Before
    public void buildDiffer() {
        excludes.add("PROC_TEST");
        differ = new DiffCopyConfig().builder()
                .setSource("source", new NamedParameterJdbcTemplate(DataSourceBuilder.create()
                        .type(HikariDataSource.class)
                        .url("jdbc:mysql://127.0.0.1:3306/source?serverTimezone=Asia/Seoul")
                        .username("root")
                        .password("1q2w3e!!")
                        .driverClassName("com.mysql.cj.jdbc.Driver")
                        .build()))
                .setDestination("dest", new NamedParameterJdbcTemplate(DataSourceBuilder.create()
                        .type(HikariDataSource.class)
                        .url("jdbc:mysql://127.0.0.1:3306/dest?serverTimezone=Asia/Seoul")
                        .username("root")
                        .password("1q2w3e!!")
                        .driverClassName("com.mysql.cj.jdbc.Driver")
                        .build()))
                .setExcludeRoutines(excludes)
                .build();
    }

    @Test
    public void getRoutines() throws SQLException {
        Routines routines = differ.getRoutines();
        excludes.add("PROC_TEST");
        Assert.assertEquals(Optional.ofNullable(routines.getNewProcedureCount()), Optional.of(1));
        Assert.assertEquals(Optional.ofNullable(routines.getNewFunctionCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedProcedureCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedFunctionCount()), Optional.of(0));

    }

    @Test
    public void getRoutineDiffText() throws SQLException {
        LinkedList<TextDiffUtil.Diff> text = differ.getRoutinesDiffText("PROC_TEST");
        System.out.println(text);
    }
}
