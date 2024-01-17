package xyz.auriium.mattlib2.nt;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.EqualsMethod;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import xyz.auriium.mattlib2.Exceptions;
import xyz.auriium.mattlib2.Mattlib2Exception;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.FixedSupplier;
import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.mattlib2.log.annote.*;
import xyz.auriium.mattlib2.utils.ReflectionUtil;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.err.YuuKonfigException;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.manipulation.Manipulator;
import yuukonfig.core.manipulation.Priority;
import yuukonfig.core.node.Mapping;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static xyz.auriium.mattlib2.utils.ReflectionUtil.getKey;


/**
 * TODO optimize for structs
 */
public class LogComponentManipulator implements Manipulator {

    final Class<?> useClass;
    final BaseManipulation manipulation;
    final RawNodeFactory factory;
    final NetworkMattLogger logger;

    public LogComponentManipulator(Class<?> useClass, BaseManipulation manipulation, RawNodeFactory factory, NetworkMattLogger logger) {
        this.useClass = useClass;
        this.manipulation = manipulation;
        this.factory = factory;
        this.logger = logger;
    }

    @Override
    public int handles() {
        if (INetworkedComponent.class.isAssignableFrom(useClass)) return Priority.HANDLE;
        return Priority.DONT_HANDLE;
    }

    static final ByteBuddy BUDDY = new ByteBuddy();


    @Override
    public Object deserialize(Node node) throws BadValueException {

        Map<Method, Supplier<Object>> configOrTuneMap = new HashMap<>();
        Map<Method, Consumer<Object>> loggerMap = new HashMap<>();
        List<Method> hasUpdatedMap = new ArrayList<>();
        Method selfPathMethod = null;

        for (Method method : useClass.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) continue;
            if (method.getDeclaringClass() == Objects.class) continue;
            ReflectionUtil.checkMattLog(method);

            String key = getKey(method);
            GenericPath newPath = node.path().append(key);

            Conf conf = method.getAnnotation(Conf.class);
            Tune tune = method.getAnnotation(Tune.class);
            HasUpdated up = method.getAnnotation(HasUpdated.class);
            SelfPath selfPath = method.getAnnotation(SelfPath.class);

            if (selfPath != null) {
                if (selfPathMethod != null) throw Exceptions.MULTIPLE_SELF_PATH(newPath);
                selfPathMethod = method;
                continue;
            }

            if (up != null) {
                hasUpdatedMap.add(method);
                continue;
            }

            if (conf != null || tune != null) { //Handle this as a config value
                Mapping mp = node.asMapping();
                Class<?> returnType = method.getReturnType();
                Node subNode = null; //shitty hack
                if (returnType == Optional.class) {
                    subNode = mp.valuePossiblyMissing(key);
                } else {
                    subNode = mp.valueGuaranteed(key);
                }

                Object confObject = manipulation.deserialize(
                        subNode,
                        returnType
                );

                Supplier<Object> objectSupplier;

                if (tune != null && MattlibSettings.USE_LOGGING) { //it's a tune and not a conf AND we are in tuning mode (only set at restart)
                    objectSupplier = logger.generateTuner(ProcessPath.ofGeneric(newPath), confObject).orElseThrow(() ->
                            new Mattlib2Exception(
                             "tunerGenerationFailed",
                             String.format("could not set up a tuneable value of type %s", confObject.getClass().getName()),
                             "report this to matt"
                            )
                    );
                } else {
                    objectSupplier = new FixedSupplier<>(confObject);
                }

                configOrTuneMap.put(method, objectSupplier);
            } else { //It's a logger!

                //System.out.println(newPath.getAsTablePath());
                Class<Object> type = (Class<Object>) method.getParameters()[0].getType();

                Consumer<Object> objectConsumer = logger.generateLogger(ProcessPath.ofGeneric(newPath), type).orElseThrow(() ->
                        new YuuKonfigException(
                        "tunerGenerationFailed",
                         String.format("could not set up a loggable value of type %s", type.getName()),
                         "report this to matt"
                        )
                );

                loggerMap.put(method, objectConsumer);
            }
        }


        if (selfPathMethod == null) throw Exceptions.NO_SELF_PATH(node.path());


        try {
            var builder = BUDDY
                    .subclass(useClass)
                    .name(useClass.getPackageName() + "." + useClass.getSimpleName())
                    .suffix("Generated_" + Integer.toHexString(hashCode()));

            for (Class<?> anInterface : useClass.getInterfaces()) {
                builder = builder.implement(anInterface);
            }


            builder = builder
                    .method(ElementMatchers.isEquals())
                    .intercept(EqualsMethod.isolated());

            builder = builder
                    .method(ElementMatchers.is(selfPathMethod))
                    .intercept(FixedValue.value(node.path()));


            for (Method method : hasUpdatedMap) {
                builder = builder
                        .method(ElementMatchers.is(method))
                        .intercept(FixedValue.value(false)); //TODO this must be updated every so often
            }


            for (Map.Entry<Method, Supplier<Object>> values : configOrTuneMap.entrySet()) { //every method on the new implementation will only do one thing (return config value)
                //System.out.println(values.getKey() + " implemented with " + values.getValue().get());

                Implementation supplierInvoke = MethodCall
                        .invoke(Supplier.class.getMethod("get"))
                        .on(values.getValue())
                        .withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);

                builder = builder
                        .method(ElementMatchers.named(values.getKey().getName()))
                        .intercept(supplierInvoke);



            }
            for (Map.Entry<Method, Consumer<Object>> values : loggerMap.entrySet()) {
                //System.out.println(values.getKey() + " is logged");
                Implementation consumerInvoke = MethodCall
                        .invoke(Consumer.class.getMethod("accept", Object.class))
                        .on(values.getValue())
                        .withArgument(0)
                        .withAssigner(Assigner.DEFAULT, Assigner.Typing.STATIC); //TODO this needs to be fixed

                builder = builder
                        .method(ElementMatchers.named(values.getKey().getName()))
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
            throw new Mattlib2Exception("bytebuddy", "critical reflection error", "ask matt for help");
        }

    }


    @Override
    public Node serializeObject(Object object, GenericPath path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node serializeDefault(GenericPath path) {

        Object proxy = Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class[]{ useClass },
                (a,b,c) -> {
                    if (b.isDefault()) {
                        return InvocationHandler.invokeDefault(a, b, c);
                    }

                    throw new IllegalStateException("Defaulting anonymous proxy does not support normal method queries!");
                }
        );

        RawNodeFactory.MappingBuilder builder = factory.makeMappingBuilder(path);

        for (Method method : useClass.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) continue;
            ReflectionUtil.checkMattLog(method);

            Class<?> returnType = method.getReturnType();
            String key = getKey(method);
            Node serialized;
            if (!method.isAnnotationPresent(Conf.class) && !method.isAnnotationPresent(Tune.class)) {
                continue;
            } else {
                if (method.isDefault()) {
                    serialized = manipulation.serialize(
                            new ProxyForwarder2(method, proxy).invoke(),
                            returnType,
                            path.append(key)
                    );
                } else {
                    serialized = manipulation.serializeDefaultCtx(
                            returnType,
                            path.append(key)
                    );
                }
            }

            builder.add(key, serialized);
        }

        return builder.build();
    }





}
