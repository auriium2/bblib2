package yuukonfig;


import yuukonfig.core.err.BadValueException;
import yuukonfig.core.manipulation.Manipulator;
import yuukonfig.core.manipulation.Priority;
import yuukonfig.core.node.Node;
import yuukonstants.GenericPath;

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
    public Object deserialize(Node node, GenericPath s) throws BadValueException {
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
