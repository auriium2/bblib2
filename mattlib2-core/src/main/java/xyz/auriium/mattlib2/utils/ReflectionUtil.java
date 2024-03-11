package xyz.auriium.mattlib2.utils;

import xyz.auriium.mattlib2.Exceptions;
import xyz.auriium.mattlib2.log.annote.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * All one-time reflection and init codegen stuff
 */
public class ReflectionUtil {

    public interface TriConsumer<T> {
        void accept(Method method, T annotation, int id);
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

    public static void iterateClassMethodsSafely(Class<?> methodsToIterateSource, Consumer<Method> consumer) {
        for (Method method : methodsToIterateSource.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) continue;
            if (method.getDeclaringClass() == Objects.class) continue;

            consumer.accept(method);
        }
    }

    public static <T extends Annotation> void iterateClassAnnotationSafely(Class<?> methodsToIterateSource, Class<T> annotation, TriConsumer<T> consumer) {
        List<Method> methods = new ArrayList<>();

        for (Method method : methodsToIterateSource.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) continue;
            if (method.getDeclaringClass() == Objects.class) continue;
            if (!method.isAnnotationPresent(annotation)) continue;

            methods.add(method);
        }

        methods.sort(METHOD_COMPARATOR);

        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            T annot = method.getAnnotation(annotation);
            consumer.accept(method, annot, i);
        }

    }

    static final Comparator<Method> METHOD_COMPARATOR = Comparator.comparing(Method::getName);


    public static void checkMattLog(Method method) {

        int quantity = 0;
        Conf conf = method.getAnnotation(Conf.class);
        if (conf != null) quantity++;
        Log log = method.getAnnotation(Log.class);
        if (log != null) quantity++;
        Callback hasUpdated = method.getAnnotation(Callback.class);
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


    public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        int n = s.length();
        int m = t.length();

        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            final CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        final int[] p = new int[n + 1];
        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t
        int upperleft;
        int upper;

        char jOfT; // jth character of t
        int cost;

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            upperleft = p[0];
            jOfT = t.charAt(j - 1);
            p[0] = j;

            for (i = 1; i <= n; i++) {
                upper = p[i];
                cost = s.charAt(i - 1) == jOfT ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperleft + cost);
                upperleft = upper;
            }
        }

        return p[n];
    }

}
