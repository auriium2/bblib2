package org.bitbuckets.bootstrap;


import xyz.auriium.mattlib2.log.annotation.Conf;
import xyz.auriium.mattlib2.log.annotation.Tune;

public interface IdComponent {


    @Conf
    int id();

    @Tune
    int id2();

}
