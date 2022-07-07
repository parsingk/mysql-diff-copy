package com.lib.dbdiffcopy;

import com.lib.dbdiffcopy.database.DestinationDataBase;
import com.lib.dbdiffcopy.database.SourceDataBase;
import com.lib.dbdiffcopy.differs.RoutineDiffer;
import com.lib.dbdiffcopy.differs.TableDiffer;
import com.lib.dbdiffcopy.schema.dto.*;
import com.lib.dbdiffcopy.schema.vo.RoutineTextVO;
import com.lib.dbdiffcopy.schema.vo.RoutineVO;
import com.lib.dbdiffcopy.schema.vo.TableVO;
import com.lib.dbdiffcopy.utils.TextDiffUtil;
import lombok.NonNull;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DatabaseDiffer implements IDatabaseDiffer {

    private SourceDataBase source;
    private DestinationDataBase destination;

    private List<String> excludeRoutines = new ArrayList<>();

    private TextDiffUtil textDiff;

    DatabaseDiffer() {}

    void setSource(String databaseName, NamedParameterJdbcTemplate jdbc) {
        source = new SourceDataBase(databaseName, jdbc);
    }

    void setDestination(String databaseName, NamedParameterJdbcTemplate jdbc) {
        destination = new DestinationDataBase(databaseName, jdbc);
    }

    void setExcludeRoutines(@NonNull List<String> excludeRoutines) {
        this.excludeRoutines = excludeRoutines;
    }

    @Override
    public Tables getTableList() throws SQLException {
        source.setPrimaryKeyIfAbsent();
        List<TableVO.Table> sourceTables = source.getTableList();
        List<TableVO.Table> destinationTables = destination.getTableList();

        return TableDiffer.tableDiff(sourceTables, destinationTables);
    }

    @Override
    public SourceTableData getSourceTableData(String tableName, long last, int count) {
        TableVO.TableCount recordTotal;
        List<Map<String, Object>> sourceData;
        try {
            recordTotal = source.getTableDataCount(tableName);
            sourceData = source.getTableData(tableName, last, count);
        } catch (IncorrectResultSizeDataAccessException e) {
            recordTotal = new TableVO.TableCount();
            recordTotal.setCount(0);

            sourceData = new ArrayList<Map<String, Object>>();
        }

        try {
            destination.getTableInfo(tableName);
            List<Map<String, Object>> destData = destination.getTableData(tableName, last, count);
//            List<TableVO.SimpleTable> destTableInfo = destination.getSimpleTableInfoList(tableName);
            return TableDiffer.dataDiff(sourceData, recordTotal, destData, last, count);
        } catch (IncorrectResultSizeDataAccessException e) {
            return TableDiffer.dataDiff(sourceData, recordTotal, new ArrayList<Map<String, Object>>(), last, count);
        }
    }

    @Override
    public DestinationTableData getDestinationTableData(String tableName, long last, int count) {
        TableVO.TableCount recordTotal = destination.getTableDataCount(tableName);
        List<Map<String, Object>> data = destination.getTableData(tableName, last, count);

        return DestinationTableData.create(data, recordTotal.getCount(), last, count);
    }

    @Override
    public DataMigration upsert(String tableName) {
        List<TableVO.DetailTable> sourceInfo = source.getDetailTableInfoList(tableName);
        List<TableVO.DetailTable> destInfo = destination.getDetailTableInfoList(tableName);
        List<Map<String, Object>> sourceData = source.getTableData(tableName, 0, 0);

        return TableDiffer.dataUpsert(sourceInfo, destInfo, sourceData, destination);
    }

    @Override
    public Routines getRoutines() {
        List<RoutineVO> sourceRoutines = source.getRoutines(excludeRoutines);
        List<RoutineVO> destRoutines = destination.getRoutines(excludeRoutines);

        return RoutineDiffer.diff(sourceRoutines, destRoutines);
    }

    @Override
    public LinkedList<TextDiffUtil.Diff> getRoutinesDiffText(String routineName) {
        LinkedList<TextDiffUtil.Diff> diff;
        textDiff = new TextDiffUtil();

        RoutineTextVO sourceRoutine = source.getRoutineText(routineName);

        try {
            RoutineTextVO destinationRoutine = destination.getRoutineText(routineName);

            diff = textDiff.diff_main(destinationRoutine.getRoutine_definition(), sourceRoutine.getRoutine_definition());
        } catch (IncorrectResultSizeDataAccessException e) {
            diff = textDiff.diff_main("", sourceRoutine.getRoutine_definition());
        }

        textDiff.diff_cleanupSemantic(diff);

        return diff;
    }
}
