package xyz.auriium.mattlib2.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Util for generating an incremental ID for class initialization
 */
public class ArbitraryIDUtil {

    final static Map<Class<?>,Integer> classMap = new HashMap<>();


    public static int nextIDFor(Class<?> clazz) {
        int current = classMap.computeIfAbsent(clazz,i->0);
        int next = current + 1;
        classMap.put(clazz, next);

        return next;
    }

}
