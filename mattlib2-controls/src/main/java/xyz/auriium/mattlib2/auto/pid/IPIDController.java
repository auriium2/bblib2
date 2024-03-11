package xyz.auriium.mattlib2.auto.pid;

public interface IPIDController {

    /**
     * Tell the PID controller to move to a reference
     * @param setpoint_primeUnits where you want to go (prime units meaning the units the controlled plant's input variable is in)
     * @param state_primeUnits where you are
     * @return a control effort in prime units
     */
    double controlToReference_primeUnits(double setpoint_primeUnits, double state_primeUnits);


    boolean isAtSetpoint();

}
