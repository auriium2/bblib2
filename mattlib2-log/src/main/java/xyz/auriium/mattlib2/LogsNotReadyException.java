package xyz.auriium.mattlib2;

public class LogsNotReadyException extends ExplainedException{
    public LogsNotReadyException(String message, String solution) {
        super(message, "mattlib2", solution);
    }
}
