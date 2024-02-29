package xyz.auriium.mattlib2;

public class MattlibSettings {

    public enum Robot {
        MCR,
        CARY
    }

    public enum LogLevel {
        VERBOSE_TELEMETRY(3), //telemetry everything
        ESSENTIAL_TELEMETRY(2), //telemetry "@essential" stuff only, log everything else
        LOG(1), //only log to static log files
        OFF(0); //don't log at all, including the static log files

        final int level;

        LogLevel(int level) {
            this.level = level;
        }

        public boolean isAt(LogLevel other) {
            return this.level >= other.level;
        }


    }

    public static LogLevel USE_TELEMETRY = LogLevel.VERBOSE_TELEMETRY;
    public static Robot ROBOT = Robot.CARY;



}
