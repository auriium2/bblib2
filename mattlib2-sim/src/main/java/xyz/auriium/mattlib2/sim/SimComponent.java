package xyz.auriium.mattlib2.sim;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annote.Conf;

public interface SimComponent extends INetworkedComponent {

    @Conf("rotational_inertia") double massMomentInertia();
    @Conf("stdv_pos") double positionStandardDeviation();
    @Conf("stdv_vel") double velocityStandardDeviation();

}
