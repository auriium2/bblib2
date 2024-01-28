package xyz.auriium.mattlib2;

public class MattlibSettings {

    public enum Robot {
        MCR,
        CARY
    }


    /**
     * Set this to false to kill logging
     */
    public static boolean USE_LOGGING = true;
    public static Robot ROBOT = Robot.CARY;

}
