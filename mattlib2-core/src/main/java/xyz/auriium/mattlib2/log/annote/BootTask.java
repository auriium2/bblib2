package xyz.auriium.mattlib2.log.annote;

/**
 * Functions annotated with this need to accept a runnable and nothing else.
 * They will be run during
 */
public @interface BootTask {

    String value();

}
