package xyz.auriium.mattlib2.auto;

import xyz.auriium.mattlib2.auto.ff.FFGenComponent;
import xyz.auriium.mattlib2.auto.ff.LinearFFGenRoutine;
import xyz.auriium.mattlib2.auto.ff.RotationFFGenRoutine;
import xyz.auriium.mattlib2.loop.ISubroutine;
import xyz.auriium.mattlib2.hardware.IActuator;
import xyz.auriium.mattlib2.hardware.ILinearEncoder;
import xyz.auriium.mattlib2.hardware.IRotationEncoder;
import xyz.auriium.mattlib2.loop.simple.ISimpleSubroutine;

public class RoutineTesting {

    public static ISimpleSubroutine ffTuningRoutine_rot(FFGenComponent component, IActuator actuator, IRotationEncoder encoder) {
        return new RotationFFGenRoutine(component, actuator, encoder);
    }

    public static ISimpleSubroutine ffTuningRoutine_linear(FFGenComponent component, IActuator actuator, ILinearEncoder encoder) {
        return new LinearFFGenRoutine(component, actuator, encoder);
    }



}
