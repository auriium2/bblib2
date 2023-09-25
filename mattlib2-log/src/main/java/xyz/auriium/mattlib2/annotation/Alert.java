package xyz.auriium.mattlib2.annotation;

/**
 * Methods annotated with this must have only string parameters and no return values
 */
public @interface Alert {

    String value();

}
