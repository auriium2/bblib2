package xyz.auriium.mattlib2.yuukonfig;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Quaternion;
import edu.wpi.first.math.geometry.Rotation3d;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.impl.safe.ManipulatorSafe;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.node.Mapping;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.Type;

public class Rotation3Manipulator implements ManipulatorSafe<Rotation3d> {

    final BaseManipulation manipulation;
    final RawNodeFactory factory;

    public Rotation3Manipulator(BaseManipulation baseManipulation, Class<?> aClass, Contextual<Type> typeContextual, RawNodeFactory rawNodeFactory) {
        this.manipulation = baseManipulation;
        this.factory = rawNodeFactory;
    }


    @Override public Rotation3d deserialize(Node node) throws BadValueException {
        Mapping mp = node.asMapping();

        return new Rotation3d(
                new Quaternion(
                        manipulation.safeDeserialize(mp.valueGuaranteed("x"), Double.class),
                        manipulation.safeDeserialize(mp.valueGuaranteed("y"), Double.class),
                        manipulation.safeDeserialize(mp.valueGuaranteed("z"), Double.class),
                        manipulation.safeDeserialize(mp.valueGuaranteed("w"), Double.class)
                )
        );
    }

    @Override public Node serializeObject(Rotation3d rotation3d, GenericPath genericPath) {
        var builder = factory.makeMappingBuilder(genericPath);

        builder.add("x", factory.scalarOf(genericPath.append("x"), rotation3d.getQuaternion().getX()));
        builder.add("y", factory.scalarOf(genericPath.append("y"), rotation3d.getQuaternion().getY()));
        builder.add("z", factory.scalarOf(genericPath.append("z"), rotation3d.getQuaternion().getZ()));
        builder.add("w", factory.scalarOf(genericPath.append("w"), rotation3d.getQuaternion().getW()));

        return builder.build();
    }

    @Override public Node serializeDefault(GenericPath genericPath) {
        return serializeObject(new Rotation3d(), genericPath);
    }
}
