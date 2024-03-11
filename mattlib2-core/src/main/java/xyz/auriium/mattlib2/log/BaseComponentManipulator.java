package xyz.auriium.mattlib2.log;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
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
import xyz.auriium.mattlib2.log.annote.Conf;
import xyz.auriium.mattlib2.log.annote.Log;
import xyz.auriium.mattlib2.log.annote.SelfPath;
import xyz.auriium.mattlib2.log.annote.Tune;
import xyz.auriium.mattlib2.utils.ReflectionUtil;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.err.BadValueException;
import yuukonfig.core.err.YuuKonfigException;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.manipulation.Manipulator;
import yuukonfig.core.manipulation.Priority;
import yuukonfig.core.node.Mapping;
import yuukonfig.core.node.Node;
import yuukonfig.core.node.RawNodeFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
/*


*/
/**
 * TODO optimize for structs
 *//*

public class BaseComponentManipulator implements Manipulator {

    static final Method SELF_PATH;

    static {
        try {
            SELF_PATH = INetworkedComponent.class.getMethod("selfPath");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e); //TODO handle this better
        }
    }

    final Class<?> useClass;
    final BaseManipulation manipulation;
    final RawNodeFactory factory;
    final IMethodGenerator methodGenerator;

    public record PairForMap(Method md, Implementation impl) {}

    public BaseComponentManipulator(Class<?> useClass, BaseManipulation manipulation, RawNodeFactory factory, IMethodGenerator methodGenerator) {
        this.useClass = useClass;
        this.manipulation = manipulation;
        this.factory = factory;
        this.methodGenerator = methodGenerator;
    }



    @Override
    public int handles() {

        if (INetworkedComponent.class.isAssignableFrom(useClass)) return Priority.HANDLE;
        return Priority.DONT_HANDLE;

    }

    static final ByteBuddy BUDDY = new ByteBuddy();
    final TypeCache<Object> cache = new TypeCache<>();

    @Override
    public Object deserialize(Node node) throws BadValueException {
        boolean nodeIsMapping = node.type() == Node.Type.MAPPING;


        if (nodeIsMapping) { //spell checker
            Map<String, Boolean> hasBeenHandled = new HashMap<>();
            node.asMapping().getMap().forEach((s,n) -> {
                if (n.type() != Node.Type.SCALAR && n.type() != Node.Type.SEQUENCE) return;
                hasBeenHandled.put(s, false);
            });
            ReflectionUtil.iterateClassMethodsSafely(useClass, md -> {
                String key = ReflectionUtil.getKey(md);
                hasBeenHandled.computeIfPresent(key, (u,b) -> true);
            });
            List<String> allMethods = Arrays.stream(useClass.getMethods()).map(ReflectionUtil::getKey).collect(Collectors.toList());
            hasBeenHandled.forEach((s,b) -> {
                if (!b) {
                    throw Exceptions.UNUSED_CONF_DATA(s, getTheClosestMatch(allMethods, s).orElse("i couldnt actually find an alternative"), node.path(), useClass);
                }
            });
        }


        List<PairForMap> implementationForMethodMap = new ArrayList<>();
        List<Implementation> implementationForUpdate = new ArrayList<>();
        List<Function<DynamicType.Builder<Object>, DynamicType.Builder<Object>>> applyUpdates = new ArrayList<>();


        //handle all loggers
        ReflectionUtil.iterateClassAnnotationSafely(useClass, Log.class, (md,a,i) -> {
            GenericPath path = node.path().append(a.value());
            Class<Object> type = (Class<Object>) md.getParameters()[0].getType();

            IMethodGenerator.Output output = methodGenerator.generateLog(i, path, type);
            Implementation[] impls = output.implementations();

            applyUpdates.add(output.applyToBuilder());
            implementationForMethodMap.add(new PairForMap(md, impls[IMethodGenerator.IDX_FOR_METHOD]));
            if (IMethodGenerator.supportsUpdate(impls)) {
                implementationForUpdate.add(impls[IMethodGenerator.IDX_FOR_UPDATE]);
            }
        });

        //handle all tuners
        ReflectionUtil.iterateClassAnnotationSafely(useClass, Tune.class, (md,a,i) -> {
            String key = a.value();
            GenericPath path = node.path().append(key);

            Mapping mp = node.asMapping();
            Class<?> returnType = md.getReturnType();

            Node subNode = mp.valuePossiblyMissing(key);
            if (returnType != Optional.class) {
                subNode = mp.valueGuaranteed(key);
            }

            Object confObject = manipulation.deserialize(
                    subNode,
                    returnType,
                    Contextual.present(
                            md.getGenericReturnType()
                    )
            );

            IMethodGenerator.Output output = methodGenerator.generateTune(i, path, returnType, confObject);
            Implementation[] impls = output.implementations();

            applyUpdates.add(output.applyToBuilder());
            implementationForMethodMap.add(new PairForMap(md, impls[IMethodGenerator.IDX_FOR_METHOD]));
            if (IMethodGenerator.supportsUpdate(impls)) {
                implementationForUpdate.add(impls[IMethodGenerator.IDX_FOR_UPDATE]);
            }
        });

        //handle all confs...
        ReflectionUtil.iterateClassAnnotationSafely(useClass, Conf.class, (md,a,i) -> {
            String key = a.value();
            GenericPath path = node.path().append(key);

            Mapping mp = node.asMapping();
            Class<?> returnType = md.getReturnType();

            Node subNode = mp.valuePossiblyMissing(key);
            if (returnType != Optional.class) {
                subNode = mp.valueGuaranteed(key);
            }

            Object confObject = manipulation.deserialize(
                    subNode,
                    returnType,
                    Contextual.present(
                            md.getGenericReturnType()
                    )
            );

            IMethodGenerator.Output output = methodGenerator.generateConf(i, path, returnType, confObject);
            Implementation[] impls = output.implementations();

            applyUpdates.add(output.applyToBuilder());
            implementationForMethodMap.add(new PairForMap(md, impls[IMethodGenerator.IDX_FOR_METHOD]));
            if (IMethodGenerator.supportsUpdate(impls)) {
                implementationForUpdate.add(impls[IMethodGenerator.IDX_FOR_UPDATE]);
            }
        });



        try {
            var builder = BUDDY
                    .subclass(Object.class)
                    .implement(useClass)
                    .name(useClass.getPackageName() + "." + useClass.getSimpleName())
                    .suffix("LCMGen_" + Integer.toHexString(hashCode()));

            builder = builder
                    .method(ElementMatchers.isEquals())
                    .intercept(EqualsMethod.isolated());

            builder = builder
                    .define(SELF_PATH)
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
                        .define(values.getKey())
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
                        .define(values.getKey())
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
        int configQuantity = 0;
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
                configQuantity++;
                if (method.isDefault()) {
                    serialized = manipulation.serialize(
                            BaseComponentManipulator.autoProxyThenInvoke(useClass, method),
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

        if (configQuantity == 0) {
            return factory.notPresentOf(path); //fuck you
        }
        return builder.build();
    }

    //Implementation specific functions


    */
/**
     * This function calls the default method of any interface with a default method on it by generating a proxy
     * @param clazz
     * @param method
     * @return the default data returned by the method
     *//*

    public static Object autoProxyThenInvoke(Class<?> clazz, Method method) {
        Object proxy = Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class[]{ clazz },
                (a,b,c) -> {
                    if (b.isDefault()) {
                        return InvocationHandler.invokeDefault(a, b, c);
                    }

                    throw new IllegalStateException("Defaulting anonymous proxy does not support normal method queries!");
                }
        );

        return new ProxyForwarder2(method, proxy).invoke();
    }


    public static Optional<String> getTheClosestMatch(List<String> collection, String target) {
        int distance = Integer.MAX_VALUE;
        String closest = null;
        for (String compareObject : collection) {
            int currentDistance = getLevenshteinDistance(compareObject, target);
            if(currentDistance < distance) {
                distance = currentDistance;
                closest = compareObject;
            }
        }
        return Optional.ofNullable(closest);
    }





}
*/
