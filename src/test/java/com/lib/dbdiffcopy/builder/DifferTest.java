package com.lib.dbdiffcopy.builder;

import com.lib.dbdiffcopy.DatabaseDiffer;
import com.lib.dbdiffcopy.DiffCopyConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class DifferTest {

    @Test
    public void buildDiffer() {
        DatabaseDiffer differ = new DiffCopyConfig().builder()
                .setSource("source", new NamedParameterJdbcTemplate(DataSourceBuilder.create().build()))
                .setDestination("dest", new NamedParameterJdbcTemplate(DataSourceBuilder.create().build()))
                .build();

        Assert.assertNotNull(differ);
    }
}
