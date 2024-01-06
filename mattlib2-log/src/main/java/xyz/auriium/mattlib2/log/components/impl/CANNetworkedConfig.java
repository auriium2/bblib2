package xyz.auriium.mattlib2.log.components.impl;

import xyz.auriium.mattlib2.log.annotation.Conf;
import xyz.auriium.mattlib2.log.components.INetworkedConfig;

/**
 *
 */
public interface CANNetworkedConfig extends INetworkedConfig {

    @Conf
    int canId();

}
