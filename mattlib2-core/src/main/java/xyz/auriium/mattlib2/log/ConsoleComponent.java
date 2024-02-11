package xyz.auriium.mattlib2.log;

import xyz.auriium.mattlib2.log.annote.Log;
import xyz.auriium.yuukonstants.exception.ExplainedException;

public interface ConsoleComponent {

    @Log("console") void reportToConsole(String stuffToGoToTheConsole);


}
