package xyz.auriium.mattlib2.yuukonfig;

import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.impl.safe.ManipulatorSafe;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.manipulation.Manipulator;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.Type;

public class LongManipulator implements ManipulatorSafe<Long> {
    final BaseManipulation manipulation;
    final RawNodeFactory factory;


    public LongManipulator(BaseManipulation manipulation, Class<?> useClass, Contextual<Type> typeContextual, RawNodeFactory factory) {
        this.manipulation = manipulation;
        this.factory = factory;
    }

    public Long deserialize(Node node) throws BadValueException {
        try {
            return Long.parseLong(node.asScalar().value());
        } catch (NumberFormatException var3) {
            throw new BadValueException("the value is not a valid double", "set the value to a number like '0.0d'", this.manipulation.configName(), node.path());
        }
    }

    public Node serializeObject(Long object, GenericPath path) {
        return this.factory.scalarOf(path, object);
    }

    public Node serializeDefault(GenericPath path) {
        return this.factory.scalarOf(path, 0L);
    }
}
