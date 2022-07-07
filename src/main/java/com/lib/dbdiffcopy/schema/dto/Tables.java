package com.lib.dbdiffcopy.schema.dto;

import com.lib.dbdiffcopy.schema.models.Table;
import lombok.Data;

import java.util.List;

@Data
public class Tables {
    private List<Table> sourceTables;
    private List<Table> destinationTables;
    private Integer tableAddCount;
    private Integer columnUpdateCount;
    private Integer columnAddCount;

    public static Tables create(List<Table> sourceTables, List<Table> destinationTables,
                                Integer tableAddCount,
                                Integer columnUpdateCount, Integer columnAddCount) {
        Tables t = new Tables();
        t.setSourceTables(sourceTables);
        t.setDestinationTables(destinationTables);
        t.setTableAddCount(tableAddCount);
        t.setColumnUpdateCount(columnUpdateCount);
        t.setColumnAddCount(columnAddCount);

        return t;
    }
}
