package xyz.auriium.mattlib2;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class
 */
public class ProcessPath {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessPath that = (ProcessPath) o;
        return Arrays.equals(pathComposition, that.pathComposition);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pathComposition);
    }

    public static ProcessPath of(String... strings) {
        return new ProcessPath(strings)
;    }

    public static ProcessPath parse(String stringWithSlash) {
        return ProcessPath.of(stringWithSlash.split("/"));
    }

    final String[] pathComposition;
    //final UUID uuid;

    public ProcessPath(String[] pathComposition) {
        this.pathComposition = pathComposition;
    }

    public String getTail() {
        return pathComposition[pathComposition.length - 1];
    }

    public String getAsTablePath() {
        StringBuilder builder = new StringBuilder();

        for (String string : pathComposition) {
            builder.append(string).append("/");
        }

        return builder.toString();
    }

    public String getAsFlatTablePath() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < pathComposition.length; i++) {
            if (i == pathComposition.length - 1) {
                builder.append(pathComposition[i]);
            } else {
                builder.append(pathComposition[i]).append(" ");
            }
        }

        return builder.toString();
    }

    public Optional<String> getAsLastTwoPath() {
        if (pathComposition.length < 2) {
            return Optional.empty();
        }

        int lastIndex = pathComposition.length - 1;
        int secondLastIndex = lastIndex - 1;

        String toReturn = pathComposition[secondLastIndex] + "/" + pathComposition[lastIndex];
        return Optional.of(toReturn);

    }


    public Optional<String> getAsLastTwoPathFlat() {
        if (pathComposition.length < 2) {
            return Optional.empty();
        }

        int lastIndex = pathComposition.length - 1;
        int secondLastIndex = lastIndex - 1;

        String toReturn = pathComposition[secondLastIndex] + " " + pathComposition[lastIndex];
        return Optional.of(toReturn);
    }

    public ProcessPath sibling(String sibling) {
        if (sibling == null) throw new IllegalStateException("what");

        if (pathComposition.length == 0) {
            return new ProcessPath(new String[] {sibling} );
        }

        String[] toReturnArray = pathComposition.clone();
        toReturnArray[pathComposition.length - 1] = sibling;

        return new ProcessPath(toReturnArray);
    }

    public ProcessPath append(String next) {
        if (next == null) throw new IllegalStateException("what");

        if (pathComposition.length == 0) {
            return new ProcessPath(new String[] {next} );
        }

        String[] toReturnArray = new String[pathComposition.length + 1];
        for (int i = 0; i < pathComposition.length; i++) {
            toReturnArray[i] = pathComposition[i];
        }

        toReturnArray[pathComposition.length] = next;

        return new ProcessPath(toReturnArray);
    }

    public int length() {
        return pathComposition.length;
    }

    public String getUniqueName(String name, Function<String, Boolean> toCheck) {

        int startAt = pathComposition.length - 1;

        if (!toCheck.apply(name)) {
            startAt -= 1;
        }

        var compound = Arrays.copyOfRange(pathComposition, startAt, pathComposition.length);

        StringBuilder use = new StringBuilder();

        for (String str : compound) {
            use.append(str).append("/");
        }

        return use.toString();
    }

    public String[] asArray() {
        return this.pathComposition;
    }


}
