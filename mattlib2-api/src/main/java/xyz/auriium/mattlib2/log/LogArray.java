package xyz.auriium.mattlib2.log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * You must mark arrays with this otherwise my code will break.
 * unfortunately i am not talented enough of a bytecode programmer to use raw opcodes
 * so i cannot implement a simple for loop
 * please accept my apologies and use this instead
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface LogArray {

    String value();

    int size();

}
