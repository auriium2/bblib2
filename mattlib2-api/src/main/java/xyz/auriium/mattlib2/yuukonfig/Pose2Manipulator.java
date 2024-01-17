package xyz.auriium.mattlib2.yuukonfig;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.impl.safe.ManipulatorSafe;
import yuukonfig.core.node.Mapping;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;

public class Pose2Manipulator implements ManipulatorSafe<Pose2d> {

    final BaseManipulation manipulation;
    final RawNodeFactory factory;

    public Pose2Manipulator(BaseManipulation manipulation, RawNodeFactory factory) {
        this.manipulation = manipulation;
        this.factory = factory;
    }

    @Override
    public Pose2d deserialize(Node node) throws BadValueException {
        Mapping mp = node.asMapping();

        double x = manipulation.safeDeserialize(mp.valueGuaranteed("x"), double.class);
        double y = manipulation.safeDeserialize(mp.valueGuaranteed("y"), double.class);
        double theta_rotations = manipulation.safeDeserialize(mp.valueGuaranteed("theta"), double.class);


        return new Pose2d(x,y, Rotation2d.fromRotations(theta_rotations));
    }

    @Override
    public Node serializeObject(Pose2d pose2d, GenericPath path) {
        var mapp = factory.makeMappingBuilder(path);

        mapp.add("x", manipulation.serialize(pose2d.getX(), double.class, path.append("x")));
        mapp.add("y", manipulation.serialize(pose2d.getY(), double.class, path.append("y")));
        mapp.add("theta", manipulation.serialize(pose2d.getRotation().getRotations(), double.class, path.append("theta")));

        return mapp.build();
    }

    @Override
    public Node serializeDefault(GenericPath path) {
        var mapp = factory.makeMappingBuilder(path);

        mapp.add("x", manipulation.serialize(0d, double.class, path.append("x")));
        mapp.add("y", manipulation.serialize(0d, double.class, path.append("y")));
        mapp.add("theta", manipulation.serialize(0d, double.class, path.append("theta")));

        return mapp.build();
    }
}
