package xyz.auriium.mattlib2.foxe.internal;

import xyz.auriium.mattlib2.foxe.ChannelData;

public interface FoxgloveStartupRegister {

    /**
     *
     * @param data the desecription of the chanel
     * @return the channel's id in the future
     */
    int registerChannel(ChannelData data);

}
