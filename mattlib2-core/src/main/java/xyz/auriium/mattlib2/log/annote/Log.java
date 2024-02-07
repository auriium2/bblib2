package xyz.auriium.mattlib2.log.annote;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark something with this to make it a logging function. Logging functions return void and only take
 * in 1 argument. Here is an example of one:
 *
 * @Log("path")
 * void logInteger(int i);
 *
 * Whever it is called in the dashboard under /<component's name>/path you will see it
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Log {


    String value();
    int vectorSize() default 0;

}
