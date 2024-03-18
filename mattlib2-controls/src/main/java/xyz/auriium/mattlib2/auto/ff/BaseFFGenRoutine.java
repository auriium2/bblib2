package xyz.auriium.mattlib2.auto.ff;

import xyz.auriium.mattlib2.auto.ff.config.GenerateFFComponent;
import xyz.auriium.mattlib2.loop.IMattlibHooked;
import xyz.auriium.mattlib2.hardware.IActuator;
import xyz.auriium.mattlib2.loop.Outcome;
import xyz.auriium.mattlib2.loop.simple.ISimpleSubroutine;

import java.util.LinkedList;
import java.util.List;


/**
 * Base routine that while active will calculate the desired ff and print it to console/push to blackboard
 */
public abstract class BaseFFGenRoutine implements ISimpleSubroutine, IMattlibHooked {

    final GenerateFFComponent component;
    final IActuator actuator;

    protected BaseFFGenRoutine(GenerateFFComponent component, IActuator actuator) {
        this.component = component;
        this.actuator = actuator;

        this.delay_ms = component.delay_ms().orElse(20L);
        this.endVoltage_volts = component.endVoltage_volts().orElse(12d);
        this.rampRate_voltsPerSecond = component.rampRate_voltsPerMS().orElse(0.001);

        mattRegister();
    }

    final long delay_ms;
    final double endVoltage_volts;
    final double rampRate_voltsPerSecond;

    abstract double emitVelocity_primeUnitsPerSecond();

    //state
    final List<Double> velocityData = new LinkedList<>();
    final List<Double> voltageData = new LinkedList<>();
    long endTime_ms;
    long startTime_ms;

    Outcome<Void> awaken() {
        startTime_ms = System.currentTimeMillis();
        endTime_ms = computeEndTimeInMS(System.currentTimeMillis(), delay_ms, endVoltage_volts, rampRate_voltsPerSecond);
        return Outcome.success();
    }

    Outcome<Void> die() {
        var pr = FastPolynomialRegression.loadRankDeficient_iterative(from(velocityData), from(voltageData), 2);
        var ks = pr.beta(0);
        var kv = pr.beta(1);
        var ka = pr.beta(2);
        component.logPredictedStaticConstant(ks);
        component.logPredictedVelocityConstant(kv);
        component.logPredictedAccelConstant(ka);

        actuator.setToVoltage(0);
        velocityData.clear();
        voltageData.clear();
        return Outcome.success();
    }

    Outcome<Void> work() {
        long curTime_ms = System.currentTimeMillis();
        if (curTime_ms > endTime_ms) return Outcome.success();

        double voltage = computeVoltage(curTime_ms, startTime_ms, delay_ms, rampRate_voltsPerSecond);
        double velocityOut = emitVelocity_primeUnitsPerSecond();
        actuator.setToVoltage(voltage);

        voltageData.add(voltage);
        velocityData.add(velocityOut);

        component.logInputVoltage(voltage);
        component.logInputVelocity(velocityOut);

        return Outcome.working();
    }

    @Override
    public Outcome<Void> runLogic(Orders ctx, Void vd) {
        return switch (ctx) {
            case CONTINUE -> work();
            case CANCEL -> Outcome.success(); //allow proceed to cleanup
            case DIE -> die();
            case AWAKEN -> awaken();
            case COMPLETED -> Outcome.success();
        };

    }

    static double[] from(List<Double> dubList) {
        Double[] dbs = dubList.toArray(Double[]::new);
        double[] result = new double[dbs.length];
        for (int i = 0; i < dbs.length; i++) {
            result[i] = dbs[i];
        }
        return result;

    }


    public static double computeVoltage(long currentTimeMS, long startTimeMS, long delayTimeMS, double rampRateVoltPerMS) {
        long timePassedMS = (currentTimeMS - startTimeMS);
        if (timePassedMS < delayTimeMS) {
            return 0d;
        } else {
            long timePassedAfterDelayMS = timePassedMS - delayTimeMS;
            return timePassedAfterDelayMS * rampRateVoltPerMS; // v/ms * ms = v
        }

    }

    public static long computeEndTimeInMS(long currentTimeOffset_ms, long delayTimeMS, double endVoltage, double rampRateVoltPerMS) {
        double invertedRampRateMSPerVolts = (1/rampRateVoltPerMS);
        double durationToReachEndVoltage_ms = invertedRampRateMSPerVolts * endVoltage;

        return currentTimeOffset_ms + delayTimeMS + (long) durationToReachEndVoltage_ms;
    }

}
