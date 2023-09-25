package xyz.auriium.yuukonfig;

import xyz.auriium.yuukonfig.core.err.BadValueException;
import xyz.auriium.yuukonfig.core.manipulation.Manipulator;
import xyz.auriium.yuukonfig.core.manipulation.Priority;
import xyz.auriium.yuukonfig.core.node.Node;

public class ConditionalDoubleManipulator implements Manipulator {

    final Class<?> useType;

    public ConditionalDoubleManipulator(Class<?> useType) {
        this.useType = useType;
    }

    @Override
    public int handles() {
        if (useType == ConditionalDouble.class) return Priority.HANDLE;

        return Priority.DONT_HANDLE;
    }

    @Override
    public Object deserialize(Node node, String s) throws BadValueException {
        return null;
    }

    @Override
    public Node serializeObject(Object o, String[] strings) {
        return null;
    }

    @Override
    public Node serializeDefault(String[] strings) {
        return null;
    }
}
