package xyz.auriium.mattlib2.yuukonfig;


import edu.wpi.first.units.UnitBuilder;
import xyz.auriium.mattlib2.Exceptions;
import xyz.auriium.mattlib2.log.ProcessMap;
import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.mattlib2.log.TypeMap;
import yuukonfig.core.YuuKonfig;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.manipulation.Manipulation;
import yuukonfig.core.manipulation.Manipulator;
import yuukonfig.core.manipulation.Priority;
import yuukonfig.core.node.Mapping;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;
import yuukonstants.GenericPath;
import yuukonstants.exception.LocatedException;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This should return Map < ProcessPath , T > where T is a typed config of ProcessPath P
 */
public class TypeMapManipulator implements Manipulator {

    final Manipulation manipulation;
    final Class<?> toCheck;
    final Contextual<Type> typeContextual;
    final RawNodeFactory factory;
    final ProcessMap processMap;


    public TypeMapManipulator(Manipulation manipulation, Class<?> toCheck, Contextual<Type> typeContextual, RawNodeFactory factory, ProcessMap loadAs) {
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
    public Object deserialize(Node node, GenericPath exceptionalKey) throws BadValueException {

        Map<ProcessPath, Object> toReturnMap = new HashMap<>();
        Mapping root = node.asMapping();

        for (int i = 0; i < processMap.size(); i++) {

            ProcessPath path = processMap.pathArray[i];
            Class<?> type = processMap.clazzArray[i];

            Node drillNode = drillToNode(root, path);
            Object configObject = manipulation.deserialize(
                    drillNode,
                    path,
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
                if (root.value(internalArray[useIndex]).type() != Node.Type.MAPPING) {
                    return factory.notPresentOf(); //TODO another terrible hack
                }

                closestToTheTruth = root.yamlMapping(internalArray[useIndex]);
            } else {
                if (closestToTheTruth.asMapping().value(internalArray[useIndex]).type() == Node.Type.NOT_PRESENT) throw Exceptions.NO_TOML(path);
                closestToTheTruth = closestToTheTruth.asMapping().yamlMapping(internalArray[useIndex]);
            }

            if (closestToTheTruth == null) throw Exceptions.NO_TOML(path);



            useIndex++;
        }

        return closestToTheTruth;

    }


    @Override
    public Node serializeObject(Object object, String[] comment) {
        throw new UnsupportedOperationException();
    }


    //we know the 'path is clear' lets spam that fucker
    Mapping doOtherThing(ProcessPath path, int index, Mapping toAdd) {

        String currentKey = path.asArray()[index];

        if (index == path.maxIndex()) {
            var builder = factory.makeMappingBuilder();
            builder.add(currentKey, toAdd);
            return builder.build();
        } else {
            var builder = factory.makeMappingBuilder();
            builder.add(currentKey, doOtherThing(path, index+1, toAdd));
            return builder.build();
        }

    }

    @Override
    public Node serializeDefault(String[] comment) {


        Mapping mappingToWorkWith = factory.makeMappingBuilder().build();

        for (int i = 0; i < processMap.size(); i++) {
            ProcessPath path = processMap.pathArray[i];
            Class<?> type = processMap.clazzArray[i];


            Node serializedNode = manipulation.serializeDefault(type, new String[0] );
            if (serializedNode.type() != Node.Type.MAPPING) throw Exceptions.NODE_NOT_MAP(path); //all serialized should be maps

            mappingToWorkWith = factory.mergeMappings(mappingToWorkWith, doOtherThing(path, 0, serializedNode.asMapping()));
        }

        return mappingToWorkWith;
    }

}
