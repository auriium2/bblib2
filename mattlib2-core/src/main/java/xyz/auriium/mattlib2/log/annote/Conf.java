package xyz.auriium.mattlib2.log.annote;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marking a function with this will make Mattlib load it from the config.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Conf  {

    String value();

}
