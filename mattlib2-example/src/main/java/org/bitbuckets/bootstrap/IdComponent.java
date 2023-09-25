package org.bitbuckets.bootstrap;

import edu.wpi.first.math.controller.PIDController;
import xyz.auriium.mattlib2.annotation.Conf;
import xyz.auriium.mattlib2.annotation.Tune;


public interface IdComponent {

    @Conf
    int id();

    @Tune
    int id2();

}
