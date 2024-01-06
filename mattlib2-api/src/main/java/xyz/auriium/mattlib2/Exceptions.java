package xyz.auriium.mattlib2;

public class Exceptions {

    public static final Mattlib2Exception ALREADY_INITIALIZED = new Mattlib2Exception(
            "logAlreadyInitialized",
            "mattlib2 was already initialized, but you called loadWaiting or loadFuture on it after initialization!",
            "make sure all calls to loadWaiting or loadFuture happen before initialization. Just make your components static and nothing else!"
    );

}
