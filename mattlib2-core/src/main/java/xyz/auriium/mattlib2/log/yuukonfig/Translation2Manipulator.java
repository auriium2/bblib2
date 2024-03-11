package xyz.auriium.mattlib2.log.yuukonfig;

import edu.wpi.first.math.geometry.Translation2d;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.impl.safe.ManipulatorSafe;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.Type;

public class Translation2Manipulator implements ManipulatorSafe<Translation2d> {

    final RawNodeFactory factory;
    final BaseManipulation manipulation;


    public Translation2Manipulator(BaseManipulation baseManipulation, Class<?> aClass, Contextual<Type> typeContextual, RawNodeFactory rawNodeFactory) {
        this.factory = rawNodeFactory;
        this.manipulation = baseManipulation;
    }

    @Override
    public Translation2d deserialize(Node node) throws BadValueException {
        return new Translation2d(
                manipulation.safeDeserialize(node.asMapping().valueGuaranteed("x"), Double.class),
                manipulation.safeDeserialize(node.asMapping().valueGuaranteed("y"), Double.class)
        );
    }

    @Override
    public Node serializeObject(Translation2d translation2d, GenericPath path) {
        var mapp = factory.makeMappingBuilder(path);

        mapp.add("x", manipulation.serialize(translation2d.getX(), double.class, path.append("x")));
        mapp.add("y", manipulation.serialize(translation2d.getY(), double.class, path.append("y")));

        return mapp.build();
    }

    @Override
    public Node serializeDefault(GenericPath path) {
        var mapp = factory.makeMappingBuilder(path);

        mapp.add("x", manipulation.serialize(0d, double.class, path.append("x")));
        mapp.add("y", manipulation.serialize(0d, double.class, path.append("y")));

        return mapp.build();
    }
}
