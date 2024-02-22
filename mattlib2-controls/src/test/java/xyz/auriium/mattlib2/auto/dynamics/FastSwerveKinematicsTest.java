package xyz.auriium.mattlib2.auto.dynamics;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FastSwerveKinematicsTest {


    @Disabled
    @Test
    void convertCentroidStateToModuleStateSafe() {

        Translation2d[] td = new Translation2d[]{
                new Translation2d(0.2286, 0.3429),
                new Translation2d(0.2286, -0.3429),
                new Translation2d(-0.3556, 0.3429),
                new Translation2d(-0.3556, -0.3429)
        };

        SwerveDriveKinematics swerveDriveKinematics = new SwerveDriveKinematics(
                td
        );

        var original = swerveDriveKinematics.toSwerveModuleStates(new ChassisSpeeds(3,2,0));

        FastSwerveKinematics fastSwerveKinematics = FastSwerveKinematics.load(
                td
        );

        var converted = fastSwerveKinematics.convertCentroidStateToModuleStateSafe(new ChassisSpeeds(3,2,0));

        for (int i = 0; i < original.length; i++) {
            Assertions.assertEquals(original[i].speedMetersPerSecond, converted[i].speedMetersPerSecond, 0.01);
            Assertions.assertEquals(original[i].angle.getRadians(), converted[i].angle.getRadians(), 0.01);
        }
    }

    @Test
    void convertModuleStateToCentroidState() {
    }

    @Test
    void convertCentroidStateToModuleState() {
    }

    @Test
    void testConvertModuleStateToCentroidState() {
    }
}