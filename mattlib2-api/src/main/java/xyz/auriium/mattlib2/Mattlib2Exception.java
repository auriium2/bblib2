package xyz.auriium.mattlib2;

import yuukonstants.exception.ExplainedException;

public class Mattlib2Exception extends ExplainedException {
    public Mattlib2Exception(String type, String message, String solution) {
        super("mattlib2",  type, message, solution);
    }

    public Mattlib2Exception(String type, String message, Throwable cause, String solution) {
        super("mattlib2", type,  message, cause, solution);
    }
}
