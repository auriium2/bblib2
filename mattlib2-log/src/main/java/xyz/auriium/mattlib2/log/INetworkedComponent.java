package xyz.auriium.mattlib2.log;

import xyz.auriium.mattlib2.log.annotation.SelfPath;

/**
 * Extending this grants access
 * to the {@link xyz.auriium.mattlib2.log.annotation.Log} and {@link xyz.auriium.mattlib2.log.annotation.Tune} annotations
 */
public interface INetworkedComponent { //IMPORTANT: This is NOT a yuukonfig#section

    @SelfPath //only for debugging
    String selfPath();

}
