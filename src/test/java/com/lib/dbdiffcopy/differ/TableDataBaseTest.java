package com.lib.dbdiffcopy.differ;

import com.lib.dbdiffcopy.DatabaseDiffer;
import com.lib.dbdiffcopy.DiffCopyConfig;
import com.lib.dbdiffcopy.schema.MigrationStatus;
import com.lib.dbdiffcopy.schema.dto.DataMigration;
import com.lib.dbdiffcopy.schema.dto.DestinationTableData;
import com.lib.dbdiffcopy.schema.dto.SourceTableData;
import com.lib.dbdiffcopy.schema.dto.Tables;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;
import java.util.Optional;

public class TableDataBaseTest {

    private DatabaseDiffer differ;

    @Before
    public void buildDiffer() {
        differ = new DiffCopyConfig().builder()
                .setSource("source", new NamedParameterJdbcTemplate(DataSourceBuilder.create()
                        .type(HikariDataSource.class)
                        .url("jdbc:mysql://127.0.0.1:3306/source?serverTimezone=Asia/Seoul")
                        .username("test")
                        .password("********************************")
                        .driverClassName("com.mysql.cj.jdbc.Driver")
                        .build()))
                .setDestination("dest", new NamedParameterJdbcTemplate(DataSourceBuilder.create()
                        .type(HikariDataSource.class)
                        .url("jdbc:mysql://127.0.0.1:3306/dest?serverTimezone=Asia/Seoul")
                        .username("test")
                        .password("********************************")
                        .driverClassName("com.mysql.cj.jdbc.Driver")
                        .build()))
                .build();
    }

    @Test
    public void upsert() {
        DataMigration result = differ.upsert("data_actor");

        Assert.assertEquals(Optional.ofNullable(result.getResult()), Optional.of(MigrationStatus.OK.getCode()));
    }

    @Test
    public void tableList() throws SQLException {
        Tables tables = differ.getTableList();

        Assert.assertEquals(Optional.ofNullable(tables.getTableAddCount()), Optional.of(1));
        Assert.assertEquals(Optional.ofNullable(tables.getColumnAddCount()), Optional.of(1));
        Assert.assertEquals(Optional.ofNullable(tables.getColumnUpdateCount()), Optional.of(0));

    }

    @Test
    public void getSourceTableData() {
        SourceTableData data = differ.getSourceTableData("aaa", 0, 0);

        Assert.assertEquals(Optional.ofNullable(data.getRecordTotal()), Optional.of(3));
    }

    @Test
    public void getDestinationTableData() {
        DestinationTableData data = differ.getDestinationTableData("test1", 0, 0);

        Assert.assertEquals(Optional.ofNullable(data.getRecordTotal()), Optional.of(1));
    }
}
