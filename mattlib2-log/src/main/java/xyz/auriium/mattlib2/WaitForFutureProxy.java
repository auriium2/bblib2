package xyz.auriium.mattlib2;

import edu.wpi.first.wpilibj.DriverStation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

/**
 * TODO use bytebuddy here this
 * This class exists so that you can immediately request an interface from MattLog without using futures
 * Internally it will only function if the future is loaded, which is fine because the user of the library
 * only should use MattLog IComponents if they are loaded, right?
 * @param <T>
 */
public class WaitForFutureProxy<T> implements InvocationHandler {

    final CompletableFuture<T> internal;

    public WaitForFutureProxy(CompletableFuture<T> internal) {
        this.internal = internal;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (internal.isDone()) {

            Method s = method.getDeclaringClass().getMethod(method.getName());

            T contained = internal.join();
            DriverStation.reportError(method.getName(), false);
            return s.invoke(contained, args);
        }
        throw new LogsNotReadyException("The log system has not been initialized yet!", "Run Mattlog#init before using components!");

    }
}
