package com.lib.dbdiffcopy.schema;

import lombok.Getter;

public enum DataStatus {

    NEW(1),
    MODIFY(2),
    DELETE(3);

    @Getter
    private final Integer status;

    DataStatus(int i) {
        this.status = i;
    }
}
