package xyz.auriium.mattlib2;

/**
 * This class exists so we can guaruntee that something is initialized by init time
 * @param <T>
 */
public class GuaranteeInit<T> implements IPeriodicLooped {

    T toBeFilled;

    public void complete(T toFillThisWith) {
        toBeFilled = toFillThisWith;
    }

    T getData() {
        if (toBeFilled == null) throw Exceptions.NOT_INITIALIZED_ON_TIME;

        return toBeFilled;
    }

}
