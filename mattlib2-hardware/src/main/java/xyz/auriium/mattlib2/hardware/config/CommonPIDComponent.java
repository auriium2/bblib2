package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.HasUpdated;
import xyz.auriium.mattlib2.log.Tune;
import yuukonfig.core.annotate.Key;

/**
 * Commonly reused details of a PID controller
 */
public interface CommonPIDComponent extends INetworkedComponent {

    @Tune
    @Key("p")
    int pConstant();

    @Tune
    @Key("d")
    int dConstant();

    @Tune
    @Key("i")
    int iConstant();


    @HasUpdated(keysToCheck = {"p", "i", "d"})
    boolean hasUpdated();

}
