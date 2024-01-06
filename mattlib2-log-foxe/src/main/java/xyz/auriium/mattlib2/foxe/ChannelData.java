package xyz.auriium.mattlib2.foxe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record ChannelData(

        String topic,
        String encoding,
        String schemaName,
        String schema,
        Optional<String> schemaEncoding
) {

    Map<String, Object> asMap() {

        Map<String, Object> internal = new HashMap<>();

        internal.put("topic", topic);
        internal.put("encoding", encoding);
        internal.put("schemaName", schemaName);
        internal.put("schema", schema);

        return internal;

    }

}
