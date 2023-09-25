package org.bitbuckets.bootstrap;

import xyz.auriium.mattlib2.annotation.Conf;
import xyz.auriium.mattlib2.annotation.Log;
import xyz.auriium.mattlib2.components.IComponent;

interface SomeComponent extends IComponent {

    @Log
    void logSomeValue(double d);

    @Conf
    int confInt();

}
