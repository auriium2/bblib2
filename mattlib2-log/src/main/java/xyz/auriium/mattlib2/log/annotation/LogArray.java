package xyz.auriium.mattlib2.log.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate a method with this to log multiple datas at the same time (typically correlated data)
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface LogArray {
}
