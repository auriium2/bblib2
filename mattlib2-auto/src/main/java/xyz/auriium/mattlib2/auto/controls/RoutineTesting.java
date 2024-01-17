package xyz.auriium.mattlib2.auto.controls;

import xyz.auriium.mattlib2.auto.controls.ff.FFGenComponent;
import xyz.auriium.mattlib2.auto.controls.ff.LinearFFGenRoutine;
import xyz.auriium.mattlib2.auto.controls.ff.RotationFFGenRoutine;
import xyz.auriium.mattlib2.auto.routines.Routine;
import xyz.auriium.mattlib2.hardware.IActuator;
import xyz.auriium.mattlib2.hardware.ILinearEncoder;
import xyz.auriium.mattlib2.hardware.IRotationEncoder;

public class RoutineTesting {

    public static Routine ffTuningRoutine_rot(FFGenComponent component, IActuator actuator, IRotationEncoder encoder) {
        return new RotationFFGenRoutine(component, actuator, encoder);
    }

    public static Routine ffTuningRoutine_linear(FFGenComponent component, IActuator actuator, ILinearEncoder encoder) {
        return new LinearFFGenRoutine(component, actuator, encoder);
    }



}
