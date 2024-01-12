package xyz.auriium.mattlib2.utils;

import xyz.auriium.mattlib2.Mattlib2Exception;
import xyz.auriium.mattlib2.log.annote.Conf;
import xyz.auriium.mattlib2.log.annote.Log;
import xyz.auriium.mattlib2.log.annote.Tune;

import java.lang.reflect.Method;

public class ReflectionUtil {

    public static String getKey(Method method) {
        Conf conf = method.getAnnotation(Conf.class);
        if (conf != null) return conf.value();
        Log log = method.getAnnotation(Log.class);
        if (log != null) return log.value();
        Tune tune = method.getAnnotation(Tune.class);
        if (tune != null) return tune.value();

        return method.getName();
    }

    public static void check(Method method) {

        int quantity = 0;
        Conf conf = method.getAnnotation(Conf.class);
        if (conf != null) quantity++;
        Log log = method.getAnnotation(Log.class);
        if (log != null) quantity++;
        Tune tune = method.getAnnotation(Tune.class);
        if (tune != null) quantity++;

        if (quantity > 1) {
            throw new Mattlib2Exception(
                    "tooManyAnnotations",
                    String.format("too many mattlib2 annotations on method %s in class %s ", method.getName(), method.getDeclaringClass().getSimpleName()),
                    "please use only one of @log, @tune, or @conf on it"
            );
        }


        if (method.getAnnotations().length == 0) {
            throw new Mattlib2Exception(
                    "noAnnotationOnMethod",
                    String.format("no mattlib2 annotations on method %s in class %s ", method.getName(), method.getDeclaringClass().getSimpleName()),
                    "please use one of @log, @tune, or @conf on it"
            );
        }

        if ((conf != null || tune != null) && method.getParameterCount() != 0) {
            throw new Mattlib2Exception(
                    "badConfOrTune",
                    String.format("the method %s in class %s cannot have parameters", method.getName(), method.getDeclaringClass().getSimpleName()),
                    "please remove parameters or do not declare it as @conf or @tune"
            );
        }

        if (log != null && method.getParameterCount() == 0) {
            throw new Mattlib2Exception(
                    "badLog",
                    String.format("method %s in class %s must have parameters", method.getName(), method.getDeclaringClass().getSimpleName()),
                    "please add parameters or do not declare @log"
            );
        }


        if (log != null && method.getReturnType() != void.class) {
            throw new Mattlib2Exception(
                    "badReturnType",
                    String.format("return type of method %s in class %s is not void, but is log", method.getName(), method.getDeclaringClass().getSimpleName()),
                    "please set the return type to void"
            );
        }
    }

}
