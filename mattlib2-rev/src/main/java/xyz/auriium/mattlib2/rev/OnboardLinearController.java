package xyz.auriium.mattlib2.rev;


import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.math.controller.PIDController;
import xyz.auriium.mattlib2.hardware.ILinearController;
import xyz.auriium.mattlib2.hardware.ILinearEncoder;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;
import xyz.auriium.yuukonstants.exception.ExplainedException;

public class OnboardLinearController extends BaseSparkMotor implements ILinearController{

    final ILinearEncoder stateObserver;
    final PIDComponent pidComponent;

    public OnboardLinearController(CANSparkMax sparkMax, MotorComponent motorComponent, RelativeEncoder encoder, ILinearEncoder stateObserver, PIDComponent pidComponent) {
        super(sparkMax, motorComponent, encoder);
        this.stateObserver = stateObserver;
        this.pidComponent = pidComponent;
    }

    final PIDController pidController = new PIDController(0,0,0);


    double setpoint_primeUnits = 0;
    double observation_primeUnits = 0;


    @Override public void stopActuator() {
        super.stopActuator();

        pidController.reset();
    }

    @Override
    public ExplainedException[] verifyInit() {
        var ee = super.verifyInit();

        pidController.setPID(
                pidComponent.pConstant(),
                pidComponent.iConstant(),
                pidComponent.dConstant()
        );

        pidController.setTolerance(pidComponent.tolerance_pidUnits());

        return ee;
    }


    @Override public void logPeriodic() {
        super.logPeriodic();

        pidComponent.reportState(observation_primeUnits);
        pidComponent.reportReference(setpoint_primeUnits);
    }

    @Override public void tunePeriodic() {
        super.tunePeriodic();

        if (pidComponent.hasUpdated()) {
            pidController.setP(pidComponent.pConstant());
            pidController.setI(pidComponent.iConstant());
            pidController.setD(pidComponent.dConstant());
            pidController.setTolerance(pidComponent.tolerance_pidUnits());
        }

    }

    @Override public void controlToLinearReferenceArbitrary(double setpointMechanism_meters, double arbitraryFF_volts) {
        double measurement_mechanismMeters = stateObserver.linearPosition_mechanismMeters();
        double feedbackVoltage = pidController.calculate(measurement_mechanismMeters, setpointMechanism_meters);


        setpoint_primeUnits = setpointMechanism_meters;
        observation_primeUnits = measurement_mechanismMeters;


        setToVoltage(feedbackVoltage + arbitraryFF_volts);
    }

}
