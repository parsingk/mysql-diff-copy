package com.lib.dbdiffcopy.differ;

import com.lib.dbdiffcopy.differs.RoutineDiffer;
import com.lib.dbdiffcopy.schema.dto.Routines;
import com.lib.dbdiffcopy.schema.vo.RoutineVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoutineDifferTest {


    private List<RoutineVO> source = new ArrayList<>();
    private List<RoutineVO> dest = new ArrayList<>();

    @Before
    public void dataSetUp() {
        RoutineVO routine;
        for (int i = 0; i < 10 ; i ++) {
            routine = new RoutineVO();
            routine.setRoutine_name("test" + i);
            if (i < 5) {
                routine.setRoutine_type("procedure");
            } else {
                routine.setRoutine_type("function");
            }

            routine.setRoutine_definition(i + "");
            source.add(routine);
        }

        for (int j = 0; j < 10; j ++) {
            routine = new RoutineVO();
            routine.setRoutine_name("test" + j);
            if (j < 5) {
                routine.setRoutine_type("procedure");
            } else {
                routine.setRoutine_type("function");
            }

            routine.setRoutine_definition(j + "");
            dest.add(routine);
        }

    }

    @Test
    public void diff() {
        Routines routines = RoutineDiffer.diff(source, dest);

        Assert.assertEquals(Optional.ofNullable(routines.getNewProcedureCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getNewFunctionCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedProcedureCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedFunctionCount()), Optional.of(0));
    }

    @Test
    public void newProcedureDiff() {
        RoutineVO routine = new RoutineVO();
        routine.setRoutine_name("test10");
        routine.setRoutine_type("procedure");
        routine.setRoutine_name("10");

        source.add(routine);

        Routines routines = RoutineDiffer.diff(source, dest);

        Assert.assertEquals(Optional.ofNullable(routines.getNewProcedureCount()), Optional.of(1));
        Assert.assertEquals(Optional.ofNullable(routines.getNewFunctionCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedProcedureCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedFunctionCount()), Optional.of(0));
    }

    @Test
    public void newFunctionDiff() {
        RoutineVO routine = new RoutineVO();
        routine.setRoutine_name("test10");
        routine.setRoutine_type("function");
        routine.setRoutine_name("10");

        source.add(routine);

        Routines routines = RoutineDiffer.diff(source, dest);

        Assert.assertEquals(Optional.ofNullable(routines.getNewProcedureCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getNewFunctionCount()), Optional.of(1));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedProcedureCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedFunctionCount()), Optional.of(0));
    }

    @Test
    public void modifiedProcedureDiff() {
        source.get(0).setRoutine_definition(10 + "");
        Routines routines = RoutineDiffer.diff(source, dest);

        Assert.assertEquals(Optional.ofNullable(routines.getNewProcedureCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getNewFunctionCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedProcedureCount()), Optional.of(1));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedFunctionCount()), Optional.of(0));
    }

    @Test
    public void modifiedFunctionDiff() {
        source.get(5).setRoutine_definition(10 + "");
        Routines routines = RoutineDiffer.diff(source, dest);

        Assert.assertEquals(Optional.ofNullable(routines.getNewProcedureCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getNewFunctionCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedProcedureCount()), Optional.of(0));
        Assert.assertEquals(Optional.ofNullable(routines.getUpdatedFunctionCount()), Optional.of(1));
    }
}
