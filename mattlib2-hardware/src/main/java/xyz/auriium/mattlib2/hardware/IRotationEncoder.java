package xyz.auriium.mattlib2.hardware;

import xyz.auriium.mattlib2.IRaw;

/**
 * Represents an encoder in rotation space
 * Units in rotations ofc
 */
public interface IRotationEncoder extends IRaw {

    /**
     *ynits in mechanism rotations, converted back to the encoder using the coefficient of motor to encoder.
     * @param offset_mechanismRotations The rotational distance (unbound) the encoder should think it is at currently. It's safe for this to be greater than normalized
     */
    void forceRotationalOffset(double offset_mechanismRotations);


    /**
     * deprecated, use mechanism instead
     * @return The current angular position in rotations, unbound
     */
    @Deprecated
    double angularPosition_encoderRotations();

    /**
     *
     * @return The current mechanism angular position in rotations, unbound
     */
    double angularPosition_mechanismRotations();

    /**
     *
     * @return The current angular position in rotations, normalized to 0-1
     */
    double angularPosition_normalizedMechanismRotations();

    /**
     *
     * @deprecated use mechanism
     * @return The current angular position of the mechanism in rotations, normalized 0 to 1
     */
    @Deprecated
    double angularPosition_normalizedEncoderRotations();

    /**
     *
     * @return angular velocity in rotations per second
     */
    double angularVelocity_mechanismRotationsPerSecond();

    /**
     *
     * @return
     */
    @Deprecated
    double angularvelocity_encoderRotationsPerSecond();



}
