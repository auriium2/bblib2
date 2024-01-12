package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annote.HasUpdated;
import xyz.auriium.mattlib2.log.annote.Tune;
import yuukonfig.core.annotate.Key;

/**
 * Commonly reused details of a PID controller
 */
public interface CommonPIDComponent extends INetworkedComponent {

    @Tune("p") int pConstant();
    @Tune("d") int dConstant();
    @Tune("i") int iConstant();


    @HasUpdated(keysToCheck = {"p", "i", "d"}) boolean hasUpdated();

}
