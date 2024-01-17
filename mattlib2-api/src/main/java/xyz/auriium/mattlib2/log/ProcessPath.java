package xyz.auriium.mattlib2.log;


import xyz.auriium.yuukonstants.GenericPath;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents a "path" of components, where /higher/things are parents to the children components
 * example: /swerve/motor1/pid
 *
 */
public class ProcessPath extends GenericPath {

    public ProcessPath(String[] pathComposition) {
        super(pathComposition);
    }

    public Optional<ProcessPath> goOneBack() {
        String[] contents = this.asArray();

        if (contents.length < 1) return Optional.empty();
        return Optional.of(new ProcessPath(Arrays.copyOf(contents, contents.length - 1)));
    }

    public static ProcessPath parse(String stringWithSlash) {
        return of(stringWithSlash.split("/"));
    }

    public static ProcessPath of(String... strings) {
        return new ProcessPath(strings);
    }


    public static ProcessPath ofGeneric(GenericPath genericPath) {
        return new ProcessPath(genericPath.asArray());
    }

}
