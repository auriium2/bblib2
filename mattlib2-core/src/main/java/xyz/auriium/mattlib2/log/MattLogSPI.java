package xyz.auriium.mattlib2.log;

import xyz.auriium.mattlib2.IMattLog;

/**
 * Represents a SPI for providing Mattlib features
 */
public interface MattLogSPI {

    IMattLog createLogger(); //This needs to be fixed


    /**
     * @return byte representing how important this provider is.
     */
    byte priority();

}
