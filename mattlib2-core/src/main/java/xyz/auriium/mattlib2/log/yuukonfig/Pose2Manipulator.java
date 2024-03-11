package xyz.auriium.mattlib2.log.yuukonfig;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.impl.safe.ManipulatorSafe;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.node.Mapping;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.Type;

public class Pose2Manipulator implements ManipulatorSafe<Pose2d> {

    final BaseManipulation manipulation;
    final RawNodeFactory factory;


    public Pose2Manipulator(BaseManipulation baseManipulation, Class<?> aClass, Contextual<Type> typeContextual, RawNodeFactory rawNodeFactory) {
        this.manipulation = baseManipulation;
        this.factory = rawNodeFactory;
    }

    @Override
    public Pose2d deserialize(Node node) throws BadValueException {
        Mapping mp = node.asMapping();

        double x = manipulation.safeDeserialize(mp.valueGuaranteed("x"), Double.class);
        double y = manipulation.safeDeserialize(mp.valueGuaranteed("y"), Double.class);
        Rotation2d theta_rotations = manipulation.safeDeserialize(mp.valueGuaranteed("theta"), Rotation2d.class);


        return new Pose2d(x,y, theta_rotations);
    }

    @Override
    public Node serializeObject(Pose2d pose2d, GenericPath path) {
        var mapp = factory.makeMappingBuilder(path);

        mapp.add("x", manipulation.serialize(pose2d.getX(), double.class, path.append("x")));
        mapp.add("y", manipulation.serialize(pose2d.getY(), double.class, path.append("y")));
        mapp.add("theta", manipulation.serialize(pose2d.getRotation(), Rotation2d.class, path.append("theta")));

        return mapp.build();
    }

    @Override
    public Node serializeDefault(GenericPath path) {
        var mapp = factory.makeMappingBuilder(path);

        mapp.add("x", manipulation.serialize(0d, double.class, path.append("x")));
        mapp.add("y", manipulation.serialize(0d, double.class, path.append("y")));
        mapp.add("theta", manipulation.serialize(Rotation2d.fromRotations(0), Rotation2d.class, path.append("theta")));

        return mapp.build();
    }
}
