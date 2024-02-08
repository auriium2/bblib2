package xyz.auriium.mattlib2.loop;

import edu.wpi.first.wpilibj2.command.Command;
import xyz.auriium.mattlib2.loop.simple.ISimpleSubroutine;

public class CTowerCommands {

     public static Command wrapRoutine(ISimpleSubroutine routine ) {
         return new WrappedCommand(routine); //stopgap
     }

}
