package xyz.auriium.mattlib2.foxe.structure;

import java.util.Map;
import java.util.Set;

public record ClientData(
        String name,
        Map<Integer, Integer> subscriptionIdToChannelId,
        Map<Integer, Integer> channelIdToSubscriptionId,
        Map<Integer, Integer> advertisements,
        Set<String> parameterSubscriptions
) { }
