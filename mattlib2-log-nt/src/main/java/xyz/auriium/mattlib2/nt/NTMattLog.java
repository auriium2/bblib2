package xyz.auriium.mattlib2.nt;

import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.*;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import xyz.auriium.mattlib2.*;
import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.ProcessMap;
import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.mattlib2.log.TypeMap;
import xyz.auriium.mattlib2.yuukonfig.*;
import yuukonfig.core.ConfigLoader;
import yuukonfig.core.YuuKonfig;
import yuukonfig.core.impl.safe.HandlesPrimitiveManipulator;
import yuukonfig.core.impl.safe.HandlesSafeManipulator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * Silly logging/configuration/tuning solution
 *
 * WARNING: When using this, you MUST CALL INIT
 */
public class NTMattLog implements IMattLog, IPeriodicLooped {

    //record for storing important data
    record LoadStruct<T>(ProcessPath path, Class<T> type, CompletableFuture<T> futureReference) { }
    final static ByteBuddy BUDDY = new ByteBuddy();

    final NetworkMattLogger logger = new NetworkMattLogger();
    final List<LoadStruct<?>> structs = new ArrayList<>();

    boolean hasBeenInitialized = false;

    public NTMattLog() {
        mattRegister();
    }

    @Override
    public void preInit() {
        hasBeenInitialized = true;

        ProcessMap processMap = new ProcessMap();

        for (LoadStruct<?> struct : structs) {
            processMap = processMap.with(struct.path, struct.type);
        }

        ProcessMap finalProcessMap = processMap;

        String load = "config.toml";
        String sim = "sim.toml";
        String mcr = "mcr.toml";

        var conf_dir = new File(Filesystem.getDeployDirectory(), "mattlib");
        var conf_file = new File(conf_dir, load);
        var sim_file = new File(conf_dir, sim);
        var mcr_file = new File(conf_dir, mcr);


        boolean isSim = RobotBase.isSimulation();
        boolean shouldDoFunnyOverwriteOfFile = false;

        if (!conf_file.exists()) {
            //create
            shouldDoFunnyOverwriteOfFile = true;
            try {
                conf_file.createNewFile();
            } catch (IOException e) {
                throw new Mattlib2Exception("localFileCreationFailure", "mattlib2 tried to regenerate your conf file but it couldnt, because of something IOExceptiony!",e, "tbh idk" );
            }
        }
        if (!sim_file.exists()) {
            throw Exceptions.MATTLIB_FILE_EXCEPTION(sim);
        }
        if (!mcr_file.exists()) throw Exceptions.MATTLIB_FILE_EXCEPTION(mcr);

        //System.out.println("has read: " + traj_file.canRead() + " has write: " + traj_file.canWrite());


        ConfigLoader<TypeMap> loader = YuuKonfig.instance()
                .register(
                        (manipulation,clazz,c,factory) -> new LogComponentManipulator(
                                clazz,
                                manipulation,
                                factory,
                                logger
                        )
                )
                .register(
                        (manipulation, useClass, useType, factory) -> new TypeMapManipulator(manipulation, useClass, useType, factory, finalProcessMap)
                )
                .register(HandlesSafeManipulator.ofSpecific(Pose2d.class, Pose2Manipulator::new))
                .register(HandlesSafeManipulator.ofSpecific(Translation2d.class, Translation2Manipulator::new))
                .register(HandlesSafeManipulator.ofSpecific(Rotation2d.class, Rotation2Manipulator::new))
                .register(HandlesSafeManipulator.ofSpecific(Pose3d.class, Pose3Manipulator::new))
                .register(HandlesSafeManipulator.ofSpecific(Rotation3d.class, Rotation3Manipulator::new))
                .register(HandlesPrimitiveManipulator.ofSpecific(Long.class, long.class, LongManipulator::new))
                .register(HandlesSafeManipulator.ofSpecific(Translation3d.class, Translation3Manipulator::new))
                .loader(TypeMap.class, conf_file.toPath());

        TypeMap map;
        if (RobotBase.isSimulation()) {
            var contentBridge = loader.loadOnlyUser();
            if (shouldDoFunnyOverwriteOfFile) {
                contentBridge = contentBridge.writeToFile();
            }

            map = contentBridge
                    .overrideMainConfigFromFile(sim_file.toPath())
                    .loadToMemoryConfig();
        } else {
            var contentBridge = loader.loadOnlyUser();

            if (MattlibSettings.ROBOT == MattlibSettings.Robot.CARY) {
                map = contentBridge.loadToMemoryConfig();
            } else if (MattlibSettings.ROBOT == MattlibSettings.Robot.MCR) {
                map = contentBridge
                        .overrideMainConfigFromFile(mcr_file.toPath())
                        .loadToMemoryConfig();
            } else {
                map = contentBridge.loadToMemoryConfig();
            }

        }

        for (LoadStruct<?> struct : structs) {
            LoadStruct<Object> oldStruct = (LoadStruct<Object>) struct;
            Object toResupply = map.backingMap.get(struct.path);
            oldStruct.futureReference.complete(toResupply);
        }
    }

