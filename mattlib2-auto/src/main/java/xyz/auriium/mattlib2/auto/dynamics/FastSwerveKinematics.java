package xyz.auriium.mattlib2.auto.dynamics;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.ojalgo.matrix.MatrixR064;
import org.ojalgo.matrix.store.Primitive64Store;

public class FastSwerveKinematics {

    final MatrixR064 inverseKinematics;
    final MatrixR064 forwardKinematics_pseudo;

    public FastSwerveKinematics(MatrixR064 inverseKinematics, MatrixR064 forwardKinematics_pseudo) {
        this.inverseKinematics = inverseKinematics;
        this.forwardKinematics_pseudo = forwardKinematics_pseudo;
    }

    public FastSwerveKinematics load(Translation2d[] swerveModulePositionOffsets_four) {
        //Generate 1st order inverse kinematic matrix

        var inverseKinematicBuilder = MatrixR064.FACTORY.makeDense(8,3);

        for (int row = 0; row < 4; row++) {
            //convert chassis x,y,theta to module [row]'s x
            inverseKinematicBuilder.set(row * 2, 0, 1);
            inverseKinematicBuilder.set(row * 2, 1, 0);
            inverseKinematicBuilder.set(row * 2, 2, -swerveModulePositionOffsets_four[row].getY());

            //convert chassis x,y,theta to module [row]'s y
            inverseKinematicBuilder.set(row * 2 + 1, 0, 0);
            inverseKinematicBuilder.set(row * 2 + 1, 1, 1);
            inverseKinematicBuilder.set(row * 2 + 1, 2, swerveModulePositionOffsets_four[row].getX());
        }

        MatrixR064 inverseKinematics = inverseKinematicBuilder.get();
        MatrixR064 forwardKinematics_pseudo = inverseKinematics.invert();

        return new FastSwerveKinematics(inverseKinematics, forwardKinematics_pseudo);
    }


    public SwerveModuleState[] convertCentroidStateToModuleStateSafe(ChassisSpeeds speeds) {
        MatrixR064 centroidStateVector = MatrixR064.FACTORY.column(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond, speeds.omegaRadiansPerSecond);
        MatrixR064 moduleStateVector = convertCentroidStateToModuleState(centroidStateVector); //compiler inlines this

        SwerveModuleState[] locallyAllocatedStates = new SwerveModuleState[4]; //TODO reduce object allocations

        for (int i = 0; i < 4; i++) {
            double x = moduleStateVector.doubleValue(2 * i);
            double y = moduleStateVector.doubleValue(2 * i + 1);

            double velocity = Math.hypot(x,y);
            Rotation2d angle = new Rotation2d(x,y);

            locallyAllocatedStates[i] = new SwerveModuleState(velocity, angle);
        }

        return locallyAllocatedStates;

    }

    public ChassisSpeeds convertModuleStateToCentroidState(SwerveModuleState[] states) {
        MatrixR064 moduleStateVector = MatrixR064.FACTORY.column(
                states[0].speedMetersPerSecond * states[0].angle.getSin(),
                states[0].speedMetersPerSecond * states[0].angle.getCos(),
                states[1].speedMetersPerSecond * states[1].angle.getSin(),
                states[1].speedMetersPerSecond * states[1].angle.getCos(),
                states[2].speedMetersPerSecond * states[2].angle.getSin(),
                states[2].speedMetersPerSecond * states[2].angle.getCos(),
                states[3].speedMetersPerSecond * states[3].angle.getSin(),
                states[3].speedMetersPerSecond * states[3].angle.getCos()
        );

        MatrixR064 centroidStateVector = convertModuleStateToCentroidState(moduleStateVector);

        return new ChassisSpeeds(
                centroidStateVector.doubleValue(0),
                centroidStateVector.doubleValue(1),
                centroidStateVector.doubleValue(2)
        );
    }

    /**
     *
     * @param centroidVelocityVector 3x1 matrix of [x,y,theta]
     * @return 8x1 matrix of module states [x,y,x,y,x,y,x,y]
     */

    public MatrixR064 convertCentroidStateToModuleState(MatrixR064 centroidVelocityVector) {
        return inverseKinematics.multiply(centroidVelocityVector);
    }

    /**
     *
     * @param moduleVelocityVector 8x1 matrix of module states [x,y,x,y,x,y,x,y]
     * @return 3x1 matrix of x,y,theta
     */
    public MatrixR064 convertModuleStateToCentroidState(MatrixR064 moduleVelocityVector) {
        return forwardKinematics_pseudo.multiply(moduleVelocityVector);
    }

}
