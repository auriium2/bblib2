package xyz.auriium.mattlib2;

import java.util.Map;

public class TypeMap {

    final Map<ProcessPath, Object> backingMap;

    public TypeMap(Map<ProcessPath, Object> backingMap) {
        this.backingMap = backingMap; //todo copy
    }

    @SuppressWarnings("unchecked")
    public <T> T requestForPath(ProcessPath path) {

        Object internal = backingMap.get(path);
        if (internal == null) throw new IllegalStateException("HOW");

        return (T) internal;

    }

    public <T> T request(Class<T> type, String... path) {
        ProcessPath toQuery = ProcessPath.of(path);

        Object ob = backingMap.get(toQuery);

        return type.cast(ob);
    }
}
