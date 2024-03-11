package xyz.auriium.mattlib2.auto.ff;

import xyz.auriium.mattlib2.auto.ff.config.GenerateFFComponent;
import xyz.auriium.mattlib2.hardware.IActuator;
import xyz.auriium.mattlib2.hardware.IRotationEncoder;

public class RotationFFGenRoutine extends BaseFFGenRoutine{

    final IRotationEncoder encoder;
    public RotationFFGenRoutine(GenerateFFComponent component, IActuator actuator, IRotationEncoder encoder) {
        super(component, actuator);
        this.encoder = encoder;
    }

    @Override
    double emitVelocity_primeUnitsPerSecond() {
        return encoder.angularVelocity_mechanismRotationsPerSecond();
    }
}
