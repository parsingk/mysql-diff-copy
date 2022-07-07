package com.lib.dbdiffcopy.schema.models;

import com.lib.dbdiffcopy.schema.vo.RoutineVO;
import lombok.Data;

@Data
public class Routine {

    private String name;

    // 1 : 프로시저, 2: 펑션
    private String type;

    // 1 : new 표시, 2: modify 표시
    private Integer status = 0;

    public Routine setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public static Routine create(RoutineVO vo) {
        Routine dto = new Routine();
        dto.setName(vo.getRoutine_name());
        dto.setType(vo.getRoutine_type().toLowerCase());

        return dto;
    }
}
