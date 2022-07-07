package com.lib.dbdiffcopy.database;

import com.lib.dbdiffcopy.schema.vo.RoutineTextVO;
import com.lib.dbdiffcopy.schema.vo.RoutineVO;
import com.lib.dbdiffcopy.schema.vo.TableVO;

import java.util.List;
import java.util.Map;

public interface IDataBase {

    List<RoutineVO> getRoutines(List<String> excludeRoutines);

    RoutineTextVO getRoutineText(String routineName);

    List<TableVO.Table> getTableList();

    List<Map<String, Object>> getTableData(String tableName, long lastNum, long count);

    TableVO.TableCount getTableDataCount(String tableName);

    List<TableVO.DetailTable> getDetailTableInfoList(String tableName);
}
