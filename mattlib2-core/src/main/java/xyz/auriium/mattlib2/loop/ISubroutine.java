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
        CONTINUE, //Tells the routine to keep working
        CANCEL,
    }

    enum SetupOrders {
        AWAKEN, //this one tells the routine work is imminent
        DIE //this one tells the routine to stop working and clean up memory allocation
    }

    void runSetup(SetupOrders orders);

    /**
     * The method describing the order-outcome loop. The routine takes orders, performs actions, then describes the outcome of it's mission
     * @param ctx
     */
    Outcome<O> runLogic(Orders orders, I whiteboard);




}
