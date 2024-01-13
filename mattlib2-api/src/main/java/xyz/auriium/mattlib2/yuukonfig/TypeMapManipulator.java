package xyz.auriium.mattlib2.yuukonfig;


import edu.wpi.first.units.UnitBuilder;
import xyz.auriium.mattlib2.Exceptions;
import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.mattlib2.log.TypeMap;
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
    public Object deserialize(Node node, GenericPath exceptionalKey) throws BadValueException {

        Map<ProcessPath, Object> toReturnMap = new HashMap<>();
        Mapping root = node.asMapping();

        for (Map.Entry<ProcessPath, Class<?>> subConfig : loadAs.entrySet()) {

            ProcessPath path = subConfig.getKey();
            Class<?> type = subConfig.getValue();

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

/*
    public void doSomethingRecursive(Map<ProcessPath, Node> map, RawNodeFactory.MappingBuilder root, Node serializedNode, ProcessPath pathToTry) {
        Node node = map.get(pathToTry);

        if (node == null || node.isEmpty()) {
            map.put()
        }


        factory.mergeMappingBuilder().build();

        if (builder == null) {
            var path = pathToTry.goOneBack();
            if (path.isEmpty()) {
                root.add(pathToTry.getTail(), serializedNode.asMapping());

                return;
            }

            if (validToTakeBefore(pathToTry)) {
                doSomethingRecursive(map, root, serializedNode, oneBefore(pathToTry));
            }




        }





    }*/

    @Override
    public Node serializeDefault(String[] comment) {
        RawNodeFactory.MappingBuilder root = factory.makeMappingBuilder();


        ProcessPath knownPath = null;
        Map<GenericPath, RawNodeFactory.MappingBuilder> fillAss = new HashMap<>();

        RawNodeFactory.MappingBuilder builder = fillAss.get(knownPath);
        if (builder == null) {





        }




        for (Map.Entry<ProcessPath, Class<?>> entry : loadAs.entrySet()) {

            Node serializedNode = manipulation.serializeDefault(entry.getValue(), new String[0] );

            //System.out.println("e" + serializedNode.toString());
            String[] internalArray = entry.getKey().asArray();



            //TODO this doesn't work for nodes that are long...

            for (int i = internalArray.length - 1; i >= 0; i--) {
                root.add(
                        internalArray[i],
                        serializedNode
                );
            }
        }


        return root.build();
    }
/*

    public void sex(Map<ProcessPath, RawNodeFactory.MappingBuilder> mappingBuilderMap, ProcessPath previousPath) {

        swerve
        "swerve/house" int

                "swerve/house"


        Map<String, Map> recursiveMap = new HashMap<>();

        for (String s : previousPath)


        mappingBuilderMap.computeIfAbsent(previousPath, p -> factory.makeMappingBuilder())
    }
*/

    boolean validToTakeBefore(GenericPath path) {
        return path.length() > 1;
    }

    ProcessPath oneBefore(GenericPath path) {
        return ProcessPath.of(Arrays.copyOf(path.asArray(), path.length() - 2));
    }
}
