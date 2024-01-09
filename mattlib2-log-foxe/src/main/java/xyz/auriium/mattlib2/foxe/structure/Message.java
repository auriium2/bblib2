package xyz.auriium.mattlib2.foxe.structure;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility wrapper
 */
public class Message {

    //TODO stop allocating an entire hashmap per message

    static final Gson GSON = new Gson();
    final Map<String, Object> map = new HashMap<>();

    public Message(TextOperation operation) {
        map.put("op", operation.stringOpCode);
    }

    public Message write(String key, Object val) {
        map.put(key, val);

        return this;
    }

    public String toString() {
        return GSON.toJson(map);
    }

}
