package xyz.auriium.mattlib2.foxe;


import com.google.gson.Gson;
import io.undertow.websockets.core.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import xyz.auriium.mattlib2.foxe.structure.ChannelData;
import xyz.auriium.mattlib2.foxe.structure.Message;
import xyz.auriium.mattlib2.foxe.structure.ServerData;
import xyz.auriium.mattlib2.foxe.structure.TextOperation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * FUTURE UPGRADES
 * - byte smashing, bytebuffer location position
 * - atomics
 * - message on ping?
 * - update during onTextMessage/onBinaryMessage, NOT during onFullTextMessage (could be quicker)
 */
public class FoxgloveSocketServer extends AbstractReceiveListener implements FoxgloveServer {

    static final Gson GSON = new Gson();

    /**
     * Describes a subscription to a channel
     * @param user TODO this should probably be a WeakReference<WebSocketChanneL>
     * @param subscriptionId id of the channel local to the subscriber. (idk why)
     */
    record UserSubscription(
            WebSocketChannel user,
            int subscriptionId
    ) {}

    record UserChannel(
            int userSpecifiedId,
            ChannelData channel
    ) {}

    record UpdateLocalChannel(




    ){}



    //immutable
    final ServerData data;
    final ChannelData[] sendingChannels;

    final ChannelData[] receivingChannels;
    final Consumer<byte[]>[] receivingCallbacks;

    final Object2IntOpenHashMap<String> sendingChannelsStringDictionary; //maps topic to sending channel id

    /**
     * 1st array: immutable atomic references, indexed by channel id, populated at startup.
     * atomic references: mutable pointer to user subscriptions, changes when client subscribes / unsubscribes, swaps entire array out
     * user subscription array: immutable array of subscriptions, replaced entirely on unsubscription
     */
    final AtomicReference<UserSubscription[]>[] channelIdToSubscriptions;
    final ConcurrentMap<WebSocketChannel, Int2IntOpenHashMap> userStorage = new ConcurrentHashMap<>(); //TODO indexing upgrades

    final Queue<UpdateLocalChannel> concurrentUpdateQueue = new ConcurrentLinkedQueue<>();

    public FoxgloveSocketServer(ServerData data, ChannelData[] channels, ChannelData[] receivingChannels, Consumer<byte[]>[] receivingCallbacks, Object2IntOpenHashMap<String> sendingChannelsStringDictionary, AtomicReference<UserSubscription[]>[] channelIdToSubscriptions) {
        this.data = data;
        this.sendingChannels = channels;
        this.receivingChannels = receivingChannels;
        this.receivingCallbacks = receivingCallbacks;
        this.sendingChannelsStringDictionary = sendingChannelsStringDictionary;
        this.channelIdToSubscriptions = channelIdToSubscriptions;
    }

    /**
     * called when a connection to new user is started
     * @param entity new user
     */
    public void onOpen(WebSocketChannel entity) {
        System.out.println(  "open conection: " +      Thread.currentThread().getId() + " unit size: " + userStorage.size());
        userStorage.put(entity, new Int2IntOpenHashMap());
        //entity.flush(); //TODO does this go at the end?



        //Say hello
        Message greetings = new Message(TextOperation.SERVER_INFO)
                .write("op", "serverInfo")
                .write("name", data.selfName())
                .write("capabilities", data.capabilities())
                .write("supportedEncodings", data.supportedEncodings())
                .write("metadata", data.metadata())
                .write("sessionId", data.sessionId());

        System.out.println(greetings.toString());
        WebSockets.sendText(greetings.toString(), entity, null);

        //advertise our channels!
        if (sendingChannels.length > 0) {
            List<Map<String, Object>> channelsAsList = new ArrayList<>();
            for (int j = 0; j < sendingChannels.length; j++) {
                var channelAsMap = sendingChannels[j].asMap();
                channelAsMap.put("id", j); //append ID

                channelsAsList.add(channelAsMap);
            }

            Message advertiseAllChannels = new Message(TextOperation.ADVERTISE)
                    .write("channels", channelsAsList);

            WebSockets.sendText(advertiseAllChannels.toString(), entity, null);
        }

        this.broadcastStatus(StatusLevel.ERROR, "oh no an exception", "some details");

        //user begins with 0 subscriptions, so no need to write to channelIdToSubscriptions

    }


