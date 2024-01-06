package xyz.auriium.mattlib2.log.annotation;

/**
 * Methods annotated with this must have only string parameters and no return values
 */
public @interface Alert {

    String value();

}
