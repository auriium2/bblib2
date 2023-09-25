package xyz.auriium.mattlib2;

public class Mattlib2Exception extends ExplainedException{
    public Mattlib2Exception(String message, String solution) {
        super("mattlib2", message, solution);
    }

    public Mattlib2Exception(String message, Throwable cause, String solution) {
        super("mattlib2", message, cause, solution);
    }
}
