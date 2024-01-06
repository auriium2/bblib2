package xyz.auriium.mattlib2.foxe;

import xyz.auriium.mattlib2.Mattlib2Exception;

public class Exceptions {

    static Mattlib2Exception BAD_CHANNEL_ID(int channel) {
        return new Mattlib2Exception(
            "badChannelId",
            "the server channel ID " + channel + " is reported as valid by the client, but is invalid on the server",
            "contact matt or investigate"
        );
    };
    static Mattlib2Exception IMPOSSIBLE_SHUTDOWN = new Mattlib2Exception(
            "server/impossibleShutdown",
            "mattlib2 tried to shutdown the foxe server but it was never started in the first place",
            "contact matt or investigate the class FoxgloveFeature in mattlib2-log-foxe"
    );

}
