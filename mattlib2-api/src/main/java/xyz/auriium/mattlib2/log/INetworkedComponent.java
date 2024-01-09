package xyz.auriium.mattlib2.log;

/**
 * Extending this grants access
 * to the {@link Log} and {@link Tune} annotations
 */
public interface INetworkedComponent { //IMPORTANT: This is NOT a yuukonfig#section

    @SelfPath //only for debugging
    String selfPath();

}
