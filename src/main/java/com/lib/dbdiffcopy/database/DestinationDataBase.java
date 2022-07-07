package com.lib.dbdiffcopy.database;

import com.lib.dbdiffcopy.database.adapters.RoutineAdapter;
import com.lib.dbdiffcopy.database.adapters.TableAdapter;
import com.lib.dbdiffcopy.schema.vo.RoutineTextVO;
import com.lib.dbdiffcopy.schema.vo.RoutineVO;
import com.lib.dbdiffcopy.schema.vo.TableVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

public class DestinationDataBase implements IDataBase {

    private final String databaseName;
    private final RoutineAdapter routineAdapter;

    private final TableAdapter tableAdapter;

    public DestinationDataBase(String databaseName, NamedParameterJdbcTemplate jdbc) {
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

    public List<TableVO.SimpleTable> getSimpleTableInfoList(String tableName) {
        return this.tableAdapter.getSimpleTableInfoList(databaseName, tableName);
    }

    public TableVO.TableInfo getTableInfo(String tableInfo) {
        return this.tableAdapter.getTableInfo(databaseName, tableInfo);
    }

    public boolean createTable(String tableName, Long autoIncrement, String primaryKeyStr, List<TableVO.DetailTable> columns) {
        return this.tableAdapter.createTable(databaseName, tableName, autoIncrement, primaryKeyStr, columns);
    }

    public boolean updateColumnName(String tableName, String destColumnName, String sourceColumnName, String sourceColumnType) {
        return this.tableAdapter.updateColumnName(databaseName, tableName, destColumnName, sourceColumnName, sourceColumnType);
    }

    public boolean insertColumnName(String tableName, List<TableVO.DetailTable> columns) {
        return this.tableAdapter.insertColumnName(databaseName, tableName, columns);
    }

    public boolean upsertData(String tableName, String columns, List<Map<String, Object>> source) {
        TableVO.RefineData data = this.tableAdapter.getRefineData(databaseName, tableName);
        return this.tableAdapter.insertTableData(databaseName, tableName, columns, source, data);
    }
}
