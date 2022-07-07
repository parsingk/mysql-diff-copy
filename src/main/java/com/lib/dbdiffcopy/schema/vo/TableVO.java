package com.lib.dbdiffcopy.schema.vo;

import com.lib.dbdiffcopy.utils.ResultSetUtil;
import lombok.Data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableVO {

    @Data
    public static class Column {
        private String table_schema;
        private String table_name;
        private String column_name;

        public static Column create(ResultSet rs, int rowCount) throws SQLException {
            Column vo = new Column();

            vo.setTable_schema(ResultSetUtil.getString(rs, "table_schema"));
            vo.setTable_name(ResultSetUtil.getString(rs, "table_name"));
            vo.setColumn_name(ResultSetUtil.getString(rs, "column_name"));

            return vo;
        }
    }

    @Data
    public static class ColumnName {
        private String column_names;

        public static ColumnName create(ResultSet rs, int rowCount) throws SQLException {
            ColumnName vo = new ColumnName();

            vo.setColumn_names(ResultSetUtil.getString(rs, "column_names"));

            return vo;
        }
    }

    @Data
    public static class TableInfo {
        private String table_schema;
        private String table_name;
        private Integer table_rows;

        public static TableInfo create(ResultSet rs, int rowCount) throws SQLException {
            TableInfo vo = new TableInfo();

            vo.setTable_schema(ResultSetUtil.getString(rs, "table_schema"));
            vo.setTable_name(ResultSetUtil.getString(rs, "table_name"));
            vo.setTable_rows(ResultSetUtil.getInt(rs, "table_rows"));

            return vo;
        }
    }

    @Data
    public static class Table {
        private String table_name;
        private Integer table_rows;
        private List<String> columns = new ArrayList<>();
        private String column_hash;

        public static Table create(ResultSet rs, int rowCount) throws SQLException {
            Table vo = new Table();

            vo.setTable_name(ResultSetUtil.getString(rs, "table_name"));
            vo.setTable_rows(ResultSetUtil.getInt(rs, "table_rows"));
            vo.setColumns(Arrays.asList(ResultSetUtil.getString(rs, "columns").split(",")));
            vo.setColumn_hash(ResultSetUtil.getString(rs, "columns_hash"));

            return vo;
        }
    }

    @Data
    public static class SimpleTable {
        private String table_name;
        private String column_name;

        public static SimpleTable create(ResultSet rs, int rowCount) throws SQLException {
            SimpleTable vo = new SimpleTable();

            vo.setTable_name(ResultSetUtil.getString(rs, "table_name"));
            vo.setColumn_name(ResultSetUtil.getString(rs, "column_name"));

            return vo;
        }
    }

    @Data
    public static class TableCount {
        private Integer count;

        public static TableCount create(ResultSet rs, int rowCount) throws SQLException {
            TableCount vo = new TableCount();

            vo.setCount(ResultSetUtil.getInt(rs, "count"));

            return vo;
        }
    }

    @Data
    public static class DetailTable implements Serializable {
        private String table_name;
        private Long auto_increment;
        private Long ordinal_position;
        private String column_name;
        private String is_nullable;
        private String column_default;
        private String column_type;
        private String column_key;
        private String extra;

        public void setColumn_default(String column_default) {
            this.column_default = column_default == null ? "NULL" : column_default;
        }

        public static DetailTable create(ResultSet rs, int rowCount) throws SQLException {
            DetailTable vo = new DetailTable();

            vo.setTable_name(ResultSetUtil.getString(rs, "table_name"));
            vo.setAuto_increment(ResultSetUtil.getLong(rs, "auto_increment"));
            vo.setOrdinal_position(ResultSetUtil.getLong(rs, "ordinal_position"));
            vo.setColumn_name(ResultSetUtil.getString(rs, "column_name"));
            vo.setIs_nullable(ResultSetUtil.getString(rs, "is_nullable"));
            vo.setColumn_default(ResultSetUtil.getString(rs, "column_default"));
            vo.setColumn_type(ResultSetUtil.getString(rs, "column_type"));
            vo.setColumn_key(ResultSetUtil.getString(rs, "column_key"));
            vo.setExtra(ResultSetUtil.getString(rs, "extra"));

            return vo;
        }
    }

    @Data
    public static class RefineData {
        private String update_data;
        private String insert_data;

        public static RefineData create(ResultSet rs, int rowCount) throws SQLException {
            RefineData vo = new RefineData();

            vo.setUpdate_data(ResultSetUtil.getString(rs, "update_data"));
            vo.setInsert_data(ResultSetUtil.getString(rs, "insert_data"));

            return vo;
        }
    }
}
