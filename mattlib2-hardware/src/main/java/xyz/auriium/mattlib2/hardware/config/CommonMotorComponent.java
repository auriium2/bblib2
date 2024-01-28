package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annote.Conf;

import java.util.Optional;

/**
 * Commonly reused configuration details of a motor
 */
public interface CommonMotorComponent extends INetworkedComponent {

    enum Type {
        BRUSHED,
        BRUSHLESS
    }

    enum Normally {
        OPEN,
        CLOSED
    }

    enum OptimizationMode {
        NONE,
        POSITION
    }


    /**
     *
     * @return brushless or brushed (look it up)
     */
    @Conf("type") default Optional<Type> typeOfMotor() { return Optional.of(Type.BRUSHLESS); }
    @Conf("encoderToMechanismCoef") double encoderToMechanismCoefficient();

    @Conf("rotationToMeterCoef") Optional<Double> rotationToMeterCoefficient();
    @Conf("currentLimit") Optional<Integer> currentLimit();
    @Conf("forwardLimit") Optional<Normally> forwardLimit();
    @Conf("reverseLimit") Optional<Normally> reverseLimit();
    @Conf("forwardSoftLimit") Optional<Double> forwardSoftLimit_mechanismRot();
    @Conf("reverseSoftLimit") Optional<Double> reverseSoftLimit_mechanismRot();

    @Conf("inverted") Optional<Boolean> inverted();
    @Conf("openRampRate") Optional<Double> openRampRate_seconds();
    @Conf("closedRampRate") Optional<Double> closedRampRate_seconds();
    @Conf("breakModeEnabled") Optional<Boolean> breakModeEnabled();
    @Conf("hasAbsolute") Optional<Boolean> hasAbsoluteEncoder();

    //Stuff used for simulation
    @Conf(ROTATIONAL_INERTIA) Optional<Double> massMomentInertia();
    @Conf(POSITION_STDV) Optional<Double> positionStandardDeviation();
    @Conf(VELOCITY_STDV) Optional<Double> velocityStandardDeviation();

    String POSITION_STDV = "positionStdv";
    String ROTATIONAL_INERTIA = "rotationalInertia";
    String VELOCITY_STDV = "velocityStdv";






}
