package xyz.auriium.yuukonfig;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.EqualsMethod;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import xyz.auriium.mattlib2.*;
import xyz.auriium.mattlib2.annotation.Conf;
import xyz.auriium.mattlib2.annotation.Log;
import xyz.auriium.mattlib2.annotation.Tune;
import xyz.auriium.mattlib2.components.IComponent;
import xyz.auriium.mattlib2.nt.InvocationSupplier;
import xyz.auriium.yuukonfig.core.annotate.Comment;
import xyz.auriium.yuukonfig.core.annotate.Key;
import xyz.auriium.yuukonfig.core.err.BadValueException;
import xyz.auriium.yuukonfig.core.manipulation.Contextual;
import xyz.auriium.yuukonfig.core.manipulation.Manipulation;
import xyz.auriium.yuukonfig.core.manipulation.Manipulator;
import xyz.auriium.yuukonfig.core.manipulation.Priority;
import xyz.auriium.yuukonfig.core.node.Node;
import xyz.auriium.yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Dirty
public class ComponentManipulator implements Manipulator {

    final Class<?> useClass;
    final Manipulation manipulation;
    final RawNodeFactory factory;
    final Supplier<Boolean> shouldUseTuning;
    final ITuneFeature tuneFeature;
    final ILogFeature logFeature;

    public ComponentManipulator(Class<?> useClass, Manipulation manipulation, Supplier<Boolean> shouldUseTuning, RawNodeFactory factory, ITuneFeature tuneFeature, ILogFeature logFeature) {
        this.useClass = useClass;
        this.manipulation = manipulation;
        this.shouldUseTuning = shouldUseTuning;
        this.factory = factory;
        this.tuneFeature = tuneFeature;
        this.logFeature = logFeature;
    }

    @Override
    public int handles() {
        if (IComponent.class.isAssignableFrom(useClass)) return Priority.HANDLE;
        return Priority.DONT_HANDLE;
    }

    static final ByteBuddy BUDDY = new ByteBuddy();

