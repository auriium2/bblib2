package xyz.auriium.mattlib2.log.annote;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Notates a function that will behave as a callback for data of a certain key. The function should have no parameters.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Callback {

    String[] keysToCheck();

}
