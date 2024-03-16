package xyz.auriium.mattlib2.loop;

import edu.wpi.first.wpilibj2.command.Command;
import xyz.auriium.mattlib2.loop.simple.ISimpleSubroutine;

public class WrappedCommand extends Command {

    final ISimpleSubroutine routine;


    Outcome<?> outcome = Outcome.working();
    public WrappedCommand(ISimpleSubroutine routine) {
        this.routine = routine;
    }

    @Override
    public void initialize() {
        routine.runLogic(ISubroutine.Orders.AWAKEN);
    }

    @Override
    public void execute() {
        outcome = routine.runLogic(ISubroutine.Orders.CONTINUE);
    }

    @Override
    public boolean isFinished() {
        return outcome.type() != Outcome.Type.WORKING;
    }

    @Override
    public void end(boolean interrupted) {
        routine.runLogic(ISubroutine.Orders.DIE);
    }
}
