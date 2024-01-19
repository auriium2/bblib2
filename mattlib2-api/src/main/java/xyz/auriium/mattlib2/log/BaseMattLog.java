package xyz.auriium.mattlib2.log;

import xyz.auriium.mattlib2.IMattLog;

public class BaseMattLog implements IMattLog {
    @Override
    public <T extends INetworkedComponent> T load(Class<T> type, String path) {
        return null;
    }
}
