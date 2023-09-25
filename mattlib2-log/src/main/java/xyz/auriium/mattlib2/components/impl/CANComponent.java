package xyz.auriium.mattlib2.components.impl;

import xyz.auriium.mattlib2.annotation.Alert;
import xyz.auriium.mattlib2.annotation.Conf;

/**
 *
 */
public interface CANComponent {

    @Conf
    int canId();

    @Alert("the can id is bad!")
    void badCanID();

    @Alert("bad config")
    void canConfigError();

    @Alert("bad can")
    void canFaultError();

}
