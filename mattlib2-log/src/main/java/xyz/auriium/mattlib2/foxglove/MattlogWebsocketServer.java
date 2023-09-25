package xyz.auriium.mattlib2.foxglove;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class MattlogWebsocketServer extends WebSocketServer {

    static final Logger MATTLOG = LoggerFactory.getLogger(MattlogWebsocketServer.class);
    static final Gson GSON = new Gson();

    final


    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

        MATTLOG.debug("server opened on: " + clientHandshake.getResourceDescriptor());

        //send server info / capabilities


        Map<String, String> info = new HashMap<>();
        info.put("op","serverInfo");
        info.put("name", "mattlog");
        info.put("capabilities", ""); //TODO add some
        info.put("supportedEncodings", ""); //TODO add some
        info.put("metadata", "");
        info.put("sessionId", "");


        //{
        //                "op": "serverInfo",
        //                "name": self.name,
        //                "capabilities": self.capabilities,
        //                "supportedEncodings": self.supported_encodings,
        //                "metadata": self.metadata,
        //                "sessionId": self.session_id,
        //            },

        webSocket.send(
                GSON.toJson(info)
        );


        //advertise channels

        Map<String, String> channels = new HashMap<>();
        channels.put("op", "advertise");
        channels.put("channels", ""); //TODO load all current "channels" from tuner

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) { //for binary msg Opcode.BINARY

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) { //for txt mssg opcode Opcode.TEXT
        //all received messages should be json




    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }


    public void init() {

    }


}
