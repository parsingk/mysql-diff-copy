package com.lib.dbdiffcopy.database.adapters;

import com.google.gson.Gson;
import com.lib.dbdiffcopy.schema.vo.TableVO;
import com.lib.dbdiffcopy.utils.Query;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.util.List;
import java.util.Map;

public class TableAdapter {

    private final Query db;

    public TableAdapter(NamedParameterJdbcTemplate jdbc) {
        this.db = new Query(jdbc);
    }

    public List<TableVO.Column> getColumnsInfo(String databaseName) {
        String query = "SELECT TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME" +
                " FROM information_schema.Columns" +
                " WHERE TABLE_SCHEMA = :databaseName AND ORDINAL_POSITION = 1 AND COLUMN_KEY != 'PRI'";


        return this.db.selectList(query, TableVO.Column::create, new MapSqlParameterSource().addValue("databaseName", databaseName));
    }

    public TableVO.TableInfo getTableInfo(String databaseName, String tableName) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT TABLE_SCHEMA, TABLE_NAME, TABLE_ROWS FROM information_schema.TABLES WHERE TABLE_SCHEMA = :databaseName AND TABLE_NAME = :tableName");

        return this.db.selectOne(buffer.toString(), TableVO.TableInfo::create, new MapSqlParameterSource()
                .addValue("databaseName", databaseName)
                .addValue("tableName", tableName)
        );
    }

    public List<TableVO.Table> getTableList(String databaseName) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT GROUP_CONCAT(table_sql separator ' UNION ALL ') AS qs FROM (")
                .append("SELECT CONCAT('SELECT ''', C.`TABLE_NAME`, ''' AS `table_name`, COUNT(*) AS table_rows, '''")
                .append(", GROUP_CONCAT(C.`COLUMN_NAME` ORDER BY C.ORDINAL_POSITION), ''' AS `columns`, '")
                .append(", 'MD5(GROUP_CONCAT(' ,GROUP_CONCAT(CONCAT('QUOTE(`',C.`COLUMN_NAME`,'`)') ORDER BY C.ORDINAL_POSITION ), ')) AS columns_hash FROM ', T.TABLE_SCHEMA, '.',T.`TABLE_NAME`) AS table_sql")
                .append(" FROM information_schema.TABLES T")
                .append(" INNER JOIN information_schema.Columns C ON T.TABLE_SCHEMA = C.TABLE_SCHEMA")
                .append(" WHERE T.TABLE_SCHEMA = :databaseName AND T.`TABLE_NAME` = C.`TABLE_NAME`")
                .append(" GROUP BY T.`table_name`")
                .append(" ORDER BY T.`table_name`")
                .append(") AA");

        String query = this.db.selectOne(buffer.toString(), String.class, new MapSqlParameterSource().addValue("databaseName", databaseName));

        return this.db.selectList(query, TableVO.Table::create, new MapSqlParameterSource());
    }

    public List<TableVO.SimpleTable> getSimpleTableInfoList(String databaseName, String tableName) {
        String query = "SELECT C.`TABLE_NAME`, C.`COLUMN_NAME`" +
                " FROM information_schema.TABLES T" +
                " INNER JOIN information_schema.Columns C ON T.TABLE_SCHEMA = C.TABLE_SCHEMA AND C.`TABLE_NAME` = T.`TABLE_NAME`" +
                " WHERE T.TABLE_SCHEMA = :databaseName AND C.`TABLE_NAME` = :tableName ORDER BY T.`TABLE_NAME`, C.ORDINAL_POSITION";

        return this.db.selectList(query, TableVO.SimpleTable::create, new MapSqlParameterSource().addValue("databaseName", databaseName).addValue("tableName", tableName));
    }

    public List<TableVO.DetailTable> getDetailTableInfoList(String databaseName, String tableName) {
        String query = "SELECT " +
                " T.TABLE_NAME, " +
                " T.AUTO_INCREMENT," +
                " C.ORDINAL_POSITION, " +
                " C.COLUMN_NAME, " +
                " C.IS_NULLABLE, " +
                " C.COLUMN_DEFAULT, " +
                " C.COLUMN_TYPE, " +
                " C.COLUMN_KEY, " +
                " C.EXTRA " +
                "FROM information_schema.TABLES T" +
                " INNER JOIN information_schema.COLUMNS C ON T.TABLE_NAME = C.TABLE_NAME AND T.TABLE_SCHEMA = C.TABLE_SCHEMA" +
                " WHERE T.TABLE_SCHEMA = :databaseName AND T.TABLE_NAME = :tableName" +
                " ORDER BY C.ORDINAL_POSITION";

        return this.db.selectList(query, TableVO.DetailTable::create, new MapSqlParameterSource()
                .addValue("databaseName", databaseName)
                .addValue("tableName", tableName)
        );
    }

    public TableVO.TableCount getTableDataCount(String databaseName, String tableName) {
        String query = String.format("SELECT COUNT(*) AS count FROM %s", databaseName + "." + tableName);

        return this.db.selectOne(query, TableVO.TableCount::create, new MapSqlParameterSource());
    }
    private TableVO.ColumnName getColumnNames(String databaseName, String tableName) {
        String query = "SELECT GROUP_CONCAT(IF(" +
                "C.DATA_TYPE = 'datetime'," +
                " CONCAT('DATE_FORMAT(`',C.`COLUMN_NAME`,'`, ''%Y-%m-%d %H:%i:%s'') AS', '`',C.`COLUMN_NAME`,'`')," +
                " CONCAT('`',C.`COLUMN_NAME`,'`'))) AS column_names" +
                " FROM information_schema.Columns C" +
                " WHERE C.TABLE_SCHEMA = :databaseName AND C.`TABLE_NAME` = :tableName ORDER BY C.ORDINAL_POSITION";

        return this.db.selectOne(query, TableVO.ColumnName::create, new MapSqlParameterSource()
                .addValue("databaseName", databaseName)
                .addValue("tableName", tableName)
        );
    }

    public List<Map<String, Object>> getTableData(String databaseName, String tableName, long lastNum, long count) {
        TableVO.ColumnName columnName = this.getColumnNames(databaseName, tableName);

        StringBuffer buffer = new StringBuffer();
        if(count > 0) {
            buffer.append("SELECT ")
                    .append(columnName.getColumn_names() + " ")
                    .append("FROM " + databaseName + "." + tableName + " ")
                    .append("LIMIT :last, :count");

            return this.db.selectList(buffer.toString(), new MapSqlParameterSource()
                    .addValue("last", lastNum)
                    .addValue("count", count)

            );
        } else {
            buffer.append("SELECT ")
                    .append(columnName.getColumn_names() + " ")
                    .append("FROM " + databaseName + "." + tableName + " ");

            return this.db.selectList(buffer.toString(), new MapSqlParameterSource());
        }
    }

    private List<Map<String, Object>> getCreateTableColumns(List<TableVO.DetailTable> columns) {
        Gson gson = new Gson();

        String query = "SELECT" +
                " GROUP_CONCAT('`', U.name, '` ', U.type, ' ', IF(U.isNullable = 'NO', 'NOT NULL', IF(U.col_default IS NULL, '', CONCAT('DEFAULT ', U.col_default))), ' ', U.col_extra) AS col" +
                " FROM (SELECT" +
                " JSON_UNQUOTE(JSON_EXTRACT(:columns, CONCAT('$[', T.id - 1 ,'].column_name'))) AS `name`," +
                " JSON_UNQUOTE(JSON_EXTRACT(:columns, CONCAT('$[', T.id - 1 ,'].column_type'))) AS `type`," +
                " JSON_UNQUOTE(JSON_EXTRACT(:columns, CONCAT('$[', T.id - 1 ,'].is_nullable'))) AS `isNullable`," +
                " JSON_UNQUOTE(JSON_EXTRACT(:columns, CONCAT('$[', T.id - 1 ,'].column_default'))) AS `col_default`," +
                " JSON_UNQUOTE(JSON_EXTRACT(:columns, CONCAT('$[', T.id - 1 ,'].column_key'))) AS col_key," +
                " JSON_UNQUOTE(JSON_EXTRACT(:columns, CONCAT('$[', T.id - 1 ,'].extra'))) AS col_extra" +
                " FROM tally T" +
                " WHERE T.id <= JSON_LENGTH(:columns)) U";

        return this.db.selectList(query, new MapSqlParameterSource()
                .addValue("columns", gson.toJson(columns))
        );
    }

    public boolean setPrimaryKey(TableVO.Column column) {
        String tableName = column.getTable_schema() + "." + column.getTable_name();
        String query = String.format("ALTER TABLE %s ADD PRIMARY KEY (%s)", tableName, column.getColumn_name());

        return Boolean.TRUE.equals(this.db.transaction().execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                boolean result = true;
                try {
                    db.update(query, new MapSqlParameterSource());
                } catch (Exception e) {
                    status.setRollbackOnly();
                    result = false;
                }
                return result;
            }
        }));
    }

    public boolean createTable(String databaseName, String tableName, Long autoIncrement, String primaryKeyStr, List<TableVO.DetailTable> columns) {
        List<Map<String, Object>> columnQuery = this.getCreateTableColumns(columns);

        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format("CREATE TABLE %s ( ", databaseName + "." + tableName));

        buffer.append(columnQuery.get(0).get("col"));

        if (primaryKeyStr != null && !primaryKeyStr.isEmpty()) {
            buffer.append(",").append(primaryKeyStr).append(") ");
        } else {
            buffer.append(") ");
        }

        buffer.append("ENGINE=InnoDB, ");

        if (autoIncrement != null) {
            buffer.append("AUTO_INCREMENT=:autoIncrement, ");
        }

        buffer.append("DEFAULT CHARSET='utf8mb4'");

        Gson gson = new Gson();

        return Boolean.TRUE.equals(this.db.transaction().execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                boolean result = true;
                try {
                    db.update(buffer.toString(), new MapSqlParameterSource()
                            .addValue("databaseName", databaseName)
                            .addValue("tableName", tableName)
                            .addValue("autoIncrement", autoIncrement)
                            .addValue("primaryKeyStr", primaryKeyStr)
                    );
                } catch (Exception e) {
                    status.setRollbackOnly();
                    result = false;
                }
                return result;
            }
        }));
    }

    public boolean updateColumnName(String databaseName, String tableName, String destColumnName, String sourceColumnName, String sourceColumnType) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format("ALTER TABLE %s ", databaseName + "." + tableName))
                .append(String.format("CHANGE COLUMN %s %s %s", destColumnName, sourceColumnName, sourceColumnType));

        return Boolean.TRUE.equals(this.db.transaction().execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                boolean result = true;
                try {
                    db.update(buffer.toString(), new MapSqlParameterSource()
                            .addValue("destColumnName", destColumnName)
                            .addValue("sourceColumnName", sourceColumnName)
                            .addValue("sourceColumnType", sourceColumnType)
                    );
                } catch (Exception e) {
                    status.setRollbackOnly();
                    result = false;
                }
                return result;
            }
        }));
    }

    private String getAlterColumnString(String json) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(SELECT GROUP_CONCAT('ADD `', ")
                .append(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s, CONCAT('$[', T.id - 1 ,'].column_name'))), '` ', ", json))
                .append(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s, CONCAT('$[', T.id - 1 ,'].column_type'))), ", json))
                .append(String.format("IF(JSON_UNQUOTE(JSON_EXTRACT(%s, CONCAT('$[', T.id - 1 ,'].is_nullable'))) = 'NO', ' NOT NULL', ' NULL'), ", json))
                .append(String.format("' DEFAULT ', JSON_UNQUOTE(JSON_EXTRACT(%s, CONCAT('$[', T.id - 1 ,'].column_default')))", json))
                .append(") FROM tally T ")
                .append(String.format("WHERE T.id <= JSON_LENGTH(%s))", json));

        return this.db.selectOne(buffer.toString(), String.class, new MapSqlParameterSource());
    }

    public boolean insertColumnName(String databaseName, String tableName, List<TableVO.DetailTable> columns) {
        StringBuffer buffer = new StringBuffer();
        Gson gson = new Gson();
        String json = "'" + gson.toJson(columns) + "'";
        String query = this.getAlterColumnString(json);
        buffer.append(String.format("ALTER TABLE %s ", databaseName + "." + tableName))
                .append(query);

        return Boolean.TRUE.equals(this.db.transaction().execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                boolean result = true;
                try {
                    db.update(buffer.toString(), new MapSqlParameterSource());
                } catch (Exception e) {
                    status.setRollbackOnly();
                    result = false;
                }
                return result;
            }
        }));
    }

    public TableVO.RefineData getRefineData(String databaseName, String tableName) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT GROUP_CONCAT(CONCAT('`',C.`COLUMN_NAME`,'` = JSON_UNQUOTE(JSON_EXTRACT(")
                .append("%s")
                .append(",  CONCAT(''$['', T.id - 1, ''].', C.`COLUMN_NAME`,''')))')) AS update_data,")
                .append(" GROUP_CONCAT(CONCAT('JSON_UNQUOTE(JSON_EXTRACT(")
                .append("%s")
                .append(",  CONCAT(''$['', T.id - 1, ''].', C.`COLUMN_NAME`,''')))')) AS insert_data")
                .append(" FROM information_schema.Columns C")
                .append(" WHERE C.TABLE_SCHEMA = :databaseName AND C.`TABLE_NAME` = :tableName  ORDER BY C.ORDINAL_POSITION");

        return this.db.selectOne(buffer.toString(), TableVO.RefineData::create, new MapSqlParameterSource()
                .addValue("databaseName", databaseName)
                .addValue("tableName", tableName)
        );
    }

    public boolean insertTableData(String databaseName, String tableName, String columns, List<Map<String, Object>> source, TableVO.RefineData data) {
        Gson gson = new Gson();
        columns = columns.replace("[", "");
        columns = columns.replace("]", "");

        String json = "'" + gson.toJson(source) + "'";
        String insertData = data.getInsert_data().replaceAll("%s", json);
        String updateData = data.getUpdate_data().replaceAll("%s", json);
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format("INSERT INTO %s (", databaseName + "." + tableName))
                .append(columns.toString())
                .append(")")
                .append(" SELECT ")
                .append(insertData).append(" FROM tally T WHERE T.id <= JSON_LENGTH(")
                .append(json).append(") ON DUPLICATE KEY UPDATE ").append(updateData);

        return Boolean.TRUE.equals(this.db.transaction().execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                boolean result = true;
                try {
                    db.update(buffer.toString(), new MapSqlParameterSource());
                } catch (Exception e) {
                    status.setRollbackOnly();
                    result = false;
                }
                return result;
            }
        }));
    }
}
