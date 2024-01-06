package xyz.auriium.mattlib2.log;

import yuukonstants.GenericPath;

/**
 * Specific GenericPath to keep people from loading directly from config
 */
public class ProcessPath extends GenericPath {

    public ProcessPath(String[] pathComposition) {
        super(pathComposition);
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
