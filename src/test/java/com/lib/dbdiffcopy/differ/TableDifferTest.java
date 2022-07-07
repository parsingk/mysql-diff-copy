package com.lib.dbdiffcopy.differ;

import com.lib.dbdiffcopy.differs.TableDiffer;
import com.lib.dbdiffcopy.schema.dto.Tables;
import com.lib.dbdiffcopy.schema.vo.TableVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableDifferTest {

    private List<TableVO.Table> source = new ArrayList<>();
    private List<TableVO.Table> dest = new ArrayList<>();
    private List<String> columns = new ArrayList<>();

    @Before
    public void dataSetUp() {
        TableVO.Table table;

        String column;
        for (int k = 0; k < 5; k ++) {
            column = "column" + k;
            columns.add(column);
        }

        for (int i = 0; i < 10; i ++) {
            table = new TableVO.Table();
            table.setTable_name("test" + i);
            table.setTable_rows(i);
            table.setColumn_hash(i + "c");
            table.setColumns(columns);

            source.add(table);
        }

        for (int i = 0; i < 10; i ++) {
            table = new TableVO.Table();
            table.setTable_name("test" + i);
            table.setTable_rows(i);
            table.setColumn_hash(i + "c");
            table.setColumns(columns);

            dest.add(table);
        }
    }

    @Test
    public void tableDiff() {
        Tables tables = TableDiffer.tableDiff(source, dest);

        Assert.assertEquals(Optional.ofNullable(tables.getTableAddCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(tables.getColumnAddCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(tables.getColumnUpdateCount()), Optional.of(0));
    }

    @Test
    public void tableAddCountDiff() {
        TableVO.Table t = new TableVO.Table();
        t.setTable_name("test10");
        t.setTable_rows(10);
        t.setColumns(columns);
        t.setColumn_hash("10c");

        source.add(t);

        Tables tables = TableDiffer.tableDiff(source, dest);

        Assert.assertEquals(Optional.ofNullable(tables.getTableAddCount()), Optional.of(1));
        Assert.assertEquals(Optional.ofNullable(tables.getColumnAddCount()), Optional.of(5));
        Assert.assertEquals(Optional.ofNullable(tables.getColumnUpdateCount()), Optional.of(0));
    }

    @Test
    public void columnAddCountDiff() {
        columns.add("column5");

        TableVO.Table t = new TableVO.Table();
        t.setTable_name("test10");
        t.setTable_rows(10);
        t.setColumns(columns);
        t.setColumn_hash("10c");

        source.add(t);

        Tables tables = TableDiffer.tableDiff(source, dest);

        Assert.assertEquals(Optional.ofNullable(tables.getTableAddCount()), Optional.of(1));
        Assert.assertEquals(Optional.ofNullable(tables.getColumnAddCount()), Optional.of(6));
        Assert.assertEquals(Optional.ofNullable(tables.getColumnUpdateCount()), Optional.of(0));
    }

    @Test
    public void columnUpdateCountDiff() {
        List<String> cols = new ArrayList<>();
        cols.add("col1");

        TableVO.Table t = new TableVO.Table();
        t.setTable_name("test10");
        t.setTable_rows(10);
        t.setColumns(cols);
        t.setColumn_hash("10c");

        source.add(t);

        List<String> colss = new ArrayList<>();
        colss.add("col0");

        TableVO.Table t1 = new TableVO.Table();
        t1.setTable_name("test10");
        t1.setTable_rows(10);
        t1.setColumns(colss);
        t1.setColumn_hash("10c");

        dest.add(t1);

        Tables tables = TableDiffer.tableDiff(source, dest);

        Assert.assertEquals(Optional.ofNullable(tables.getTableAddCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(tables.getColumnAddCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(tables.getColumnUpdateCount()), Optional.of(1));
    }
}
