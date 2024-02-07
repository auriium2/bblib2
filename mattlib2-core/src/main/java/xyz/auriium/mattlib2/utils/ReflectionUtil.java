package xyz.auriium.mattlib2.utils;

import xyz.auriium.mattlib2.Exceptions;
import xyz.auriium.mattlib2.log.annote.*;

import java.lang.reflect.Method;

/**
 * All one-time reflection and init codegen stuff
 */
public class ReflectionUtil {

    static {

    }

    public static String getKey(Method method) {
        Conf conf = method.getAnnotation(Conf.class);
        if (conf != null) return conf.value();
        Log log = method.getAnnotation(Log.class);
        if (log != null) return log.value();
        Tune tune = method.getAnnotation(Tune.class);
        if (tune != null) return tune.value();

        return method.getName();
    }


    public static void checkMattLog(Method method) {

        int quantity = 0;
        Conf conf = method.getAnnotation(Conf.class);
        if (conf != null) quantity++;
        Log log = method.getAnnotation(Log.class);
        if (log != null) quantity++;
        HasUpdated hasUpdated = method.getAnnotation(HasUpdated.class);
        if (hasUpdated != null) quantity++;
        Tune tune = method.getAnnotation(Tune.class);
        if (tune != null) quantity++;
        SelfPath selfPath = method.getAnnotation(SelfPath.class);
        if (selfPath != null) quantity++;

        String methodName = method.getName();
        String simpleName = method.getDeclaringClass().getSimpleName();

        if (quantity > 1) {
            throw xyz.auriium.mattlib2.Exceptions.TOO_MANY_ANNOTATIONS(methodName, simpleName);
        }

        if (quantity == 0) {
            throw xyz.auriium.mattlib2.Exceptions.NO_ANNOTATIONS_ON_METHOD(methodName, simpleName);
        }

        if ((conf != null || tune != null) && method.getParameterCount() != 0) {
            throw xyz.auriium.mattlib2.Exceptions.BAD_CONF_OR_TUNE(methodName, simpleName);
        }

        if (log != null && (method.getParameterCount() == 0 || method.getParameterCount() > 1)) {
            throw xyz.auriium.mattlib2.Exceptions.BAD_LOG(methodName, simpleName);
        }

        if (log != null && method.getReturnType() != void.class) {
            throw Exceptions.BAD_RETURN_TYPE(methodName, simpleName);
        }
    }

}
