package xyz.auriium.mattlib2.foxe;

import java.util.function.Consumer;

public interface MultiplexUserChannel {

    Object[] poll();

    /**
     *
     * @param latest NOT THREADSAFE
     */
    void registerCallback(Consumer<Object> latest);

}
