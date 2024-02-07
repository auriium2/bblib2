package xyz.auriium.mattlib2.loop;

import edu.wpi.first.wpilibj2.command.Command;

public class CTowerCommands {

     public static Command wrapRoutine(IRoutine routine ) {
         return new WrappedCommand(routine); //stopgap
     }

}
