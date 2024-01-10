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
import xyz.auriium.mattlib2.*;
import xyz.auriium.mattlib2.log.*;
import xyz.auriium.mattlib2.yuukonfig.CustomForwarder;
import yuukonfig.core.annotate.Comment;
import yuukonfig.core.annotate.Key;
import yuukonfig.core.err.BadConfigException;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.manipulation.Manipulation;
import yuukonfig.core.manipulation.Manipulator;
import yuukonfig.core.manipulation.Priority;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;
import yuukonstants.GenericPath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * TODO optimize for structs
 */
public class LogComponentManipulator implements Manipulator {

    final Class<?> useClass;
    final Manipulation manipulation;
    final RawNodeFactory factory;
    final Supplier<Boolean> shouldUseTuning;
    final NetworkMattLogger logger;

    public LogComponentManipulator(Class<?> useClass, Manipulation manipulation, Supplier<Boolean> shouldUseTuning, RawNodeFactory factory, NetworkMattLogger logger) {
        this.useClass = useClass;
        this.manipulation = manipulation;
        this.shouldUseTuning = shouldUseTuning;
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
    public Object deserialize(Node node, GenericPath exceptionalKey) throws BadValueException {

        Map<Method, Supplier<Object>> configOrTuneMap = new HashMap<>();
        Map<Method, Consumer<Object>> loggerMap = new HashMap<>();
        List<Method> hasUpdatedMap = new ArrayList<>();


        for (Method method : useClass.getMethods()) {

            if (method.getDeclaringClass() == Objects.class) continue;
            check(method);



            String key = getKey(method);
            GenericPath newPath = exceptionalKey.append(key);

            Conf conf = method.getAnnotation(Conf.class);
            Tune tune = method.getAnnotation(Tune.class);
            HasUpdated up = method.getAnnotation(HasUpdated.class);
            if (up != null) {
                hasUpdatedMap.add(method);
                continue;
            }

            if (conf != null || tune != null) { //Handle this as a config value
                Node nullable = node.asMapping().value(key);

                if (nullable == null) throw Exceptions.NO_TOML(newPath);


                Class<?> returnType = method.getReturnType();
                Object confObject = manipulation.deserialize(
                        nullable,
                        newPath,
                        returnType
                );

                Supplier<Object> objectSupplier;

                if (tune != null && shouldUseTuning.get()) { //it's a tune and not a conf AND we are in tuning mode (only set at restart)
                    objectSupplier = logger.generateTuner(ProcessPath.ofGeneric(newPath), confObject).orElseThrow(() ->
                            new BadConfigException(
                             "tunerGenerationFailed",
                             String.format("could not set up a tuneable value of type %s", confObject.getClass().getName()),
                             "report this to matt"
                            )
                    );
                } else {
                    objectSupplier = () -> confObject;
                }

                configOrTuneMap.put(method, objectSupplier);
            } else { //It's a logger!

                Class<Object> type = (Class<Object>) method.getParameters()[0].getType();

                Consumer<Object> objectConsumer = logger.generateLogger(ProcessPath.ofGeneric(newPath), type).orElseThrow(() ->
                        new BadConfigException(
                        "tunerGenerationFailed",
                         String.format("could not set up a loggable value of type %s", type.getName()),
                         "report this to matt"
                        )
                );

                loggerMap.put(method, objectConsumer);


            }
        }




        try {
            var builder = BUDDY.subclass(useClass)
                    .name(useClass.getPackageName() + "." + useClass.getSimpleName())
                    .suffix("Generated_" + Integer.toHexString(hashCode()));

            builder = builder
                    .method(ElementMatchers.isEquals())
                    .intercept(EqualsMethod.isolated());

            builder = builder
                    .method(ElementMatchers.is(INetworkedComponent.class.getMethod("selfPath")))
                    .intercept(FixedValue.value(exceptionalKey));

            for (Method method : hasUpdatedMap) {
                builder = builder
                        .method(ElementMatchers.is(method))
                        .intercept(FixedValue.value(false)); //TODO this must be updated every so often
            }

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
            throw new Mattlib2Exception("bytebuddy", "critical reflection error", "ask matt for help");
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

    public static void check(Method method) {

        int quantity = 0;
        Conf conf = method.getAnnotation(Conf.class);
        if (conf != null) quantity++;
        Log log = method.getAnnotation(Log.class);
        if (log != null) quantity++;
        LogArray logArr = method.getAnnotation(LogArray.class);
        if (logArr != null) quantity++;
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

        if (method.getAnnotations().length == 0) {
            throw xyz.auriium.mattlib2.Exceptions.NO_ANNOTATIONS_ON_METHOD(methodName, simpleName);
        }

        if ((conf != null || tune != null) && method.getParameterCount() != 0) {
            throw xyz.auriium.mattlib2.Exceptions.BAD_CONF_OR_TUNE(methodName, simpleName);
        }

        if (log != null && method.getParameterCount() == 0) {
            throw xyz.auriium.mattlib2.Exceptions.BAD_LOG(methodName, simpleName);
        }

        if (log != null && method.getReturnType() != void.class) {
            throw Exceptions.BAD_RETURN_TYPE(methodName, simpleName);
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
