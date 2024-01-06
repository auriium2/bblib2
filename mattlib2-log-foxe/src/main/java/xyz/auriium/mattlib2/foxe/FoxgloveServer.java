package xyz.auriium.mattlib2.foxe;

import java.io.IOException;
import java.util.Map;

public interface FoxgloveServer {

    enum StatusLevel {
        INFO(0),
        WARNING(1),
        ERROR(2);

        public final int id;

        StatusLevel(int id) {
            this.id = id;
        }
    }

    //OP FUNCTIONS


    void pushDataToChannel(int channelId, byte[] data) throws IOException;
    void broadcastTime(long time);
    void broadcastStatus(StatusLevel statusLevel, String status, String details);


}
