package xyz.auriium.mattlib2;

public class Exceptions {

    public static final Mattlib2Exception ALREADY_INITIALIZED = new Mattlib2Exception(
            "logAlreadyInitialized",
            "mattlib2 was already initialized, but you called loadWaiting or loadFuture on it after initialization!",
            "make sure all calls to loadWaiting or loadFuture happen before initialization. Just make your components static and nothing else!"
    );

    public static final Mattlib2Exception NOT_INITIALIZED_ON_TIME = new Mattlib2Exception(
            "guaranteeInit/notInitialized",
            "you made a variable into a guarantee init, but then tried to use it before filling that variable with data.",
            "make sure that the variable found at the line in the below stack trace is completed before it is used by others"
    );

}
