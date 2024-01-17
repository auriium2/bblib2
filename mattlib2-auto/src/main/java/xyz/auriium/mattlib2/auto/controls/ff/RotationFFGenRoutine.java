package xyz.auriium.mattlib2.auto.controls.ff;

import xyz.auriium.mattlib2.hardware.IActuator;
import xyz.auriium.mattlib2.hardware.IRotationEncoder;

public class RotationFFGenRoutine extends BaseFFGenRoutine{

    final IRotationEncoder encoder;
    public RotationFFGenRoutine(FFGenComponent component, IActuator actuator, IRotationEncoder encoder) {
        super(component, actuator);
        this.encoder = encoder;
    }

    @Override
    double emitVelocity_primeUnitsPerSecond() {
        return encoder.angularVelocity_mechanismRotationsPerSecond();
    }
}
