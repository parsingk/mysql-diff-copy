package com.lib.dbdiffcopy;

import com.lib.dbdiffcopy.schema.dto.*;
import com.lib.dbdiffcopy.utils.TextDiffUtil;

import java.sql.SQLException;
import java.util.LinkedList;

public interface IDatabaseDiffer {

    /**
     * Get All Table List
     * @return
     * @throws SQLException
     */
    Tables getTableList() throws SQLException;

    /**
     * Get Source Table Data
     * if count is 0, it will get all rows of table.
     *
     * @param tableName
     * @param last
     * @param count
     * @return
     */
    SourceTableData getSourceTableData(String tableName, long last, int count);

    /**
     * Get Destination Table Data
     * if count is 0, it will get all rows of table.
     *
     * @param tableName
     * @param last
     * @param count
     * @return
     */
    DestinationTableData getDestinationTableData(String tableName, long last, int count);

    /**
     * Migration From source To destination.
     *
     * creating table, adding columns, updating columns, inserting data, updating data.
     *
     * @param tableName
     * @return
     */
    DataMigration upsert(String tableName);

    /**
     * Get All Procedures and Functions From both of DataSources.
     *
     * @return
     */
    Routines getRoutines();


    /**
     * Diff Procedure's or Function's Text.
     *
     * @param routineName
     * @return
     */
    LinkedList<TextDiffUtil.Diff> getRoutinesDiffText(String routineName);
}
