package xyz.auriium.mattlib2.auto.ff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BaseFFGenRoutineTest {

    @Test
    void testComputeVoltage_shouldWorkWithoutDelay() {
        long startTime = 5000; //arbitrary offset 1

        double voltageAtTime = BaseFFGenRoutine.computeVoltage(startTime, startTime, 0, 0.0008);
        Assertions.assertEquals(0, voltageAtTime);

        long endTimeMS = BaseFFGenRoutine.computeEndTimeInMS(startTime, 0,12,0.0008);
        double voltageAtTime2 = BaseFFGenRoutine.computeVoltage(endTimeMS, startTime, 0, 0.0008);

        Assertions.assertEquals(12, voltageAtTime2);


    }

    @Test
    void testComputeVoltage_shouldWorkWithDelay() {
        long startTime = 5000; //arbitrary offset 1
        long delayTimeMS = 2000;
        long afterDelay = startTime + delayTimeMS;

        double voltageAtTime = BaseFFGenRoutine.computeVoltage(startTime, startTime, delayTimeMS, 0.0008);
        Assertions.assertEquals(0, voltageAtTime);

        double voltageAtTimeD = BaseFFGenRoutine.computeVoltage(afterDelay, startTime, delayTimeMS, 0.0008);
        Assertions.assertEquals(0, voltageAtTimeD);


        long endTimeMS = BaseFFGenRoutine.computeEndTimeInMS(startTime, delayTimeMS, 12,0.0008);
        double voltageAtTime2 = BaseFFGenRoutine.computeVoltage(endTimeMS, startTime, delayTimeMS, 0.0008);

        Assertions.assertEquals(12, voltageAtTime2);


    }





    @Test
    void testComputeEndTimeInMS_shouldGenerateSaneNumbers() {
        long currentTime = 5000; //arbitrary offset

        long endTimeMS = BaseFFGenRoutine.computeEndTimeInMS(currentTime, 0,12,  0.0008);

        //offset + 12 * 1 / ramprate
        Assertions.assertEquals(5000 + (12 * 1250),endTimeMS );

        long offset2 = 2000;

        long endTimeMS2 = BaseFFGenRoutine.computeEndTimeInMS(currentTime, offset2,12,  0.0008);

        //offset + 12 * 1 / ramprate
        Assertions.assertEquals(5000 + (12 * 1250) + offset2,endTimeMS2 );

    }
}