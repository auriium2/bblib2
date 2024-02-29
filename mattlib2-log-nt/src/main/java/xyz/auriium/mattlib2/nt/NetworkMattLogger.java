package xyz.auriium.mattlib2.nt;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import edu.wpi.first.util.datalog.*;
import edu.wpi.first.wpilibj.DataLogManager;
import xyz.auriium.mattlib2.log.InitializableConsumer;
import xyz.auriium.mattlib2.log.InitializableSupplier;
import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.mattlib2.log.TypeMap;
import xyz.auriium.mattlib2.nt.consumers.*;
import xyz.auriium.mattlib2.nt.suppliers.*;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkMattLogger {

    static final ConsumerMap consumerMap = new ConsumerMap();
    static final SupplierMap supplierMap = new SupplierMap();

    final DataLog dataLog = DataLogManager.getLog();

    static {

        consumerMap.registerTwo(Integer.class, int.class, (e,d) -> new IntConsumer(e, new IntegerLogEntry(d, e.getName())));
        consumerMap.registerTwo(Double.class, double.class, (e,d) -> new DoubleConsumer(e, new DoubleLogEntry(d, e.getName())));
        consumerMap.registerTwo(Long.class, long.class, (e,d) -> new LongConsumer(e, new IntegerLogEntry(d, e.getName())));
        consumerMap.register(String.class, (e,d) -> new StringConsumer(e, new StringLogEntry(d, e.getName())));
        consumerMap.registerTwo(Boolean.class, boolean.class, (e,d) -> new BooleanConsumer(e, new BooleanLogEntry(d, e.getName())));
        consumerMap.register(boolean[].class, (e,d) -> new BooleanArrayConsumer(e, new BooleanArrayLogEntry(d, e.getName())));
        consumerMap.register(double[].class, (e,d) -> new DoubleArrayConsumer(e, new DoubleArrayLogEntry(d, e.getName())));
        consumerMap.register(long[].class, (e,d) -> new LongArrayConsumer(e, new IntegerArrayLogEntry(d, e.getName())));
        consumerMap.register(int[].class, (e,d) -> new IntArrayConsumer(e, new IntegerArrayLogEntry(d, e.getName())));
        consumerMap.register(Pose2d.class, (e,d) -> new PoseConsumer(e, new DoubleArrayLogEntry(d, e.getName())));
        consumerMap.register(Pose3d.class, (e,d) -> new Pose3Consumer(e, new DoubleArrayLogEntry(d, e.getName())));
        consumerMap.register(SwerveModulePosition[].class, (e,d) -> new SwervePosConsumer(e, new DoubleArrayLogEntry(d, e.getName())));
        consumerMap.register(SwerveModuleState[].class, (e,d) -> new SwerveStateConsumer(e, new DoubleArrayLogEntry(d, e.getName())));
        consumerMap.register(Translation2d.class, (e,d) -> new TranslationConsumer(e, new DoubleArrayLogEntry(d, e.getName())));

        supplierMap.registerTwo(int.class, Integer.class, IntSupplier::new);
        supplierMap.registerTwo(double.class, Double.class, DoubleSupplier::new);
        supplierMap.registerTwo(boolean.class, Boolean.class, BooleanSupplier::new);


    }

    @SuppressWarnings("unchecked")
    public <T> Optional<Supplier<T>> generateTuner(ProcessPath path, T defaultValue) {
        Class<T> returnType = (Class<T>) defaultValue.getClass();
        NetworkTableEntry entry = NetworkTableInstance.getDefault().getTable("mattlib").getEntry(path.tablePath());
        Optional<SupplierMap.SupplierSupplier<T>> opt = supplierMap.of(returnType);
        if (opt.isEmpty()) return Optional.empty();
        var supplier = opt.get();

        InitializableSupplier<T> supplier1 = supplier.make(entry, defaultValue);
        supplier1.initializeLogging();




        return Optional.of(supplier1);

    }



    public <T> Optional<Consumer<T>> generateLogger(ProcessPath path, Class<T> type) {

        NetworkTableEntry entry = NetworkTableInstance.getDefault().getTable("mattlib").getEntry(path.tablePath());
        Optional<ConsumerMap.ConsumerSupplier<T>> opt = consumerMap.of(type);
        if (opt.isEmpty()) return Optional.empty();
        var consoomer = opt.get();

        InitializableConsumer<T> consumer1 = consoomer.make(entry, dataLog);
        consumer1.initializeLogging();

        return Optional.of(consumer1);
    }

}
