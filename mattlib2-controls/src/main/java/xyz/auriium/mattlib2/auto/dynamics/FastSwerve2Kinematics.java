package xyz.auriium.mattlib2.auto.dynamics;

import edu.wpi.first.math.geometry.Translation2d;
import org.ojalgo.matrix.MatrixR064;

public class FastSwerve2Kinematics extends FastSwerveKinematics {

    final MatrixR064 inverse2Kinematics;

    public FastSwerve2Kinematics(MatrixR064 inverseKinematics, MatrixR064 forwardKinematics_pseudo, MatrixR064 inverse2Kinematics) {
        super(inverseKinematics, forwardKinematics_pseudo);
        this.inverse2Kinematics = inverse2Kinematics;
    }


    public static FastSwerveKinematics load(Translation2d[] swerveModulePositionOffsets_four) {
        //Generate 1st order inverse kinematic matrix

        var inverseKinematicBuilder = MatrixR064.FACTORY.makeDense(8,3);
        var inverseKinematicBuilder2 = MatrixR064.FACTORY.makeDense(8,4); //ax ay w^2 wdot

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
        MatrixR064 forwardKinematics_pseudo = (inverseKinematics.transpose().multiply(inverseKinematics)).invert().multiply(inverseKinematics.transpose());

        //FUCKING GIVE ME THE MOORE PERSIJISNINFOSNFOSNFAISNFO
        //DUMB SHIT

        return new FastSwerveKinematics(inverseKinematics, forwardKinematics_pseudo);
    }

}
