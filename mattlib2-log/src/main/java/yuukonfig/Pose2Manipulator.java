package yuukonfig;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.impl.safe.ManipulatorSafe;
import yuukonfig.core.manipulation.Manipulation;
import yuukonfig.core.node.Mapping;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;
import yuukonstants.GenericPath;

public class Pose2Manipulator implements ManipulatorSafe<Pose2d> {

    final Manipulation manipulation;
    final RawNodeFactory factory;

    public Pose2Manipulator(Manipulation manipulation, RawNodeFactory factory) {
        this.manipulation = manipulation;
        this.factory = factory;
    }

    @Override
    public Pose2d deserialize(Node node, GenericPath genericPath) throws BadValueException {
        Mapping mp = node.asMapping();

        double x = (double) manipulation.deserialize(mp.value("x"), genericPath.append("x"), double.class);
        double y = (double) manipulation.deserialize(mp.value("y"), genericPath.append("y"), double.class);
        double theta_rotations = (double) manipulation.deserialize(mp.value("theta_rot"), genericPath.append("theta_rot"), double.class);


        return new Pose2d(x,y, Rotation2d.fromRotations(theta_rotations));
    }

    @Override
    public Node serializeObject(Pose2d pose2d, String[] strings) {
        var sequence = factory.makeSequenceBuilder();

        sequence.add(factory.scalarOf(pose2d.getX(), "x", new String[0]));
        sequence.add(factory.scalarOf(pose2d.getY(), "y", new String[0]));
        sequence.add(factory.scalarOf(pose2d.getRotation().getRadians(), "theta_rot", new String[0]));

        return sequence.build();
    }

    @Override
    public Node serializeDefault(String[] strings) {
        var sequence = factory.makeSequenceBuilder();

        sequence.add(factory.scalarOf(0d, "x", new String[0]));
        sequence.add(factory.scalarOf(0d, "y", new String[0]));
        sequence.add(factory.scalarOf(0d, "theta_rot", new String[0]));

        return sequence.build();
    }
}
