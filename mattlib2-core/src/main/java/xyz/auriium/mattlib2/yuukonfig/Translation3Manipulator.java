package xyz.auriium.mattlib2.yuukonfig;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.impl.safe.ManipulatorSafe;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.Type;

public class Translation3Manipulator implements ManipulatorSafe<Translation3d> {

    final RawNodeFactory factory;
    final BaseManipulation manipulation;


    public Translation3Manipulator(BaseManipulation baseManipulation, Class<?> aClass, Contextual<Type> typeContextual, RawNodeFactory rawNodeFactory) {
        this.factory = rawNodeFactory;
        this.manipulation = baseManipulation;
    }

    @Override
    public Translation3d deserialize(Node node) throws BadValueException {
        return new Translation3d(
                manipulation.safeDeserialize(node.asMapping().valueGuaranteed("x"), Double.class),
                manipulation.safeDeserialize(node.asMapping().valueGuaranteed("y"), Double.class),
                manipulation.safeDeserialize(node.asMapping().valueGuaranteed("z"), Double.class)
        );
    }

    @Override
    public Node serializeObject(Translation3d t, GenericPath path) {
        var mapp = factory.makeMappingBuilder(path);

        mapp.add("x", manipulation.serialize(t.getX(), double.class, path.append("x")));
        mapp.add("y", manipulation.serialize(t.getY(), double.class, path.append("y")));
        mapp.add("z", manipulation.serialize(t.getZ(), double.class, path.append("z")));

        return mapp.build();
    }

    @Override
    public Node serializeDefault(GenericPath path) {
        var mapp = factory.makeMappingBuilder(path);

        mapp.add("x", manipulation.serialize(0d, double.class, path.append("x")));
        mapp.add("y", manipulation.serialize(0d, double.class, path.append("y")));
        mapp.add("z", manipulation.serialize(0d, double.class, path.append("z")));

        return mapp.build();
    }
}
