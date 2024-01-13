package xyz.auriium.mattlib2.log;

import xyz.auriium.mattlib2.utils.BufferUtils;

public class ProcessMap {

    public final ProcessPath[] pathArray;
    public final Class<?>[] clazzArray;

    public ProcessMap(ProcessPath[] pathArray, Class<?>[] clazzArray) {
        this.pathArray = pathArray;
        this.clazzArray = clazzArray;
    }

    public ProcessMap() {
        pathArray = new ProcessPath[0];
        clazzArray = new Class<?>[0];
    }

    public int size() {
        return pathArray.length;
    }

    public ProcessMap with(ProcessPath path, Class<?> clazz) {
        return new ProcessMap(BufferUtils.add(pathArray, path), BufferUtils.add(clazzArray, clazz));
    }
}
