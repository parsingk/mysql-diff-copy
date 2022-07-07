package com.lib.dbdiffcopy.differs;

import com.lib.dbdiffcopy.schema.DataStatus;
import com.lib.dbdiffcopy.schema.dto.Routines;
import com.lib.dbdiffcopy.schema.models.Routine;
import com.lib.dbdiffcopy.schema.vo.RoutineVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class RoutineDiffer {

    public static Routines diff(List<RoutineVO> sourceRoutines, List<RoutineVO> destRoutines) {
        Map<String, List<RoutineVO>> destRoutinesMap = destRoutines.stream().collect(Collectors.groupingBy(RoutineVO::getRoutine_name));

        AtomicInteger newProcedureCount = new AtomicInteger(0);
        AtomicInteger updatedProcedureCount = new AtomicInteger(0);
        AtomicInteger newFunctionCount = new AtomicInteger(0);
        AtomicInteger updatedFunctionCount = new AtomicInteger(0);

        AtomicReference<Routine> sourceDto = new AtomicReference<>();
        AtomicReference<List<RoutineVO>> destRoutine = new AtomicReference<>();
        AtomicReference<RoutineVO> destRoutineInfo = new AtomicReference<>();

        List<Routine> sourceDtoList = sourceRoutines.stream().map(routine -> {
            sourceDto.set(Routine.create(routine));

            destRoutine.set(destRoutinesMap.getOrDefault(routine.getRoutine_name(), new ArrayList<>()));
            destRoutineInfo.set(destRoutine.get().isEmpty() ? new RoutineVO() : destRoutine.get().get(0));

            if(destRoutine.get().isEmpty()) {
                sourceDto.set(sourceDto.get().setStatus(DataStatus.NEW.getStatus()));

                if(RoutineVO.isProcedure(routine.getRoutine_type())) newProcedureCount.incrementAndGet();
                if(RoutineVO.isFunction(routine.getRoutine_type())) newFunctionCount.incrementAndGet();
            }

            else if(!routine.getRoutine_definition().equals(destRoutineInfo.get().getRoutine_definition())) {
                sourceDto.set(sourceDto.get().setStatus(DataStatus.MODIFY.getStatus()));

                if(RoutineVO.isProcedure(routine.getRoutine_type())) updatedProcedureCount.incrementAndGet();
                if(RoutineVO.isFunction(routine.getRoutine_type())) updatedFunctionCount.incrementAndGet();
            }

            return sourceDto.get();
        }).collect(Collectors.toList());

        List<Routine> destDtoList = destRoutines.stream().map(Routine::create).collect(Collectors.toList());

        Map<String, List<Routine>> sourceMap = sourceDtoList.stream().collect(Collectors.groupingBy(Routine::getType));
        Map<String, List<Routine>> destMap = destDtoList.stream().collect(Collectors.groupingBy(Routine::getType));

        return Routines.create(newProcedureCount.get(), newFunctionCount.get(), updatedProcedureCount.get(), updatedFunctionCount.get(), sourceMap, destMap);
    }
}
