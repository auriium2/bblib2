package xyz.auriium.mattlib2.log;

import java.util.Optional;

/**
 * Represents a SPI for providing Mattlib features
 */
public interface ILoggerProvider {

    Optional<IMattLogger> createLogger(); //This needs to be fixed


    /**
     * @return byte representing how important this provider is.
     */
    byte priority();

}
