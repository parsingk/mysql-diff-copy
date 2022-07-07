package com.lib.dbdiffcopy.database;

import com.lib.dbdiffcopy.database.adapters.RoutineAdapter;
import com.lib.dbdiffcopy.database.adapters.TableAdapter;
import com.lib.dbdiffcopy.schema.vo.RoutineTextVO;
import com.lib.dbdiffcopy.schema.vo.RoutineVO;
import com.lib.dbdiffcopy.schema.vo.TableVO;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SourceDataBase implements IDataBase {

    private final String databaseName;
    private final RoutineAdapter routineAdapter;

    private final TableAdapter tableAdapter;

    public SourceDataBase(String databaseName, NamedParameterJdbcTemplate jdbc) {
        this.databaseName = databaseName;
        this.routineAdapter = new RoutineAdapter(jdbc);
        this.tableAdapter = new TableAdapter(jdbc);
    }

    @Override
    public List<RoutineVO> getRoutines(List<String> excludeRoutines) {
        return this.routineAdapter.getRoutines(databaseName, excludeRoutines);
    }

    @Override
    public RoutineTextVO getRoutineText(String routineName) {
        return this.routineAdapter.getRoutineText(databaseName, routineName);
    }

    @Override
    public List<TableVO.Table> getTableList() {
        return this.tableAdapter.getTableList(databaseName);
    }

    @Override
    public TableVO.TableCount getTableDataCount(String tableName) {
        return this.tableAdapter.getTableDataCount(databaseName, tableName);
    }

    @Override
    public List<Map<String, Object>> getTableData(String tableName, long lastNum, long count) {
        return this.tableAdapter.getTableData(databaseName, tableName, lastNum, count);
    }

    @Override
    public List<TableVO.DetailTable> getDetailTableInfoList(String tableName) {
        return this.tableAdapter.getDetailTableInfoList(databaseName, tableName);
    }

    public void setPrimaryKeyIfAbsent() throws SQLException {
        List<TableVO.Column> columns = this.tableAdapter.getColumnsInfo(databaseName);
        for (TableVO.Column column : columns) {
            if (!this.tableAdapter.setPrimaryKey(column)) {
                throw new SQLException("SET PRIMARY KEY THREW EXCEPTION");
            }
        }
    }
}
