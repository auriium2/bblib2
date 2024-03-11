package xyz.auriium.mattlib2.log;


import xyz.auriium.mattlib2.Exceptions;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.manipulation.Manipulator;
import yuukonfig.core.manipulation.ManipulatorConstructor;
import yuukonfig.core.manipulation.Priority;
import yuukonfig.core.node.Mapping;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * This should return Map < ProcessPath , T > where T is a typed config of ProcessPath P
 */
public class TypeMapManipulator implements Manipulator {

    final BaseManipulation manipulation;
    final Class<?> toCheck;
    final Contextual<Type> typeContextual;
    final RawNodeFactory factory;
    final ProcessMap processMap;

    public static ManipulatorConstructor GENERATE(ProcessMap map) {
        return (m,t,c,f) -> new TypeMapManipulator(m,t,c,f,map);
    }


    public TypeMapManipulator(BaseManipulation manipulation, Class<?> toCheck, Contextual<Type> typeContextual, RawNodeFactory factory, ProcessMap loadAs) {
        this.manipulation = manipulation;
        this.toCheck = toCheck;
        this.typeContextual = typeContextual;
        this.factory = factory;
        this.processMap = loadAs;
    }

    @Override
    public int handles() {
        if (toCheck.isAssignableFrom(TypeMap.class)) return Priority.HANDLE;

        return Priority.DONT_HANDLE;
    }

    @Override
    public Object deserialize(Node node) throws BadValueException {
        Map<ProcessPath, Object> toReturnMap = new HashMap<>();
        Mapping root = node.asMapping();

        for (int i = 0; i < processMap.size(); i++) {
            ProcessPath path = processMap.pathArray[i];
            Class<?> type = processMap.clazzArray[i];

            Node drillNode = drillToNode(factory, root, path);
            Object configObject = manipulation.deserialize(
                    drillNode,
                    type
            );


            toReturnMap.put(path, configObject);
        }

        return new TypeMap(toReturnMap);
    }

    /**
     *
     * @param factory
     * @param root
     * @param pathToMatchWith Given a path p this will look inside mapping root to find that node inside of root
     * @return the node if located, or an empty node with the original (incorrect) path if not located.
     */
    public static Node drillToNode(RawNodeFactory factory, Mapping root, ProcessPath pathToMatchWith) {
        if (pathToMatchWith.length() == 0) {return root; }

        String[] internalArray = pathToMatchWith.asArray(); int useIndex = 0;
        Node closestToTheTruth = root.valuePossiblyMissing(internalArray[useIndex]); useIndex++;

        //System.out.println(closestToTheTruth.type() + " is origin type");
        if (closestToTheTruth.type() == Node.Type.NOT_PRESENT) {
            //System.out.println("strange happenings: " +pathToMatchWith.tablePath());
            return factory.notPresentOf(pathToMatchWith);
        }

        while (useIndex < internalArray.length) {
            closestToTheTruth = closestToTheTruth.asMapping().valuePossiblyMissing(internalArray[useIndex]);
            if (closestToTheTruth.type() == Node.Type.NOT_PRESENT) {

                //System.out.println("strange happenings: " +pathToMatchWith.tablePath());
                return factory.notPresentOf(pathToMatchWith);
            }
            useIndex++;
        }

      //  System.out.println(closestToTheTruth.path().tablePath() + " at, located " + pathToMatchWith.tablePath() + " with index of " + useIndex + " with type " + closestToTheTruth.type().name());

        return closestToTheTruth;
    }


    @Override
    public Node serializeObject(Object object, GenericPath path) {
        throw new UnsupportedOperationException();
    }


    //we know the 'path is clear' lets spam that fucker
    public static Mapping recursivelySerialize(RawNodeFactory factory, ProcessPath path, int index, Node toAdd) {
        String currentKey = path.asArray()[index];

        if (index == path.maxIndex()) {
            var builder = factory.makeMappingBuilder(path);
            builder.add(currentKey, toAdd);
            return builder.build();
        } else {
            var builder = factory.makeMappingBuilder(path);
            builder.add(currentKey, recursivelySerialize(factory, path, index+1, toAdd));
            return builder.build();
        }
    }

    @Override
    public Node serializeDefault(GenericPath rootPath) {
        Mapping mappingToWorkWith = factory.makeMappingBuilder(rootPath).build();

        //DONT USE THE FUCKING ROOT PATH
        for (int i = 0; i < processMap.size(); i++) {
            ProcessPath path = processMap.pathArray[i];
            Class<?> type = processMap.clazzArray[i];

            Node serializedNode = manipulation.serializeDefaultCtx(type, path);
            if (serializedNode.type() != Node.Type.MAPPING && serializedNode.type() != Node.Type.NOT_PRESENT) throw Exceptions.NODE_NOT_MAP(path); //all serialized should be maps

            mappingToWorkWith = factory.mergeMappings(mappingToWorkWith, recursivelySerialize(factory, path, 0, serializedNode));
        }

        return mappingToWorkWith;
    }

}
