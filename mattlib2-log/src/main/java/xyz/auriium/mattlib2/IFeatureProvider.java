package xyz.auriium.mattlib2;

import java.util.Optional;

/**
 * Represents a SPI for providing Mattlib features
 */
public interface IFeatureProvider {

    Optional<ITuneFeature> createTuneFeature(); //This needs to be fixed


    /**
     * @return byte representing how important this provider is.
     */
    byte priority();

}
