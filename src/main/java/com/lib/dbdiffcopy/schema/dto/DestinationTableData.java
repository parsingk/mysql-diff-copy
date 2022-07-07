package com.lib.dbdiffcopy.schema.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DestinationTableData {

    private List<Map<String, Object>> data;
    private Integer recordTotal;
    private long last;
    private long count;

    public static DestinationTableData create(List<Map<String, Object>> data, Integer recordTotal,
                                              long last, long count) {
        DestinationTableData dto = new DestinationTableData();
        dto.setData(data);
        dto.setRecordTotal(recordTotal);
        dto.setLast(last);
        dto.setCount(count);

        return dto;
    }
}
