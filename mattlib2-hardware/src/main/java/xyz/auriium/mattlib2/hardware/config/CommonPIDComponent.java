package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annote.Conf;
import xyz.auriium.mattlib2.log.annote.Tune;

import java.util.Optional;

/**
 * Commonly reused details of a PID controller
 */
public interface CommonPIDComponent extends INetworkedComponent {

    @Tune("p") double pConstant();
    @Tune("i") double iConstant();
    @Tune("d") double dConstant();
    @Conf("tolerance") Optional<Double> tolerance_pidUnits();
    @Conf("use_pid_deadband") Optional<Boolean> usePidDeadband();

}
