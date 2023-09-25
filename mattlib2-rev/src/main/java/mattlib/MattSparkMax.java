package mattlib;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.REVLibError;
import xyz.auriium.mattlib2.components.impl.CANComponent;
import xyz.auriium.mattlib2.components.impl.MotorComponent;
import xyz.auriium.mattlib2.hardware.IActuator;
import xyz.auriium.mattlib2.hardware.ILooped;

public class MattSparkMax implements IActuator, ILooped {

    final CANSparkMax sparkMax;
    final CANComponent canConfig;
    final MotorComponent motorConfig;

    public MattSparkMax(CANComponent canConfig, MotorComponent motorConfig) {
        this.motorConfig = motorConfig;
        this.canConfig = canConfig;

        try {
            this.sparkMax = new CANSparkMax(
                    canConfig.canId(),
                    CANSparkMaxLowLevel.MotorType.kBrushless
            );
        } catch (IllegalStateException e) {
            canConfig.badCanID();

            throw new IllegalStateException("MATTLIB: BAD CAN ID FAIL");
        }


        REVLibError err = sparkMax.restoreFactoryDefaults();
        if (err != REVLibError.kOk) {
            canConfig.canConfigError();
        }

        sparkMax.enableVoltageCompensation(12);
    }

    @Override
    public void setToVoltage(double voltage) {
        sparkMax.setVoltage(voltage);
    }

    @Override
    public void setToPercent(double percent_zeroToOne) {
        sparkMax.setVoltage(percent_zeroToOne * 12);
    }

    @Override
    public double reportCurrentNow() {
        return outputCurrent;
    }

    @Override
    public double reportVoltageNow() {
        return outputVoltage;
    }

    double outputCurrent = 0;
    double outputVoltage = 0;

    @Override
    public void loop() {
        outputVoltage = sparkMax.getBusVoltage();
        outputCurrent = sparkMax.getOutputCurrent();

        motorConfig.logCurrentDraw(outputCurrent);
        motorConfig.logVoltageGiven(outputVoltage);

    }
}
