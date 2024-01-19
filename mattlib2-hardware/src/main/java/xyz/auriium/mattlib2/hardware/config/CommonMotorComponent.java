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


    /**
     *
     * @return brushless or brushed (look it up)
     */
    @Conf("type") default Type typeOfMotor() { return Type.BRUSHLESS; }
    @Conf("encoderToMechanism") double encoderToMechanismCoefficient();

    @Conf("rotationsToMeters") Optional<Double> rotationToMeterCoefficient();
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






}
