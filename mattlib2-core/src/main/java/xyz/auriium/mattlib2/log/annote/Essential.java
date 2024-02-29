package xyz.auriium.mattlib2.log.annote;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker for things that must be logged/tuned
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Essential {
}
