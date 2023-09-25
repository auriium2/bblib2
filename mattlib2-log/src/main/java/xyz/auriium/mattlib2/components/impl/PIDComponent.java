package xyz.auriium.mattlib2.components.impl;

import xyz.auriium.mattlib2.annotation.Log;
import xyz.auriium.mattlib2.annotation.Tune;
import xyz.auriium.mattlib2.components.IComponent;
import xyz.auriium.yuukonfig.core.annotate.Key;

public interface PIDComponent extends IComponent {

    @Tune
    @Key("p")
    int pConstant();

    @Tune
    @Key("i")
    int iConstant();

    @Tune
    @Key("d")
    int dConstant();

    @Log
    void reportError(double error);

    @Log
    void reportOutput(double output);

}