    /**
     * The "safe" version of loadWaiting, wont block your code if you forget to call init. Don't use this unless you know how to program multithreaded, use load instead
     * @param type type of component you want
     * @param path the path to load the component under.
     * @return a future waiting on a version of the component that is safe to call no matter what
     * @param <T> a component class
     */
    @Deprecated
    public <T extends INetworkedComponent> CompletableFuture<T> loadFuture(Class<T> type, String... path) {
        if (hasBeenInitialized) throw Exceptions.ALREADY_INITIALIZED();

        CompletableFuture<T> uncompleted = new CompletableFuture<>();

        LoadStruct<T> toLoad = new LoadStruct<>(new ProcessPath(path), type, uncompleted);
        structs.add(toLoad);

        return uncompleted; //will be completed... later!
    }

    ElementMatcher.Junction<ByteCodeElement> recursivelyGenerateMatcher(Class<?> superclass, ElementMatcher.Junction<ByteCodeElement> lastMatcher) {
        ElementMatcher.Junction<ByteCodeElement> matcher = lastMatcher;

        for (Class<?> clz : superclass.getInterfaces()) {
            matcher = matcher.or(ElementMatchers.isDeclaredBy(clz));
            matcher = recursivelyGenerateMatcher(clz, matcher);
        }

        return matcher;
    }

    static final Pattern pattern = Pattern.compile("\\s");

    /**
     * Gets a component using bytecode compiler
     * @param type the type of component you want
     * @param path the path you want to load the component under. In config.toml this will be [your.process.path]
     * @return A version of the component that WILL BLOCK YOUR CODE if you call it before init is called >:(
     * @param <T> a component class
     *
     *           WARNING: CALLING ANYTHING OFF OF THE GENERATED COMPONENT BEFORE {@link #preInit()} is called will BREAK YOUR CODE
     */
    public <T extends INetworkedComponent> T load(Class<T> type, String path) {
        if (pattern.matcher(path).find()) throw Exceptions.BAD_NAME(path);
        if (hasBeenInitialized) throw Exceptions.ALREADY_INITIALIZED();

        CompletableFuture<T> uncompleted = new CompletableFuture<>();

        LoadStruct<T> toLoad = new LoadStruct<>(ProcessPath.of(path), type, uncompleted);
        structs.add(toLoad);

        //Code below will generate a new Java class that implements a delegate which will call the future's join function
        //As fast as normal code to invoke

        try {
            TypeDescription.Generic gentrifiedFuture = TypeDescription.Generic.Builder.parameterizedType(CompletableFuture.class, type).build();

            var typedJoin = gentrifiedFuture.getDeclaredMethods().filter(ElementMatchers.named("join")).get(0);

            MethodCall callJoinOnFutureField = MethodCall
                    .invoke(typedJoin)
                    .onField("future");

            ElementMatcher.Junction<ByteCodeElement> initialMatcher = ElementMatchers.isDeclaredBy(type);
            var matcher = recursivelyGenerateMatcher(type, initialMatcher);

            var dyn = BUDDY
                    .subclass(type)
                    .name(type.getPackageName() + "." + type.getSimpleName())
                    .suffix("FXGen_" + Integer.toHexString(toLoad.hashCode()))
                    .defineField("future", gentrifiedFuture, Opcodes.ACC_FINAL | Opcodes.ACC_PUBLIC)
                    .defineConstructor(Opcodes.ACC_PUBLIC)
                    .withParameters(gentrifiedFuture)
                    .intercept(MethodCall.invoke(Object.class.getConstructor()).andThen(FieldAccessor.ofField("future").setsArgumentAt(0)))
                    .defineMethod("getInsideFuture", type, Opcodes.ACC_PUBLIC)
                    .intercept(callJoinOnFutureField)
                    .method(ElementMatchers.isEquals())
                    .intercept(EqualsMethod.isolated())
                    .method(ElementMatchers.isHashCode())
                    .intercept(HashCodeMethod.usingDefaultOffset())
                    .method(matcher.and(ElementMatchers.isMethod()))
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



}
