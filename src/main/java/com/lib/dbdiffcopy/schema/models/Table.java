package com.lib.dbdiffcopy.schema.models;

import lombok.Data;

import java.util.List;

@Data
public class Table {
    private String table_name;
    private Integer status = 0;
    private List<Column> columns;
    private Integer deletedColumnCount = 0;
    private Integer newColumnCount = 0;

    private Integer updateColumnCount = 0;

    public Table setTable_name(String table_name) {
        this.table_name = table_name;
        return this;
    }

    public Table setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Table setColumns(List<Column> columns) {
        this.columns = columns;
        return this;
    }

    public Table setDeletedColumnCount(Integer deletedColumnCount) {
        this.deletedColumnCount = deletedColumnCount;
        return this;
    }

    public Table setNewColumnCount(Integer newColumnCount) {
        this.newColumnCount = newColumnCount;
        return this;
    }

    public Table setUpdateColumnCount(Integer updateColumnCount) {
        this.updateColumnCount = updateColumnCount;
        return this;
    }
}
