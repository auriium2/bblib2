package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annote.HasUpdated;
import xyz.auriium.mattlib2.log.annote.Tune;

/**
 * Commonly reused details of a PID controller
 */
public interface CommonPIDComponent extends INetworkedComponent {

    @Tune("p") double pConstant();
    @Tune("i") double iConstant();
    @Tune("d") double dConstant();
    @Tune("tolerance") double tolerance_pidUnits();

    @HasUpdated(keysToCheck = {"p", "i", "d"}) boolean hasUpdated();

}
