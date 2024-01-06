package xyz.auriium.mattlib2.log;

import xyz.auriium.mattlib2.Mattlib2Exception;

public class LogsNotReadyException extends Mattlib2Exception {
    public LogsNotReadyException(String message, String solution) {
        super("logsNotReady", message, solution);
    }
}