    @Override
    protected void onClose(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) throws IOException {
        super.onClose(webSocketChannel, channel);

        System.out.println("closed!");
    }



    @SuppressWarnings("unchecked") //TODO handle casting better
    @Override
    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
        super.onFullTextMessage(channel, message);
        //guess that the message is a JSON

        String rawData = message.getData();
        Map<String, Object> mapRepresentation = GSON.fromJson(rawData, Map.class);

        String operation = (String) mapRepresentation.get("op");


        switch (operation) {
            case "subscribe" -> handleClientSubscription((int) mapRepresentation.get("channelId"), (int) mapRepresentation.get("id"), channel);
            case "unsubscribe" -> handleClientUnsubscription((int) mapRepresentation.get("channelId"), channel);
            case "advertise" -> {
                ChannelData of =  new ChannelData(
                        (String)mapRepresentation.get("topic"),
                        (String)mapRepresentation.get("encoding"),
                        (String)mapRepresentation.get("schemaName"),
                        (String)mapRepresentation.get("schema"),
                        Optional.ofNullable((String) mapRepresentation.get("schemaEncoding"))
                );

                //match client data



                handleClientAdvertisement((Integer) mapRepresentation.get("id"), of, channel);
            }
            default -> broadcastStatus(StatusLevel.ERROR, "unknown client simple operation recv: " + operation, "oops");

        }
    }

    @Override
    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
        super.onFullBinaryMessage(channel, message);

        //TODO i have no idea if this works
        ByteBuffer contents = WebSockets.mergeBuffers(message.getData().getResource());
        //Is ByteBuffer flipped open or is it normal
        byte opcode = contents.get(0);

        switch (opcode) {
            case OpCode.CLIENT_MSG_DATA -> {
                //TRYING TO READ DATA FROM CLIENT INTO RECV CHANNEL
                byte[] channelIdAsBytes = new byte[4];
                int channelIdAsInt = 0;
                for (byte b : channelIdAsBytes) {
                    channelIdAsInt = (channelIdAsInt << 8) + (b & 0xFF);
                }
                ChannelData receivingChannel = receivingChannels[channelIdAsInt];
                /*
                byte[] messageData = new byte[1+4+8+receivingChannel.actualSchema().outputByteLength()];
*//*
                contents.get((1), channelIdAsBytes, 0, 4); //skip 1st byte
                contents.get((1 + 4 + 8), messageData);*/
            }
            case OpCode.CLIENT_SERVICE_CALL_REQUEST -> {
                // nothing yet
            }
            default -> broadcastStatus(StatusLevel.ERROR, "unknown client binary operation recv: " + opcode, "oops");
        }

        //TODO handle client binary messages (CBM) and service/parameter requests
    }



    @Override
    public void broadcastTime(long time) { //TODO can probably optimize this
        ByteBuffer message = ByteBuffer.allocate(1 + 8);
        message.put(OpCode.SERVER_TIME); //1 byte
        message.putLong(time); //8 bytes
        message.flip();

        userStorage.forEach((u,ignored) -> WebSockets.sendBinary(message, u, null));
    }

    @Override
    public void broadcastStatus(StatusLevel statusLevel, String status, String details) {
        Message message = new Message(TextOperation.STATUS)
                .write("level", statusLevel.id)
                .write("message", status)
                .write("tips", details);

        userStorage.forEach((u,ignored) -> WebSockets.sendText(message.toString(), u, null));
    }

    public void handleClientAdvertisement(int userDefinedId, ChannelData userChannelData, WebSocketChannel channel) {
        //need to match the user defined ID to channel data

        String topic = userChannelData.topic();

        if (!sendingChannelsStringDictionary.containsKey(topic)) {
            this.broadcastStatus(StatusLevel.ERROR, String.format("client advertised topic {%s} with id {%s}, but server has no such id", userChannelData.topic(), userChannelData), "oops");

            return;
        }

        int serverChannelId = sendingChannelsStringDictionary.getInt(topic);

        //TODO make sure this works
        userStorage.compute(channel, (k,v) -> {
            if (v == null) {
                return new Int2IntOpenHashMap(new int[] {userDefinedId}, new int[] {serverChannelId});
            }
            v.put(userDefinedId, serverChannelId);
            return v;
        });

    }

    public void handleClientSubscription(int channelId, int subscriptionId, WebSocketChannel channel) {
        //gonna mutate channelIdToSubscriptions
        if (channelId > channelIdToSubscriptions.length) throw Exceptions.BAD_CHANNEL_ID(channelId);

        AtomicReference<UserSubscription[]> channelMapReference = channelIdToSubscriptions[channelId];

        //might want to explicitly do a CAS instead of using updateAndGet because idk how array reference CAS comaprison works
        channelMapReference.updateAndGet(currentSubscriptionMap -> { //CAS between original and original + new object
            int currentLength = currentSubscriptionMap.length;
            UserSubscription[] next = new UserSubscription[currentLength + 1];

            //wait we're floated
            System.arraycopy(currentSubscriptionMap, 0, next, 0, currentSubscriptionMap.length);

            next[currentLength] = new UserSubscription(channel, subscriptionId);
            return next;
        });
    }

    public void handleClientUnsubscription(int channelId, WebSocketChannel channel) {
        //gonna mutate channelIdToSubscriptions
        if (channelId > channelIdToSubscriptions.length) throw Exceptions.BAD_CHANNEL_ID(channelId);

        AtomicReference<UserSubscription[]> channelMapReference = channelIdToSubscriptions[channelId];

        channelMapReference.updateAndGet(currentSubscriptionMap -> { //CAS between original and original - new object
            List<UserSubscription> mutable = new ArrayList<>(List.of(currentSubscriptionMap));

            UserSubscription toRemove= null;
            for (UserSubscription u : mutable) {
                if (u.user.equals(channel)) {
                    toRemove = u;
                }
            }

            if (toRemove != null) mutable.remove(toRemove);
            return mutable.toArray(UserSubscription[]::new);
        });
    }


    void readDataFromUserChannel(int userChannelId, ByteBuffer buffer, WebSocketChannel channel) {
        Int2IntOpenHashMap registeredChannels = userStorage.get(channel);
        if (registeredChannels == null || !registeredChannels.containsKey(userChannelId)) {
            this.broadcastStatus(StatusLevel.ERROR, String.format("user never advertised, but posted message on channel {%s}", userChannelId), "oops");
            return;
        }

        int receivingChannelId = registeredChannels.get(userChannelId);
        receivingCallbacks[receivingChannelId].accept(buffer.array()); //TODO swap this out when schemas are done
    }

    @Override
    public void pushDataToChannel(int channelId, byte[] data) throws IOException {
        ChannelData channelToPushTo = sendingChannels[channelId];
        AtomicReference<UserSubscription[]> subscriptions = channelIdToSubscriptions[channelId]; //get user's subscriptions safely
        UserSubscription[] subscriptionsByChannelId = subscriptions.get(); //atomic reference is read only here. using get is safe.

        for (UserSubscription subscription : subscriptionsByChannelId) {
            WebSocketChannel associated = subscription.user;
            int associatedId = subscription.subscriptionId;
            long nanoTimeRoughEstimate = Duration.ofMillis(System.currentTimeMillis()).toNanos(); //TODO make this optimized and not bad

            //TODO can replace data output stream with raw byte array from schema OR mount stream in static schema
            ByteBuffer buff = ByteBuffer.allocate(1 + 4 + 8 + data.length);
            buff.put(OpCode.SERVER_MSG_DATA); //1 byte
            buff.putInt(associatedId); //4 bytes
            buff.putLong(nanoTimeRoughEstimate); //8 bytes nanos
            buff.put(data); //n bytes
            buff.flip();

            WebSockets.sendBinary(buff, associated, null);

        }

    }
}
