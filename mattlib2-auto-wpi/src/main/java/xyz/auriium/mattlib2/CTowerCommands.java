package xyz.auriium.mattlib2;

import edu.wpi.first.wpilibj2.command.Command;
import xyz.auriium.mattlib2.auto.routines.Routine;

public class CTowerCommands {

     public static Command wrapRoutine(Routine routine ) {
         return new WrappedCommand(routine); //stopgap
     }

}
