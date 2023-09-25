package xyz.auriium.yuukonfig;


import xyz.auriium.mattlib2.Dirty;
import xyz.auriium.yuukonfig.core.impl.manipulator.section.ImpossibleAccessException;
import xyz.auriium.yuukonfig.core.impl.manipulator.section.InvocationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Dirty
public class CustomForwarder {

    private final Method method;
    private final Object toInvokeOn;

    CustomForwarder(Method method, Object toInvokeOn) {
        this.method = method;
        this.toInvokeOn = toInvokeOn;
    }

    public Object invoke() {
        try {
            return method.invoke(toInvokeOn);
        } catch (IllegalAccessException | InvocationTargetException e) {

            if (e.getMessage().contains("cannot access a member of interface")) {
                throw new ImpossibleAccessException(
                        String.format("The config interface %s must be public for Mattlib2 to read it!", method.getDeclaringClass().getName())
                );
            }

            throw new InvocationException(
                    String.format("An exception was thrown while trying to access or serialize the config! %s", e)
            );

        }
    }

}
