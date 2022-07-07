package com.lib.dbdiffcopy.schema.dto;

import lombok.Data;

@Data
public class DataMigration {

    private Integer result;

    public static DataMigration create(Integer result) {
        DataMigration dto = new DataMigration();
        dto.setResult(result);

        return dto;
    }
}