    @Override
    public Object deserialize(Node node, String exceptionalKey) throws BadValueException {
        Map<Method, Supplier<Object>> configOrTuneMap = new HashMap<>();
        Map<Method, Consumer<Object>> loggerMap = new HashMap<>();
        ProcessPath path = ProcessPath.parse(exceptionalKey); //TODO this is really stupid

        for (Method method : useClass.getMethods()) {
            if (method.getDeclaringClass() == Objects.class) continue;
            check(method);

            String key = getKey(method);

            Conf conf = method.getAnnotation(Conf.class);
            Tune tune = method.getAnnotation(Tune.class);

            if (conf != null || tune != null) { //Handle this as a config value
                Node nullable = node.asMapping().value(key);

                if (nullable == null) throw new BadValueException(
                        manipulation.configName(),
                        key,
                        "No YAML found, please write some in!"
                );

                Class<?> returnType = method.getReturnType();
                Object confObject = manipulation.deserialize(
                        nullable,
                        key,
                        returnType
                );

                Supplier<Object> objectSupplier;

                if (tune != null && shouldUseTuning.get()) { //it's a tune and not a conf AND we are in tuning mode (only set at restart)
                    objectSupplier = tuneFeature.generateTuner(path.append(key), confObject).orElseThrow(() -> {
                        throw new BadValueException(
                                manipulation.configName(),
                                exceptionalKey,
                                String.format(
                                        "Cannot set up a tuneable value that tunes a value with type: %s",
                                        confObject.getClass().getName()
                                )
                        );
                    });
                } else {
                    objectSupplier = new InvocationSupplier<>(() -> confObject);
                }

                configOrTuneMap.put(method, objectSupplier);
            } else { //It's a logger!

                Class<Object> type = (Class<Object>) method.getParameters()[0].getType();

                Consumer<Object> objectConsumer = logFeature.generateLogger(path.append("key"), type).orElseThrow(() -> {
                    throw new BadValueException(
                            manipulation.configName(),
                            exceptionalKey,
                            String.format(
                                    "Cannot set up a loggable value that logs a value with type: %s",
                                    type.getName()
                            )
                    );
                });

                loggerMap.put(method, objectConsumer);


            }
        }


        try {
            var builder = BUDDY.subclass(useClass)
                    .name(useClass.getPackageName() + "." + useClass.getSimpleName())
                    .suffix("Generated_" + Integer.toHexString(hashCode()));

            builder
                    .method(ElementMatchers.isEquals())
                    .intercept(EqualsMethod.isolated());

            for (Map.Entry<Method, Supplier<Object>> values : configOrTuneMap.entrySet()) { //every method on the new implementation will only do one thing (return config value)
                Implementation supplierInvoke = MethodCall
                        .invoke(Supplier.class.getMethod("get"))
                        .on(values.getValue())
                        .withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);

                builder = builder
                        .method(ElementMatchers.is(values.getKey()))
                        .intercept(supplierInvoke);
            }
            for (Map.Entry<Method, Consumer<Object>> values : loggerMap.entrySet()) {
                Implementation consumerInvoke = MethodCall
                        .invoke(Consumer.class.getMethod("accept", Object.class))
                        .on(values.getValue())
                        .withArgument(0)
                        .withAssigner(Assigner.DEFAULT, Assigner.Typing.STATIC); //TODO this needs to be fixed

                builder = builder
                        .method(ElementMatchers.is(values.getKey()))
                        .intercept(consumerInvoke);
            }

            DynamicType.Unloaded<?> unloaded = builder.make();
            ClassLoader loaderToUse = useClass.getClassLoader();
            DynamicType.Loaded<?> loaded = unloaded.load(loaderToUse, ClassLoadingStrategy.ForBootstrapInjection.Default.INJECTION);

            return loaded
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ExplainedException("mattlib2", "critical reflection error", "ask matt for help");
        }

    }


    @Override
    public Node serializeObject(Object object, String[] comment) {
        RawNodeFactory.MappingBuilder builder = factory.makeMappingBuilder();

        for (Method method : useClass.getMethods()) {
            if (method.getDeclaringClass() == Objects.class) continue;
            check(method);

            String key = getKey(method);
            Class<?> as = method.getReturnType();
            String[] comments = getComment(method);
            Object toSerialize = new CustomForwarder(method, object).invoke(); //get the return of the method


            Node serialized = manipulation.serialize(
                    toSerialize,
                    as,
                    comments,
                    Contextual.present(
                            method.getGenericReturnType()
                    )
            );

            builder.add(key, serialized);

        }


        return builder.build();
    }

    void check(Method method) {

        int quantity = 0;
        Conf conf = method.getAnnotation(Conf.class);
        if (conf != null) quantity++;
        Log log = method.getAnnotation(Log.class);
        if (log != null) quantity++;
        Tune tune = method.getAnnotation(Tune.class);
        if (tune != null) quantity++;

        if (quantity > 1) {
            throw new BadValueException(
                    manipulation.configName(),
                    method.getName(),
                    "Too many annotations! \nPlease use only @log, @tune, or @conf on it"
            );
        }


        if (method.getAnnotations().length == 0) {
            throw new BadValueException(
                    manipulation.configName(),
                    method.getName(),
                    String.format("No annotation on method: %s for class: %s! \nPlease add either @log, @tune, or @conf on it", method.getName(), useClass.getName())
            );
        }

        if ((conf != null || tune != null) && method.getParameterCount() != 0) {
            throw new IllegalStateException(
                    String.format("The config interface method '%s' cannot have arguments, but it has %s arguments!", method.getName(), method.getParameterCount())
            );
        }

        if (log != null && method.getParameterCount() == 0) {
            throw new IllegalStateException(
                    String.format("The logging interface method '%s' must have arguments!", method.getName())
            );
        }


        if (log != null && method.getReturnType() != void.class) {
            throw new BadValueException(
                    manipulation.configName(),
                    method.getName(),
                    String.format("return type must be void, but return type is %s", method.getReturnType())
            );
        }
    }



    @Override
    public Node serializeDefault(String[] comment) {


        RawNodeFactory.MappingBuilder builder = factory.makeMappingBuilder();

        for (Method method : useClass.getMethods()) {
            if (method.getDeclaringClass() == Objects.class) continue;
            check(method);
            if (method.getAnnotation(Log.class) != null) continue; //No need to serialize for log

            Class<?> returnType = method.getReturnType();
            String key = getKey(method);
            String[] comments = getComment(method);
            Node serialized = manipulation.serializeDefault(
                    returnType,
                    comments,
                    Contextual.present(method.getGenericReturnType())
            );

            //System.out.println("finished: " + key + " : " + serialized);

            builder.add(key, serialized);
        }

        return builder.build(String.valueOf(Arrays.asList(comment)));
    }

    String[] getComment(Method method) {
        if (method.isAnnotationPresent(Comment.class)) {
            return method.getAnnotation(Comment.class).value();
        }

        return new String[]{};
    }


    String getKey(Method method) {
        if (method.isAnnotationPresent(Key.class)) {
            return method.getAnnotation(Key.class).value();
        }

        return method.getName();
    }
}
