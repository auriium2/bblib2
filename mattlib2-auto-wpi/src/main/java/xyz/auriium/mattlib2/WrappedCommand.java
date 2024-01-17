package xyz.auriium.mattlib2;

import edu.wpi.first.wpilibj2.command.Command;
import xyz.auriium.mattlib2.auto.routines.Routine;

public class WrappedCommand extends Command {

    final Routine routine;

    public WrappedCommand(Routine routine) {
        this.routine = routine;
    }

    @Override public void initialize() {
        routine.awaken();
    }

    @Override public void execute() {
        routine.runLogic(Routine.Orders.CONTINUE);
    }

    @Override public void end(boolean interrupted) {
        routine.cleanup();
    }
}
