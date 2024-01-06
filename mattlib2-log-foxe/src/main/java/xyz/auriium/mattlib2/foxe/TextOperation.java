package xyz.auriium.mattlib2.foxe;

public enum TextOperation {

    SERVER_INFO("serverInfo"),
    ADVERTISE("advertise"),
    STATUS("status");

    final String stringOpCode;

    TextOperation(String stringOpCode) {
        this.stringOpCode = stringOpCode;
    }
}
