package xyz.auriium.mattlib2.foxe;

public interface Schema {

    byte[] toBytes(Object data);
    byte[] schemaAsBytes();
    int outputByteLength();

}
