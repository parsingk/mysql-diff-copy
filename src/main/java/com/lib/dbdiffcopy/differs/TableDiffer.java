package com.lib.dbdiffcopy.differs;

import com.lib.dbdiffcopy.database.DestinationDataBase;
import com.lib.dbdiffcopy.schema.DataStatus;
import com.lib.dbdiffcopy.schema.MigrationStatus;
import com.lib.dbdiffcopy.schema.dto.DataMigration;
import com.lib.dbdiffcopy.schema.dto.SourceTableData;
import com.lib.dbdiffcopy.schema.dto.Tables;
import com.lib.dbdiffcopy.schema.models.Column;
import com.lib.dbdiffcopy.schema.models.Table;
import com.lib.dbdiffcopy.schema.vo.TableVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TableDiffer {

    public static Tables tableDiff(List<TableVO.Table> sourceTables, List<TableVO.Table> destinationTables) {

        Map<String, List<TableVO.Table>> destMap = destinationTables.stream()
                .collect(Collectors.groupingBy(TableVO.Table::getTable_name));

        AtomicInteger tableAdd = new AtomicInteger(0);
        AtomicInteger totalColumnUpdateCount = new AtomicInteger(0);
        AtomicInteger totalColumnAddCount = new AtomicInteger(0);
        AtomicInteger columnUpdateCount = new AtomicInteger(0);
        AtomicInteger columnAddCount = new AtomicInteger(0);

        AtomicReference<Table> table = new AtomicReference<>();
        AtomicReference<List<TableVO.Table>> destList = new AtomicReference<>();
        AtomicReference<TableVO.Table> dest = new AtomicReference<>();
        AtomicReference<Column> col = new AtomicReference<>();
        List<Table> sourceTableList = sourceTables.stream().map(source -> {
            table.set(new Table());
            destList.set(destMap.getOrDefault(source.getTable_name(), new ArrayList<>()));

            if (destList.get().isEmpty()) {
                dest.set(new TableVO.Table());
                table.set(table.get().setStatus(DataStatus.NEW.getStatus()));
                tableAdd.incrementAndGet();
            } else {
                dest.set(destList.get().get(0));
                if (!source.getColumn_hash().equals(dest.get().getColumn_hash()) || !source.getTable_rows().equals(dest.get().getTable_rows())) {
                    table.set(table.get().setStatus(DataStatus.MODIFY.getStatus()));        // 데이터가 다른 경우
                }
            }


            List<Column> columns = new ArrayList<Column>();
            List<String> destColumns = dest.get().getColumns();
            String columnName;
            String destColumnName;
            Column column;
            for (int i = 0; i < source.getColumns().size(); i++) {
                columnName = source.getColumns().get(i);
                column = new Column().setColumn_name(columnName);

                if (!destColumns.contains(columnName)) {        // 테이블 내에 같은 컬럼이 없는 경우
                    if (table.get().getStatus() == 0) {
                        table.set(table.get().setStatus(DataStatus.MODIFY.getStatus()));
                    }

                    try {
                        destColumnName = destColumns.get(i);
                    } catch (IndexOutOfBoundsException e) {
                        column.setStatus(DataStatus.NEW.getStatus());
                        totalColumnAddCount.incrementAndGet();
                        columnAddCount.incrementAndGet();

                        columns.add(column);
                        continue;
                    }

                    if (!destColumnName.equalsIgnoreCase(columnName)) {
                        column.setStatus(DataStatus.MODIFY.getStatus());
                        totalColumnUpdateCount.incrementAndGet();
                        columnUpdateCount.incrementAndGet();
                    }
                }

                columns.add(column);
            }

            int columnDeleteCount = 0;
            for (int j = 0; j < destColumns.size(); j++) {
                try {
                    source.getColumns().get(j);
                } catch (IndexOutOfBoundsException e) {
                    column = new Column().setColumn_name(destColumns.get(j));
                    column.setStatus(DataStatus.DELETE.getStatus());
                    columnDeleteCount++;

                    columns.add(column);
                }
            }

            table.set(table.get().setTable_name(source.getTable_name()));
            table.set(table.get().setColumns(columns));
            table.set(table.get().setNewColumnCount(columnAddCount.get()));
            table.set(table.get().setUpdateColumnCount(columnUpdateCount.get()));
            table.set(table.get().setDeletedColumnCount(columnDeleteCount));

            columnAddCount.set(0);
            columnUpdateCount.set(0);
            columnDeleteCount = 0;

            return table.get();
        }).collect(Collectors.toList());


        List<Table> destinationTableList = destinationTables.stream().map((t) -> {
            Table liveTable = new Table();
            liveTable.setTable_name(t.getTable_name());
            List<Column> columns = new ArrayList<>();
            for (String columnName : t.getColumns()) {
                columns.add(new Column().setColumn_name(columnName));
            }

            liveTable.setColumns(columns);
            return liveTable;
        }).collect(Collectors.toList());

        return Tables.create(sourceTableList, destinationTableList, tableAdd.get(), totalColumnUpdateCount.get(), totalColumnAddCount.get());
    }

    public static SourceTableData dataDiff(List<Map<String, Object>> sourceData, TableVO.TableCount sourceRecordTotal,
                                           List<Map<String, Object>> destinationData,
                                           long last, long count) {

        String status = "status";
        int index = 0;
        int insertTotal = 0;
        Map<String, Object> destData;
        Object columnData;
        List<Integer> concurrentIndex = new ArrayList();
        List<String> updatedColumns;
        List<String> newColumns;
        List<String> deletedColumns = new ArrayList<>();
        for (Map<String, Object> data : sourceData) {
            updatedColumns = new ArrayList<>();
            newColumns = new ArrayList<>();
            if(destinationData == null || destinationData.isEmpty()) {
                data.put(status, DataStatus.NEW.getStatus());
                insertTotal++;
                continue;
            }

            if(!destinationData.contains(data)) {
                if(destinationData.size() - 1 < index) {
                    data.put(status, DataStatus.NEW.getStatus());
                    insertTotal++;
                    continue;
                }

                destData = destinationData.get(index);
                List<String> sourceKeys = new ArrayList<>(data.keySet());
                List<String> destKeys = new ArrayList<>(destData.keySet());

                String columnName = null;
                String destColumnName = null;
                for (int i = 0; i < sourceKeys.size(); i++) {
                    columnName = sourceKeys.get(i);

                    if (i > destData.size() - 1) {
                        newColumns.add(columnName); // 새로운 컬럼이 생겼을 경우
                    } else {
                        destColumnName = destKeys.get(i);
                        columnData = destData.get(destColumnName);

                        if (!data.get(columnName).equals(columnData)) {
                            concurrentIndex.add(index);         // destData가 sourceData와 데이터가 다른 경우 update할 데이터
                            updatedColumns.add(columnName);
                        }
                    }
                }

                if (destData.size() > data.size()) {
                    for (String k : destKeys) {
                        if (!data.containsKey(k)) {
                            data.put(k, destData.get(k));
                            if (!deletedColumns.contains(k)) {
                                deletedColumns.add(k);
                            }
                        }
                    }
                }

                data.put(status, DataStatus.MODIFY.getStatus());
            }

            data.put("updatedColumns", updatedColumns);
            data.put("newColumns", newColumns);
            data.put("deletedColumns", deletedColumns);
            index++;
        }

        return SourceTableData.create(sourceData, sourceRecordTotal.getCount(), insertTotal, concurrentIndex.size(), last, count);
    }

    public static DataMigration dataUpsert(List<TableVO.DetailTable> sourceInfo, List<TableVO.DetailTable> destinationInfo,
                                           List<Map<String, Object>> sourceData, DestinationDataBase destinationDataBase) {
        List<String> columnList = new ArrayList<>();
        for (TableVO.DetailTable source : sourceInfo) {
            columnList.add("`" + source.getColumn_name() + "`");
        }

        TableVO.DetailTable sourceObj = sourceInfo.get(0);
        String tableName = sourceObj.getTable_name();
        boolean isCreated = false;

        // create table
        if (destinationInfo == null || destinationInfo.isEmpty()) {
            Long autoIncrement = sourceObj.getAuto_increment();
            String primaryKeyStr = getPrimaryKeyStr(sourceInfo);

            // create
            isCreated = destinationDataBase.createTable(tableName, autoIncrement, primaryKeyStr, sourceInfo);
            if (!isCreated) {
                return DataMigration.create(MigrationStatus.ERROR_CREATE_TABLE.getCode());
            }
        }

        if (!isCreated) {
            DataMigration result = upsertColumn(tableName, sourceInfo, destinationInfo, destinationDataBase);
            if (result.getResult() < 0) {
                return result;
            }
        }

        isCreated = destinationDataBase.upsertData(tableName, Arrays.toString(columnList.toArray()), sourceData);

        if (!isCreated) {
            return DataMigration.create(MigrationStatus.ERROR_UPSERT_DATA.getCode());
        }

        return DataMigration.create(MigrationStatus.OK.getCode());
    }

    private static String getPrimaryKeyStr(List<TableVO.DetailTable> table) {
        String primaryStr = "PRIMARY KEY (";

        boolean isExistPrimaryKey = false;

        String key;
        for (TableVO.DetailTable obj : table) {
            key = obj.getColumn_key();

            if(key.equalsIgnoreCase("pri")) {
                isExistPrimaryKey = true;
                primaryStr += "`" + obj.getColumn_name() + "`,";
            }
        }

        primaryStr = primaryStr.substring(0, primaryStr.length() - 1);  // 마지막 , 제거
        primaryStr += ")";

        return isExistPrimaryKey ? primaryStr : "";
    }

    private static DataMigration upsertColumn(String tableName, List<TableVO.DetailTable> sourceInfo, List<TableVO.DetailTable> destinationInfo,
                                              DestinationDataBase destinationDataBase) {

        int index = 0;
        List<TableVO.DetailTable> addList = new ArrayList<>();
        TableVO.DetailTable destObj;
        boolean columnResult = false;
        for (TableVO.DetailTable source : sourceInfo) {
            if (destinationInfo.size() - 1 < index) {
                addList.add(source);
                continue;
            }

            destObj = destinationInfo.get(index);

            if (!destObj.getColumn_name().equals(source.getColumn_name())) {
                columnResult = destinationDataBase.updateColumnName(tableName, destObj.getColumn_name(), source.getColumn_name(), source.getColumn_type());
                if (!columnResult) {
                    return DataMigration.create(MigrationStatus.ERROR_UPDATE_COLUMN.getCode());
                }
            }

            index++;
        }

        if (addList.size() > 0) {
            columnResult = destinationDataBase.insertColumnName(tableName, addList);
            if (!columnResult) {
                return DataMigration.create(MigrationStatus.ERROR_INSERT_COLUMN.getCode());
            }
        }

        return DataMigration.create(MigrationStatus.OK.getCode());
    }
}
