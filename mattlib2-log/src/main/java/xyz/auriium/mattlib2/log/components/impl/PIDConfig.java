package xyz.auriium.mattlib2.log.components.impl;

import xyz.auriium.mattlib2.log.annotation.HasUpdated;
import xyz.auriium.mattlib2.log.annotation.Log;
import xyz.auriium.mattlib2.log.annotation.Tune;
import xyz.auriium.mattlib2.log.components.INetworkedConfig;
import yuukonfig.core.annotate.Key;

public interface PIDConfig extends INetworkedConfig {

    @Tune
    @Key("p")
    int pConstant();

    @Tune
    @Key("d")
    int dConstant();

    @Tune
    @Key("i")
    int iConstant();


    @HasUpdated(keysToCheck = {"p", "i", "d"})
    boolean hasUpdated();

    @Log
    void reportError(double error);

    @Log
    void reportOutput(double output);

}
