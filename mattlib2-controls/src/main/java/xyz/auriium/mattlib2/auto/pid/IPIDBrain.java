package xyz.auriium.mattlib2.auto.pid;

import xyz.auriium.mattlib2.auto.pid.IPIDController;

/**
 * Spawns off little pid children
 */
public interface IPIDBrain {

    IPIDController spawn();


}
