package xyz.auriium.mattlib2.log;

import edu.wpi.first.wpilibj.Filesystem;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.*;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatchers;
import xyz.auriium.mattlib2.Exceptions;
import xyz.auriium.mattlib2.Mattlib2Exception;
import yuukonfig.LogComponentManipulator;
import yuukonfig.TypeMapManipulator;
import yuukonfig.core.YuuKonfig;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * Silly logging/configuration/tuning solution
 *
 * WARNING: When using this, you MUST CALL INIT
 */
public class MattLog {

    //record for storing important data
    record LoadStruct<T>(ProcessPath path, Class<T> type, CompletableFuture<T> futureReference) { }
    final static ByteBuddy BUDDY = new ByteBuddy();

    final IMattLogger logger;
    final List<LoadStruct<?>> structs = new ArrayList<>();

    /**
     * @param logger logging feature to use
     *
     *                    MAKE SURE TO CALL INIT
     */
    public MattLog(IMattLogger logger) {
        this.logger = logger;
    }

    boolean hasBeenInitialized = false;


    /**
     * The "safe" version of loadWaiting, wont block your code if you forget to call init. Don't use this unless you know how to program multithreaded, use load instead
     * @param type type of component you want
     * @param path the path to load the component under.
     * @return a future waiting on a version of the component that is safe to call no matter what
     * @param <T> a component class
     */
    @Deprecated
    public <T extends INetworkedComponent> CompletableFuture<T> loadFuture(Class<T> type, String... path) {
        if (hasBeenInitialized) throw Exceptions.ALREADY_INITIALIZED;

        CompletableFuture<T> uncompleted = new CompletableFuture<>();

        LoadStruct<T> toLoad = new LoadStruct<>(new ProcessPath(path), type, uncompleted);
        structs.add(toLoad);

        return uncompleted; //will be completed... later!
    }

    /**
     * Loads many components of the same type
     * @param type
     * @param originalPath the root path all the new components should be based on
     * @param range
     * @param subNamingFunction a function that makes the new components have slightly different names based on the numerical id
     * @return an ordered array of all the new components within the range
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T extends INetworkedComponent> T[] loadRange(Class<T> type, String originalPath, int range, BiFunction<String, Integer, String> subNamingFunction) {
        T[] array = (T[]) new INetworkedComponent[4];
        for (int i = 0; i < range; i++) {
            array[i] = load(type, subNamingFunction.apply(originalPath, i));
        }
        return array;
    }

    /**
     *
     * @param type
     * @param originalPath
     * @param range
     * @return
     * @param <T>
     */
    public <T extends INetworkedComponent> T[] loadRange(Class<T> type, String originalPath, int range) {
        return loadRange(type, originalPath, range, (s,i) -> s+"/"+i);
    }

    /**
     * Gets a component using bytecode compiler
     * @param type the type of component you want
     * @param path the path you want to load the component under. In config.toml this will be [your.process.path]
     * @return A version of the component that WILL BLOCK YOUR CODE if you call it before init is called >:(
     * @param <T> a component class
     *
     *           WARNING: CALLING ANYTHING OFF OF THE GENERATED COMPONENT BEFORE {@link #initializeComponentsAndGenerateLoops()} is called will BREAK YOUR CODE
     */
    public <T extends INetworkedComponent> T load(Class<T> type, String path) {
        if (hasBeenInitialized) throw Exceptions.ALREADY_INITIALIZED;

        CompletableFuture<T> uncompleted = new CompletableFuture<>();

        LoadStruct<T> toLoad = new LoadStruct<>(ProcessPath.parse(path), type, uncompleted);
        structs.add(toLoad);

        //Code below will generate a new Java class that implements a delegate which will call the future's join function
        //As fast as normal code to invoke

        try {
            TypeDescription.Generic gentrifiedFuture = TypeDescription.Generic.Builder.parameterizedType(CompletableFuture.class, type).build();

            var typedJoin = gentrifiedFuture.getDeclaredMethods().filter(ElementMatchers.named("join")).get(0);

            MethodCall callJoinOnFutureField = MethodCall
                    .invoke(typedJoin)
                    .onField("future");

            var dyn = BUDDY
                    .subclass(type)
                    .name(type.getPackageName() + "." + type.getSimpleName())
                    .suffix("Delegated_" + Integer.toHexString(toLoad.hashCode()))
                    .defineField("future", gentrifiedFuture, Opcodes.ACC_FINAL | Opcodes.ACC_PUBLIC)
                    .defineConstructor(Opcodes.ACC_PUBLIC)
                    .withParameters(gentrifiedFuture)
                    .intercept(MethodCall.invoke(Object.class.getConstructor()).andThen(FieldAccessor.ofField("future").setsArgumentAt(0)))
                    .defineMethod("getInsideFuture", type, Opcodes.ACC_PUBLIC)
                    .intercept(callJoinOnFutureField)
                    .method(ElementMatchers.isEquals())
                    .intercept(EqualsMethod.isolated())
                    .method(ElementMatchers.isDeclaredBy(type).and(ElementMatchers.isMethod()))
                    .intercept(MethodDelegation.toMethodReturnOf("getInsideFuture"));

            DynamicType.Unloaded<?> unloaded = dyn.make();
            ClassLoader loaderToUse = type.getClassLoader();
            DynamicType.Loaded<?> loaded = unloaded.load(loaderToUse, ClassLoadingStrategy.ForBootstrapInjection.Default.INJECTION);

            var out= loaded
                    .getLoaded()
                    .getDeclaredConstructor(CompletableFuture.class)
                    .newInstance(uncompleted);

            return type.cast(out);

        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new Mattlib2Exception("bytecodeFailure","critical bytecode manipulation error", "contact matt");
        }

    }

    /**
     * If this isn't called, your code will explode
     */
    public void initializeComponentsAndGenerateLoops() {
        hasBeenInitialized = true;

        Map<ProcessPath, Class<?>> loadAs = new HashMap<>();

        for (LoadStruct<?> struct : structs) {
            loadAs.put(struct.path, struct.type);
        }

        logger.init();

        TypeMap map = YuuKonfig.instance()
                .register(
                        (manipulation,clazz,c,factory) -> new LogComponentManipulator(
                                clazz,
                                manipulation,
                                () -> false,
                                factory,
                                logger
                        )
                )
                .register(
                        (manipulation, useClass, useType, factory) -> new TypeMapManipulator(manipulation, useClass, useType, factory, loadAs)
                )
                .loader(TypeMap.class, Filesystem.getDeployDirectory().toPath().resolve("config.toml"))
                .load();

        for (LoadStruct<?> struct : structs) {
            LoadStruct<Object> oldStruct = (LoadStruct<Object>) struct;
            Object toResupply = map.backingMap.get(struct.path);
            oldStruct.futureReference.complete(toResupply);
        }
        logger.ready();
    }



}
