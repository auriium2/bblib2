package xyz.auriium.mattlib2.hard;

/**
 * What in the fuck
 * Represents a motor controller, which by definition can control actuation, likely has onboard PD control, and has linear/rotation control
 */
public interface IRotationalController extends IRotationalMotor, IRotationalPIDControl {
}
