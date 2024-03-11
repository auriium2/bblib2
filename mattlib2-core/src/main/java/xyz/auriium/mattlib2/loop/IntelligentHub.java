package xyz.auriium.mattlib2.loop;

import xyz.auriium.mattlib2.log.ProcessPath;

public interface IntelligentHub {

    void reportToConsole(String message);
    void reportWatchdogFinished(ProcessPath path, long time_ms);

}
