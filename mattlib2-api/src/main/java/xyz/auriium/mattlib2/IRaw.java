package xyz.auriium.mattlib2;

/**
 * Anything with this interface is wrapping a piece of hardware.
 * If you know the name of the hardware (i.e. TalonFX) and the interface extending this
 * does not provide sufficient functionality, you can use rawaccess to get it
 */
public interface IRaw {

    /**
     * How to use this: call .rawAccess(TalonFX.class) on a IMotor that you know is a TalonFX to get the TalonFX class out of it
     *
     * @param clazz The underlying class of the abstraction that you are trying to get
     * @param <T>
     * @return the raw hardware (like a TalonFX)
     * @throws UnsupportedOperationException if you get the class identifier wrong (like if you tried to get a TalonSRX from a CANCODER)
     */
    @Deprecated
    default <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("does not implement " + clazz.getSimpleName());
    }

}
