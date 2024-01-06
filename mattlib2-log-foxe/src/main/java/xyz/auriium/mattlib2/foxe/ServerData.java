package xyz.auriium.mattlib2.foxe;

import java.util.Map;

public record ServerData(



        String selfName,
        String[] capabilities,
        String[] supportedEncodings,
        Map<String, String>metadata,
        String sessionId

) {
}
