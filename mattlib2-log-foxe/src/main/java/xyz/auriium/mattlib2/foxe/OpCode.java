package xyz.auriium.mattlib2.foxe;

/**
 * Represents all the binary opcodes that foxglove uses in websocket messages to represent stuff
 */
public interface OpCode {

    byte SERVER_MSG_DATA = 1;
    byte SERVER_TIME = 2;
    byte SERVER_SERVICE_CALL_RESPONSE = 3;
    byte SERVER_FETCH_ASSET_RESPONSE  =4;

    byte CLIENT_MSG_DATA = 1;
    byte CLIENT_SERVICE_CALL_REQUEST = 2;

}
