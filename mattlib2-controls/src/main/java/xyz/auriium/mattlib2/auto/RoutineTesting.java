package xyz.auriium.mattlib2.auto;

import xyz.auriium.mattlib2.auto.ff.GenerateFFComponent;
import xyz.auriium.mattlib2.auto.ff.LinearFFGenRoutine;
import xyz.auriium.mattlib2.auto.ff.RotationFFGenRoutine;
import xyz.auriium.mattlib2.hardware.IActuator;
import xyz.auriium.mattlib2.hardware.ILinearEncoder;
import xyz.auriium.mattlib2.hardware.IRotationEncoder;
import xyz.auriium.mattlib2.loop.simple.ISimpleSubroutine;

public class RoutineTesting {

    public static ISimpleSubroutine ffTuningRoutine_rot(GenerateFFComponent component, IActuator actuator, IRotationEncoder encoder) {
        return new RotationFFGenRoutine(component, actuator, encoder);
    }

    public static ISimpleSubroutine ffTuningRoutine_linear(GenerateFFComponent component, IActuator actuator, ILinearEncoder encoder) {
        return new LinearFFGenRoutine(component, actuator, encoder);
    }



}
