package xyz.auriium.mattlib2.log.yuukonfig;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.impl.safe.ManipulatorSafe;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.manipulation.Manipulator;
import yuukonfig.core.node.Mapping;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.Type;

public class Pose3Manipulator implements ManipulatorSafe<Pose3d> {

    final BaseManipulation manipulation;
    final RawNodeFactory factory;

    public Pose3Manipulator(BaseManipulation baseManipulation, Class<?> aClass, Contextual<Type> typeContextual, RawNodeFactory rawNodeFactory) {
        this.manipulation = baseManipulation;
        this.factory = rawNodeFactory;
    }

    @Override
    public Pose3d deserialize(Node node) throws BadValueException {
        Mapping mp = node.asMapping();

        return new Pose3d(
                manipulation.safeDeserialize(mp.valueGuaranteed("x"), Double.class),
                manipulation.safeDeserialize(mp.valueGuaranteed("y"), Double.class),
                manipulation.safeDeserialize(mp.valueGuaranteed("z"), Double.class),
                manipulation.safeDeserialize(mp.valueGuaranteed("rot"), Rotation3d.class)
        );
    }

    @Override
    public Node serializeObject(Pose3d pose3d, GenericPath genericPath) {
        var builder = factory.makeMappingBuilder(genericPath);

        builder.add("x", factory.scalarOf(genericPath.append("x"), pose3d.getX()));
        builder.add("y", factory.scalarOf(genericPath.append("y"), pose3d.getY()));
        builder.add("z", factory.scalarOf(genericPath.append("z"), pose3d.getZ()));
        builder.add("rot", factory.scalarOf(genericPath.append("rot"), manipulation.serialize(pose3d.getRotation(), Rotation3d.class, genericPath.append("rot"))));

        return builder.build();
    }

    @Override
    public Node serializeDefault(GenericPath genericPath) {
        return serializeObject(new Pose3d(), genericPath);
    }
}
