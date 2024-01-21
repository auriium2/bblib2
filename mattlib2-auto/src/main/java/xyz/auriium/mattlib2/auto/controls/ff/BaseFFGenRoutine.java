package xyz.auriium.mattlib2.auto.controls.ff;

import xyz.auriium.mattlib2.IPeriodicLooped;
import xyz.auriium.mattlib2.auto.routines.Routine;
import xyz.auriium.mattlib2.hardware.IActuator;

import java.util.LinkedList;
import java.util.List;


/**
 * Base routine that while active will calculate the desired ff and print it to console/push to blackboard
 */
public abstract class BaseFFGenRoutine implements Routine, IPeriodicLooped {

    final FFGenComponent component;
    final IActuator actuator;

    protected BaseFFGenRoutine(FFGenComponent component, IActuator actuator) {
        this.component = component;
        this.actuator = actuator;

        mattRegister();
    }

    abstract double emitVelocity_primeUnitsPerSecond();

    //state
    final List<Double> velocityData = new LinkedList<>();
    final List<Double> voltageData = new LinkedList<>();
    long endTime_ms;
    long startTime_ms;

    @Override
    public void awaken() {
        velocityData.add(0d);
        voltageData.add(0d);
        velocityData.add(0d);
        voltageData.add(0d);
        velocityData.add(0d);
        voltageData.add(0d);
        velocityData.add(0d);
        voltageData.add(0d);
        velocityData.add(0d);
        voltageData.add(0d);
        velocityData.add(0d);
        voltageData.add(0d);
        velocityData.add(0d);
        voltageData.add(0d);
        velocityData.add(0d);
        voltageData.add(0d);

        startTime_ms = System.currentTimeMillis();
        endTime_ms = computeEndTimeInMS(System.currentTimeMillis(), component.endVoltage_volts(), component.rampRate_voltsPerMS());
    }

    @Override
    public Outcome runLogic(Orders ctx) {
        long curTime_ms = System.currentTimeMillis();
        if (curTime_ms > endTime_ms) {
            return Outcome.OK_FINISHED;
        }


        double voltage = computeVoltage(curTime_ms, startTime_ms, component.delay_ms(), component.rampRate_voltsPerMS());
        double velocityOut = emitVelocity_primeUnitsPerSecond();
        actuator.setToVoltage(voltage);

        voltageData.add(voltage);
        velocityData.add(velocityOut);

        var pr = new PolynomialRegression(from(velocityData), from(voltageData), 1);
        var ks = pr.beta(0);
        var kv = pr.beta(1);

        component.logInputVoltage(voltage);
        component.logInputVelocity(velocityOut);
        component.logPredictedStaticConstant(ks);
        component.logPredictedStaticConstant(kv);

        return Outcome.WORKING;
    }

    static double[] from(List<Double> dubList) {
        Double[] dbs = dubList.toArray(Double[]::new);
        double[] result = new double[dbs.length];
        for (int i = 0; i < dbs.length; i++) {
            result[i] = dbs[i];
        }
        return result;

    }

    @Override
    public void cleanup() {
        actuator.setToVoltage(0);
        velocityData.clear();
        voltageData.clear();
    }

    public static double computeVoltage(long currentTimeMS, long startTimeMS, long delayTimeMS, double rampRateVoltPerMS) {
        long timePassedMS = (currentTimeMS - startTimeMS) * 1000;
        if (timePassedMS < delayTimeMS) {
            return 0d;
        } else {
            long timePassedAfterDelayMS = timePassedMS - delayTimeMS;
            return timePassedAfterDelayMS * rampRateVoltPerMS; // v/ms * ms = v
        }

    }

    public static long computeEndTimeInMS(long startTimeMS, double endVoltage, double rampRateVoltagePerSecond) {
        double invertedRampRateSecondsPerVolts = (1/rampRateVoltagePerSecond);
        double durationToReachEndVoltage_seconds = invertedRampRateSecondsPerVolts * endVoltage;
        double durationToReachEndVoltage_miliSeconds = durationToReachEndVoltage_seconds * 1000L;

        return startTimeMS + (long) durationToReachEndVoltage_miliSeconds;
    }

}
