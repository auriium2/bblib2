package xyz.auriium.mattlib2.nt;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.mattlib2.nt.consumers.*;
import xyz.auriium.mattlib2.nt.suppliers.*;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkMattLogger {



    public <T> Optional<Supplier<T>> generateTuner(ProcessPath path, T defaultValue) {
        return Optional.ofNullable(getCorrectSupplier(path, defaultValue));
    }

    public BooleanSupplier generateHasUpdatedSupplier(ProcessPath path) {

        var entry = NetworkTableInstance.getDefault().getTable("mattlib")
                .getEntry(path.tablePath());

        return new BooleanSupplier() {
            @Override public boolean getAsBoolean() {
                return false;
            }
        };
    }

    @SuppressWarnings("unchecked")
    <T> Supplier<T> getCorrectSupplier(ProcessPath path, T defaultValue) {
        Class<?> returnType = defaultValue.getClass();
        NetworkTableEntry entry =  NetworkTableInstance.getDefault().getTable("mattlib").getEntry(path.tablePath());



        if (returnType == Double.class || returnType == double.class) { //handle doubles

            Supplier<Double> supplier = new NTValueSupplier<>(entry, double.class, (double) defaultValue);

            return (Supplier<T>) supplier;
        }

        if (returnType == Long.class || returnType == long.class) { //handle longs
            Supplier<Long> supplier = new NTValueSupplier<>(entry, long.class, (long) defaultValue);

            return (Supplier<T>) supplier;
        }

        if (returnType == Integer.class || returnType == int.class) {
            Supplier<Integer> supplier = new NTValueSupplier<>(entry, int.class, (int) defaultValue);

            return (Supplier<T>) supplier;
        }

        if (returnType == String.class) {
            Supplier<String> supplier = new NTValueSupplier<>(entry, String.class, (String) defaultValue);

            return (Supplier<T>) supplier;
        }

        if (returnType == Boolean.class || returnType == boolean.class) {
            Supplier<Boolean> supplier = new NTValueSupplier<>(entry, boolean.class, (boolean) defaultValue);

            return (Supplier<T>) supplier;
        }

        if (returnType == double[].class || returnType == Double[].class) {
            Supplier<double[]> sx = new NTArraySupplier<>(entry::getDoubleArray, new double[0]);

            return (Supplier<T>) sx;
        }

        if (returnType == boolean[].class || returnType == Boolean[].class) {
            Supplier<boolean[]> sx = new NTArraySupplier<>(entry::getBooleanArray, new boolean[0]);

            return (Supplier<T>) sx;
        }

        if (returnType == float[].class || returnType == Float[].class) {
            Supplier<float[]> sx = new NTArraySupplier<>(entry::getFloatArray, new float[0]);

            return (Supplier<T>) sx;
        }

        if (returnType == String[].class) {
            Supplier<String[]> sx = new NTArraySupplier<>(entry::getStringArray, new String[0]);

            return (Supplier<T>) sx;
        }


        if (returnType == int[].class || returnType == Integer[].class) {

            return (Supplier<T>) new PureNTIntArraySupplier(entry);
        }

        if (returnType == Pose2d.class) {
            return (Supplier<T>) new PoseSupplier(entry);
        }

        if (returnType == Pose3d.class) {
            return (Supplier<T>) new Pose3Supplier(entry);
        }

        if (returnType == Translation2d.class) {
            return (Supplier<T>) new TranslationSupplier(entry);
        }



        //TODO handle double arrays

        return null;

    }



    public <T> Optional<Consumer<T>> generateLogger(ProcessPath path, Class<T> type) {

        NetworkTableEntry entry = NetworkTableInstance.getDefault().getTable("mattlib").getEntry(path.tablePath());

        //do not ask
        if (type == Integer.class || type == int.class) {
            Consumer<Integer> cs = new NetworkTableConsumer<>(entry);
            cs.accept(0);

            return Optional.of((Consumer<T>)cs);
        }

        if (type == Long.class || type == long.class) {
            Consumer<Long> cs = new NetworkTableConsumer<>(entry);
            cs.accept(0L);

            return Optional.of((Consumer<T>)cs);
        }

        if (type == Byte.class || type == byte.class) {
            Consumer<Byte> cs = new NetworkTableConsumer<>(entry);
            cs.accept((byte)0);

            return Optional.of((Consumer<T>)cs);
        }

        if (type == Double.class || type == double.class) {
            Consumer<Double> cs = new NetworkTableConsumer<>(entry);
            cs.accept(0d);

            return Optional.of((Consumer<T>)cs);
        }

        if (type == Boolean.class || type == boolean.class) {
            Consumer<Boolean> cs = new NetworkTableConsumer<>(entry);
            cs.accept(false);

            return Optional.of((Consumer<T>)cs);
        }

        if (type == String.class) {
            Consumer<String> cs = new NetworkTableConsumer<>(entry);
            cs.accept("");

            return Optional.of((Consumer<T>)cs);
        }

        if (type == int[].class || type == Integer[].class) {
            return Optional.of((Consumer<T>) new IntArrayConsumer(entry));
        }

        if (type == double[].class || type == Double[].class) {
            Consumer<double[]> cs = new DoubleArrayConsumer(entry);
            cs.accept(new double[0]);

            return Optional.of((Consumer<T>)cs);
        }

        if (type == boolean[].class || type == Boolean[].class) {
            Consumer<boolean[]> cs = new BooleanArrayConsumer(entry);
            cs.accept(new boolean[0]);

            return Optional.of((Consumer<T>)cs);
        }

        if (type == Pose2d.class) {
            Consumer<Pose2d> cs = new PoseConsumer(entry);
            cs.accept(new Pose2d());

            return Optional.of((Consumer<T>)cs);
        }

        if (type == Pose3d.class) {
            Consumer<Pose3d> cs = new Pose3Consumer(entry);
            cs.accept(new Pose3d());

            return Optional.of((Consumer<T>)cs);
        }

        if (type == Translation2d.class) {
            Consumer<Translation2d> cs = new TranslationConsumer(entry);
            cs.accept(new Translation2d());

            return Optional.of((Consumer<T>)cs);        }


        if (type == SwerveModulePosition[].class) {
            Consumer<SwerveModulePosition[]> cs = new SwervePosConsumer(entry);
            cs.accept(new SwerveModulePosition[]{ new SwerveModulePosition(), new SwerveModulePosition(), new SwerveModulePosition(), new SwerveModulePosition()});

            return Optional.of((Consumer<T>)cs);
        }

        if (type == SwerveModuleState[].class) {
            Consumer<SwerveModuleState[]> cs = new SwerveStateConsumer(entry);
            cs.accept(new SwerveModuleState[]{ new SwerveModuleState(), new SwerveModuleState(), new SwerveModuleState(), new SwerveModuleState()});

            return Optional.of((Consumer<T>)cs);
        }



        return Optional.empty();
    }

}
