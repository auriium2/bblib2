package org.bitbuckets.bootstrap;

import edu.wpi.first.wpilibj.RobotBase;
import xyz.auriium.mattlib2.utils.ExceptionUtil;

public class Main {

    public static void main(String[] args) {
        RobotBase.startRobot(ExceptionUtil.wrapExceptionalSupplier(MyRobot::new));
    }

}
