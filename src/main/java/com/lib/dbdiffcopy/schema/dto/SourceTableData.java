package com.lib.dbdiffcopy.schema.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SourceTableData {

    private List<Map<String, Object>> data;
    private Integer recordTotal;
    private Integer insertTotal;
    private Integer updateTotal;
    private long last;
    private long count;

    public static SourceTableData create(List<Map<String, Object>> data, Integer recordTotal,
                                              Integer insertTotal, Integer updateTotal,
                                              long last, long count) {
        SourceTableData dto = new SourceTableData();
        dto.setData(data);
        dto.setRecordTotal(recordTotal);
        dto.setInsertTotal(insertTotal);
        dto.setUpdateTotal(updateTotal);
        dto.setLast(last);
        dto.setCount(count);

        return dto;
    }
}
