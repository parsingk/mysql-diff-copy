package com.lib.dbdiffcopy.schema.models;

import lombok.Data;

@Data
public class Column {

    private String column_name;
    private Integer status = 0;
    public Column setColumn_name(String column_name) {
        this.column_name = column_name;
        return this;
    }

    public Column setStatus(Integer status) {
        this.status = status;
        return this;
    }
}
