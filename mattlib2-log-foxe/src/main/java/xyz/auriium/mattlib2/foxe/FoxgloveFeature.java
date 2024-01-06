package xyz.auriium.mattlib2.foxe;

import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketExtension;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.core.protocol.Handshake;
import io.undertow.websockets.core.protocol.version13.Hybi13Handshake;
import io.undertow.websockets.extensions.ExtensionFunction;
import io.undertow.websockets.extensions.ExtensionHandshake;
import io.undertow.websockets.extensions.PerMessageDeflateHandshake;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import xyz.auriium.mattlib2.log.IMattLogger;
import xyz.auriium.mattlib2.log.ProcessPath;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.websocket;

/**
 * This class implements the log feature and tune feature from mattlib2-log allowing you to drop it in when creating
 * a Mattlog
 */
public class FoxgloveFeature implements IMattLogger {

    Undertow server;

    @Override
    public void init() {

    }

    @Override
    public <T> Optional<Consumer<T>> generateLogger(ProcessPath path, Class<T> type) {
        return null;
    }

    @Override
    public <T> Optional<Supplier<T>> generateTuner(ProcessPath path, T defaultValue) {
        return Optional.empty();
    }


    @Override
    public void ready() {

        //TODO take all the compiled channels and put them in here
        FoxgloveSocketServer socketServer = new FoxgloveSocketServer(
                new ServerData("mattlib2", new String[0], new String[] {"json"}, new HashMap<>(), "1"),
                new ChannelData[0],
                new ChannelData[0],
                new Consumer[0],
                new Object2IntOpenHashMap<>(),
                new AtomicReference[0]
        );

        Set<String> subprotocols = Set.of(
                "foxglove.websocket.v1"
        );

        //augment hybi 13 handshake with foxglove's subprotocol
        Set<Handshake> augmentedHandshakes = Set.of(
                new Hybi13Handshake(subprotocols, true)
        );

        //set up websocket handler, tell it to use custom handshake and on connect use our onOpen
        WebSocketProtocolHandshakeHandler http2wsUpgrade = new WebSocketProtocolHandshakeHandler(
                augmentedHandshakes,
                (WebSocketConnectionCallback) (exchange, channel) -> {
                    socketServer.onOpen(channel);
                    channel.getReceiveSetter().set(socketServer);
                    channel.resumeReceives();
                }
        );

        //Configure compression
        http2wsUpgrade.addExtension(new PerMessageDeflateHandshake());

        server = Undertow.builder()
                .addHttpListener(8765, "localhost")
                .setHandler(http2wsUpgrade)
                .build();

        server.start();


    }

    @Override
    public void close() throws IOException {
        if (server == null) throw Exceptions.IMPOSSIBLE_SHUTDOWN;

        server.stop();
    }
}
