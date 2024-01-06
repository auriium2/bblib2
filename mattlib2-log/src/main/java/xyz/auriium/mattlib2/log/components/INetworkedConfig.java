package xyz.auriium.mattlib2.log.components;

import xyz.auriium.mattlib2.log.annotation.SelfPath;

/**
 * The base interface for something that is a Matt//Log component
 */
public interface INetworkedConfig { //IMPORTANT: This is NOT a yuukonfig#section

    /**
     *
     * @return This component's current path (as provided by the user). Only use this for debugging!
     */
    @SelfPath
    String selfPath();

}
