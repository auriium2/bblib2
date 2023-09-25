package xyz.auriium.yuukonfig;


import xyz.auriium.mattlib2.ProcessPath;
import xyz.auriium.mattlib2.TypeMap;
import xyz.auriium.yuukonfig.core.err.BadValueException;
import xyz.auriium.yuukonfig.core.manipulation.*;
import xyz.auriium.yuukonfig.core.node.Mapping;
import xyz.auriium.yuukonfig.core.node.Node;
import xyz.auriium.yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * This should return Map < ProcessPath , T > where T is a typed config of ProcessPath P
 */
public class TypeMapManipulator implements Manipulator {

    final Manipulation manipulation;
    final Class<?> toCheck;
    final Contextual<Type> typeContextual;
    final RawNodeFactory factory;
    final Map<ProcessPath, Class<?>> loadAs; //shitty hacl


    public TypeMapManipulator(Manipulation manipulation, Class<?> toCheck, Contextual<Type> typeContextual, RawNodeFactory factory, Map<ProcessPath, Class<?>> loadAs) {
        this.manipulation = manipulation;
        this.toCheck = toCheck;
        this.typeContextual = typeContextual;
        this.factory = factory;
        this.loadAs = loadAs;
    }

    @Override
    public int handles() {
        if (toCheck.isAssignableFrom(TypeMap.class)) return Priority.HANDLE;

        return Priority.DONT_HANDLE;
    }

    @Override
    public Object deserialize(Node node, String exceptionalKey) throws BadValueException {

        Map<ProcessPath, Object> toReturnMap = new HashMap<>();
        Mapping root = node.asMapping();

        for (Map.Entry<ProcessPath, Class<?>> subConfig : loadAs.entrySet()) {

            ProcessPath path = subConfig.getKey();
            Class<?> type = subConfig.getValue();

            Node drillNode = drillToNode(root, path);
            Object configObject = manipulation.deserialize(
                    drillNode,
                    path.getAsTablePath(),
                    type,
                    Contextual.present(null)
            );
            toReturnMap.put(path, configObject);

        }

        return new TypeMap(toReturnMap);
    }

    //TODO unit test this
    Node drillToNode(Mapping root, ProcessPath path) throws BadValueException {

        if (path.length() == 0) {
            return root;
        }

        String[] internalArray = path.asArray();
        int useIndex = 0;

        Node closestToTheTruth = null;

        while (useIndex < internalArray.length) {
            if (closestToTheTruth == null) {
                closestToTheTruth = root.yamlMapping(internalArray[useIndex]);
            } else {
                closestToTheTruth = closestToTheTruth.asMapping().yamlMapping(internalArray[useIndex]);
            }

            if (closestToTheTruth == null) throw new BadValueException(
                    manipulation.configName(),
                    internalArray[useIndex],
                    "No YAML found, please write some in!"
            );



            useIndex++;
        }

        return closestToTheTruth;

    }


    @Override
    public Node serializeObject(Object object, String[] comment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node serializeDefault(String[] comment) {
        RawNodeFactory.MappingBuilder root = factory.makeMappingBuilder();
        for (Map.Entry<ProcessPath, Class<?>> entry : loadAs.entrySet()) {

            Node serializedNode = manipulation.serializeDefault(entry.getValue(), new String[0] );

            System.out.println("e" + serializedNode.toString());
            String[] internalArray = entry.getKey().asArray();



            for (int i = internalArray.length - 1; i >= 0; i--) {
                root.add(
                        internalArray[i],
                        serializedNode
                );
            }
        }


        return root.build();
    }
}
