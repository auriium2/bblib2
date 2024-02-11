package xyz.auriium.mattlib2.log;

import xyz.auriium.mattlib2.log.annote.Log;
import xyz.auriium.yuukonstants.exception.ExplainedException;

public interface ConsoleComponent extends INetworkedComponent {

    @Log("console") void reportToConsole(String stuffToGoToTheConsole);


}
