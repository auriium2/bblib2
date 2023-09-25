package org.bitbuckets.bootstrap;

import edu.wpi.first.wpilibj.TimedRobot;
import xyz.auriium.mattlib2.MattLog;
import xyz.auriium.mattlib2.annotation.Conf;
import xyz.auriium.mattlib2.annotation.Log;
import xyz.auriium.mattlib2.components.IComponent;
import xyz.auriium.mattlib2.components.impl.MotorComponent;
import xyz.auriium.mattlib2.components.impl.PIDComponent;
import xyz.auriium.mattlib2.nt.NetworkLogFeature;
import xyz.auriium.mattlib2.nt.NetworkTuneFeature;

import java.util.concurrent.CompletableFuture;

public class MyRobot extends TimedRobot {

    static final MattLog LOG = new MattLog(new NetworkLogFeature(), new NetworkTuneFeature());
    static final SomeComponent component2 = LOG.loadWaiting(SomeComponent.class, "rightSomeComponent");




    @Override
    public void robotInit() {


        LOG.init();



        component2.logSomeValue(2);


        try {
            Thread.sleep(1000);
        }catch (Exception e) {

        }




    }

}
