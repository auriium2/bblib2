package xyz.auriium.mattlib2;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.StubMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.any;

/**
 * Util to generate a "fake" version of any object
 */
public class MockingUtil {

    final TypeCache<?> cache = new TypeCache<>();


    static class SelfHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if (method.getReturnType().equals(Void.TYPE)) return null;
            if (method.getReturnType().equals(Optional.class)) return Optional.empty();
            if (method.getReturnType().equals(Boolean.TYPE)) return false;
            if (method.getReturnType().equals(Double.TYPE)) return 0d;


            //System.out.println("invocation of: " + method.getName());

            throw new UnsupportedOperationException("Tried to call mocked method with unsupported return type: " + method.getName());
        }
    }

    /**
     * You can only use this on public classes, otherwise it will break
     *
     * @param copy
     * @param <T>  type of class you want
     * @return a dynamic generated version of your class that will do nothing and return 0 or null wheenver it is called
     */
    public static <T> T buddy(Class<T> copy) {
        if (copy.isInterface()) {
            //TODO return dynamic proxy

            return (T) Proxy.newProxyInstance(
                    MockingUtil.class.getClassLoader(),
                    new Class[]{copy},
                    new SelfHandler());

        }

        Constructor<?>[] constructor = copy.getConstructors();
        if (constructor.length == 0) throw new IllegalStateException();


        int len = constructor[0].getParameterCount();
        Object[] nulls = new Object[len];


        DynamicType.Unloaded<T> buddy = new ByteBuddy()
                .subclass(copy, ConstructorStrategy.Default.NO_CONSTRUCTORS)
                .defineConstructor(Visibility.PUBLIC)
                .intercept(MethodCall.invoke(constructor[0])
                        .with(nulls)
                )
                .method(any())
                .intercept(StubMethod.INSTANCE)
                .make();


        try {
            Class<?> clazz = buddy.load(MockingUtil.class.getClassLoader()).getLoaded();

            return (T) clazz.newInstance();

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}