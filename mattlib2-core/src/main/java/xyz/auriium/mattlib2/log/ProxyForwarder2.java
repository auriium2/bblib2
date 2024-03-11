package xyz.auriium.mattlib2.log;

import yuukonfig.core.impl.manipulator.section.Forwarder;
import yuukonfig.core.impl.manipulator.section.ImpossibleAccessException;
import yuukonfig.core.impl.manipulator.section.InvocationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProxyForwarder2 implements Forwarder {
    private final Method method;
    private final Object toInvokeOn;

    public ProxyForwarder2(Method method, Object toInvokeOn) {
        this.method = method;
        this.toInvokeOn = toInvokeOn;
    }

    public Object invoke() {
        try {
            return this.method.invoke(this.toInvokeOn);
        } catch (InvocationTargetException | IllegalAccessException var2) {
            if (var2.getMessage() == null) {
                throw new ImpossibleAccessException(String.format("The config interface %s must be public for YuuKonfig to read it! And: %s", this.method.getDeclaringClass().getName(), var2.getLocalizedMessage()));
            } else if (var2.getMessage().contains("cannot access a member of interface")) {
                throw new ImpossibleAccessException(String.format("The config interface %s must be public for YuuKonfig to read it!", this.method.getDeclaringClass().getName()));
            } else {
                throw new InvocationException(String.format("An exception was thrown while trying to access or serialize the config! %s", var2));
            }
        }
    }
}