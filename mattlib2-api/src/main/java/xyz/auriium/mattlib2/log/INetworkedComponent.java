package xyz.auriium.mattlib2.log;

import xyz.auriium.mattlib2.log.annote.Log;
import xyz.auriium.mattlib2.log.annote.SelfPath;
import xyz.auriium.mattlib2.log.annote.Tune;
import yuukonstants.GenericPath;

/**
 * Extending this grants access
 * to the {@link Log} and {@link Tune} annotations
 */
public interface INetworkedComponent { //IMPORTANT: This is NOT a yuukonfig#section

    @SelfPath //only for debugging
    GenericPath selfPath();

}
