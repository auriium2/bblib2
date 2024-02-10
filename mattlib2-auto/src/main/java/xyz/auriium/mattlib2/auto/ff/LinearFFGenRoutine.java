package xyz.auriium.mattlib2.auto.ff;

import xyz.auriium.mattlib2.hardware.IActuator;
import xyz.auriium.mattlib2.hardware.ILinearEncoder;

public class LinearFFGenRoutine extends BaseFFGenRoutine{

    final ILinearEncoder encoder;

    public LinearFFGenRoutine(GenerateFFComponent component, IActuator actuator, ILinearEncoder encoder) {
        super(component, actuator);

        this.encoder = encoder;
    }

    @Override
    double emitVelocity_primeUnitsPerSecond() {
        return encoder.linearVelocity_mechanismMetersPerSecond();
    }
}
