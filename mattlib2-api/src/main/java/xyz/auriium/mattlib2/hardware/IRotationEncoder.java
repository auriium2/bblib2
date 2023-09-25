package xyz.auriium.mattlib2.hardware;

/**
 * Represents an encoder in rotation space
 * Units in rotations ofc
 */
public interface IRotationEncoder {

    /**
     * @return The current rotation position of the encoder (behind the gearbox) in rotations
     */
    double rotationEncoderPosition_rot();

    /**
     * @return The current rotational position of the actual mechanism (after gearbox, gearing, etc)
     * in rotations
     */
    double rotationMechanismPosition_rot();


}
