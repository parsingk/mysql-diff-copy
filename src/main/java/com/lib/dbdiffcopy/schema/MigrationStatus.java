package com.lib.dbdiffcopy.schema;

import lombok.Getter;

public enum MigrationStatus {

    OK(1),

    ERROR_CREATE_TABLE(-99),
    ERROR_UPDATE_COLUMN(-100),
    ERROR_INSERT_COLUMN(-101),
    ERROR_UPSERT_DATA(-102);



    @Getter
    private Integer code;

    MigrationStatus(Integer code) {
        this.code = code;
    }
}
