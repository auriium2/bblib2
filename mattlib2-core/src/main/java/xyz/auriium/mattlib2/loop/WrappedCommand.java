package xyz.auriium.mattlib2.loop;

import edu.wpi.first.wpilibj2.command.Command;

public class WrappedCommand extends Command {

    final IRoutine routine;

    IRoutine.Outcome lastOrder = IRoutine.Outcome.WORKING;

    public WrappedCommand(IRoutine routine) {
        this.routine = routine;
    }

    @Override
    public void initialize() {
        routine.runLogic(IRoutine.Orders.AWAKEN);
    }

    @Override
    public void execute() {
        lastOrder = routine.runLogic(IRoutine.Orders.CONTINUE);
    }

    @Override public boolean isFinished() {
        return lastOrder != IRoutine.Outcome.WORKING;
    }

    @Override public void end(boolean interrupted) {
        routine.runLogic(IRoutine.Orders.CLEANUP);
    }
}
