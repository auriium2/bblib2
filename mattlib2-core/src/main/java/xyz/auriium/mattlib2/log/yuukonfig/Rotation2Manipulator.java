package xyz.auriium.mattlib2.log.yuukonfig;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.impl.safe.ManipulatorSafe;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.node.Mapping;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.Type;

public class Rotation2Manipulator implements ManipulatorSafe<Rotation2d> {

    final BaseManipulation manipulation;
    final RawNodeFactory factory;


    public Rotation2Manipulator(BaseManipulation baseManipulation, Class<?> aClass, Contextual<Type> typeContextual, RawNodeFactory rawNodeFactory) {
        this.manipulation = baseManipulation;
        this.factory = rawNodeFactory;
    }

    @Override
    public Rotation2d deserialize(Node node) throws BadValueException {
        double theta_rotations = manipulation.safeDeserialize(node, Double.class);

        return Rotation2d.fromRotations(theta_rotations);
    }

    @Override
    public Node serializeObject(Rotation2d pose2d, GenericPath path) {
        return factory.scalarOf(path, pose2d.getRotations());
    }

    @Override
    public Node serializeDefault(GenericPath path) {
        return factory.scalarOf(path, 0d);
    }

}
