package xyz.auriium.mattlib2.nt;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;

import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.mattlib2.nt.consumers.*;
import xyz.auriium.mattlib2.nt.suppliers.*;

import java.util.EnumSet;
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
            Supplier<Double> supplier = new NTValueSupplier<>(entry, double.class);

            return (Supplier<T>) supplier;
        }

        if (returnType == Long.class || returnType == long.class) { //handle longs
            Supplier<Long> supplier = new NTValueSupplier<>(entry, long.class);

            return (Supplier<T>) supplier;
        }

        if (returnType == Integer.class || returnType == int.class) {
            Supplier<Integer> supplier = new NTValueSupplier<>(entry, int.class);

            return (Supplier<T>) supplier;
        }

        if (returnType == String.class) {
            Supplier<String> supplier = new NTValueSupplier<>(entry, String.class);

            return (Supplier<T>) supplier;
        }

        if (returnType == Boolean.class || returnType == boolean.class) {
            Supplier<Boolean> supplier = new NTValueSupplier<>(entry, boolean.class);

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
        if (type == Integer.class || type == int.class || type == Double.class || type == double.class || type == Boolean.class || type == boolean.class || type == String.class || type == Byte.class || type == byte.class || type == Long.class || type == long.class) {
            return Optional.of(new NetworkTableConsumer<>(entry));
        }

        if (type == int[].class || type == Integer[].class) {
            return Optional.of((Consumer<T>) new IntArrayConsumer(entry));
        }

        if (type == double[].class || type == Double[].class) {
            return Optional.of((Consumer<T>) new DoubleArrayConsumer(entry));
        }

        if (type == boolean[].class || type == Boolean[].class) {
            return Optional.of((Consumer<T>) new BooleanArrayConsumer(entry));
        }

        if (type == Pose2d.class) {
            return Optional.of((Consumer<T>) new PoseConsumer(entry));
        }

        if (type == Pose3d.class) {
            return Optional.of((Consumer<T>) new Pose3Consumer(entry));
        }

        if (type == Translation2d.class) {
            return Optional.of((Consumer<T>) new TranslationConsumer(entry));
        }


        if (type == SwerveModulePosition[].class) {
            return Optional.of((Consumer<T>) new SwervePosConsumer(entry));
        }

        if (type == SwerveModuleState[].class) {
            return Optional.of((Consumer<T>) new SwerveStateConsumer(entry));
        }



        return Optional.empty();
    }

}
