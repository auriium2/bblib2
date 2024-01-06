package xyz.auriium.mattlib2.foxe;

import java.util.Map;

public interface UserFoxgloveServer {


    //USER FUNCTIONS

    /**
     * Use user ID handle to get user
     * @param channelId
     * @return
     */
    ServerChannel acquireChannel(int channelId);

    /**
     * Use user string path to get user. Slow.
     * @param path
     * @return
     */
    ServerChannel acquireChannelByPath(String path) throws IllegalArgumentException;


    /**
     *
     * @return An immutable mapping between user paths and associated IDs
     */
    Map<String, Integer> getPathDictionary();

    MultiplexUserChannel acquireUserChannel();

}
