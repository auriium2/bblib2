package xyz.auriium.mattlib2;

public class ExplainedException extends RuntimeException{

    final String source;
    final String solution;

    public ExplainedException(String source, String message, String solution) {
        super(message);
        this.source = source;
        this.solution = solution;
    }

    public ExplainedException(String source, String message, Throwable cause, String solution) {
        super(message, cause);
        this.source = source;
        this.solution = solution;
    }
}
