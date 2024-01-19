package xyz.auriium.mattlib2;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import org.junit.jupiter.api.Test;
import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.yuukonstants.GenericPath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Scratchpad {

    public interface Interface2 extends MyComponent {

    }

    public interface MyComponent extends INetworkedComponent {
        int i();
    }

    @Test
    public void testScratching() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        Method md = INetworkedComponent.class.getMethod("selfPath");


        var newInstance = new ByteBuddy().subclass(Object.class)
                .implement(Interface2.class)
                .define(md)
                .intercept(FixedValue.value(new GenericPath("hi")))
                .make()
                .load(INetworkedComponent.class.getClassLoader())
                .getLoaded()
                .newInstance();

        MyComponent cm = (MyComponent) newInstance;

        var path = cm.selfPath();

        System.out.println(path.tablePath());
    }

}
