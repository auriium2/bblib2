package xyz.auriium.mattlib2.nt;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;

import edu.wpi.first.util.datalog.*;
import edu.wpi.first.wpilibj.DataLogManager;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;
import xyz.auriium.mattlib2.log.InitializableSupplier;
import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.mattlib2.log.TypeMap;
import xyz.auriium.mattlib2.nt.consumers.*;
import xyz.auriium.mattlib2.nt.suppliers.*;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkMattLogger {

    static final ConsumerMap consumerMap = new ConsumerMap();
    static final SupplierMap supplierMap = new SupplierMap();

    final DataLog dataLog = DataLogManager.getLog();

    static {

        consumerMap.registerTwo(Integer.class, int.class, (e,d,t) -> new IntConsumer(e, new IntegerLogEntry(d, e.getName()), t));
        consumerMap.registerTwo(Double.class, double.class, (e,d,t) -> new DoubleConsumer(e, new DoubleLogEntry(d, e.getName()), t));
        consumerMap.registerTwo(Long.class, long.class, (e,d,t) -> new LongConsumer(e, new IntegerLogEntry(d, e.getName()), t));
        consumerMap.register(String.class, (e,d,t) -> new StringConsumer(e, new StringLogEntry(d, e.getName()), t));
        consumerMap.registerTwo(Boolean.class, boolean.class, (e,d,t) -> new BooleanConsumer(e, new BooleanLogEntry(d, e.getName()), t));
        consumerMap.register(boolean[].class, (e,d,t) -> new BooleanArrayConsumer(e, new BooleanArrayLogEntry(d, e.getName()), t));
        consumerMap.register(double[].class, (e,d,t) -> new DoubleArrayConsumer(e, new DoubleArrayLogEntry(d, e.getName()), t));
        consumerMap.register(long[].class, (e,d,t) -> new LongArrayConsumer(e, new IntegerArrayLogEntry(d, e.getName()), t));
        consumerMap.register(int[].class, (e,d,t) -> new IntArrayConsumer(e, new IntegerArrayLogEntry(d, e.getName()), t));
        consumerMap.register(Pose2d.class, (e,d,t) -> new PoseConsumer(e, new DoubleArrayLogEntry(d, e.getName()), t));
        consumerMap.register(Pose3d.class, (e,d,t) -> new Pose3Consumer(e, new DoubleArrayLogEntry(d, e.getName()), t));
        consumerMap.register(SwerveModulePosition[].class, (e,d,t) -> new SwervePosConsumer(e, new DoubleArrayLogEntry(d, e.getName()), t));
        consumerMap.register(SwerveModuleState[].class, (e,d,t) -> new SwerveStateConsumer(e, new DoubleArrayLogEntry(d, e.getName()), t));
        consumerMap.register(Translation2d.class, (e,d,t) -> new TranslationConsumer(e, new DoubleArrayLogEntry(d, e.getName()), t));

        supplierMap.registerTwo(int.class, Integer.class, IntSupplier::new);
        supplierMap.registerTwo(double.class, Double.class, DoubleSupplier::new);
        supplierMap.registerTwo(boolean.class, Boolean.class, BooleanSupplier::new);


    }

    @SuppressWarnings("unchecked")
    public <T> Optional<Supplier<T>> generateTuner(ProcessPath path, T defaultValue) {
        Class<T> returnType = (Class<T>) defaultValue.getClass();
        NetworkTableEntry entry = NetworkTableInstance.getDefault().getTable("mattlib").getEntry(path.tablePath() + "-tune");
        Optional<SupplierMap.SupplierSupplier<T>> opt = supplierMap.of(returnType);

        if (opt.isEmpty()) return Optional.empty();
        var supplier = opt.get();

        InitializableSupplier<T> supplier1 = supplier.make(entry, defaultValue);
        supplier1.initializeLogging();




        return Optional.of(supplier1);

    }



    public <T> Optional<Consumer<T>> generateLogger(ProcessPath path, Class<T> type, MattlibSettings.LogLevel threshold) {

        NetworkTableEntry entry = NetworkTableInstance.getDefault().getTable("mattlib").getEntry(path.tablePath());
        Optional<ConsumerMap.ConsumerSupplier<T>> opt = consumerMap.of(type);
        if (opt.isEmpty()) return Optional.empty();
        var consoomer = opt.get();

        InitializableConsumer<T> consumer1 = consoomer.make(entry, dataLog, threshold);
        consumer1.initializeLogging();

        return Optional.of(consumer1);
    }

}
