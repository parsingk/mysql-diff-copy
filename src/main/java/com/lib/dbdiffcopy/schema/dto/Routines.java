package com.lib.dbdiffcopy.schema.dto;

import com.lib.dbdiffcopy.schema.models.Routine;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Routines {

    private Integer newProcedureCount;
    private Integer newFunctionCount;
    private Integer updatedProcedureCount;
    private Integer updatedFunctionCount;
    private Map<String, List<Routine>> source;
    private Map<String, List<Routine>> destination;

    public static Routines create(Integer newProcedureCount, Integer newFunctionCount,
                                  Integer updatedProcedureCount, Integer updatedFunctionCount,
                                  Map<String, List<Routine>> source, Map<String, List<Routine>> destination) {
        Routines routines = new Routines();
        routines.setSource(source);
        routines.setDestination(destination);
        routines.setNewProcedureCount(newProcedureCount);
        routines.setNewFunctionCount(newFunctionCount);
        routines.setUpdatedProcedureCount(updatedProcedureCount);
        routines.setUpdatedFunctionCount(updatedFunctionCount);

        return routines;
    }
}
