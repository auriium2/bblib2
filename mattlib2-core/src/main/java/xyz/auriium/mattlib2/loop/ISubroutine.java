package xyz.auriium.mattlib2.loop;

/**
 * Like a WPI command but more confusing
 */
public interface ISubroutine<I,O> {

    /**
     * The orders of a routine describes what the system controlling the routine desires the routine to do
     * The system controlling the routine could be another routine, the robot brain, etc
     *
     * the routine does not always have to listen to the orders but for the most part should do so
     */
    enum Orders {
        AWAKEN, //this one tells the routine work is imminent. It will always be sent.
        CONTINUE, //Tells the routine to keep working and wait for either cancel or completed
        CANCEL, //This signal is sent when a subroutine has been interrupted explicitly by a parent. Prefer using DIE over this one.
        COMPLETED, //this signal is sent to acknowledge completion of the routine i.e. after the routine runs a successful outcome.
        DIE, //this one tells the routine to stop working and clean up memory allocation.
        // It will always be sent after a cancel or completed

    }


    /**
     * The method describing the order-outcome loop. The routine takes orders, performs actions, then describes the outcome of it's mission
     * @param ctx
     */
    Outcome<O> runLogic(Orders orders, I whiteboard);




}
