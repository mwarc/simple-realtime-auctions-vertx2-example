package com.github.mwarc.realtimeauctions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.buffer.Buffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuctionConverter {

    private static final Logger logger = LoggerFactory.getLogger(AuctionConverter.class);

    private AuctionConverter() {
    }

    public static String toJson(Map<String, String> auction) {
        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(auction);
        } catch (IOException e) {
            logger.error("Failed to serialize auction {}", auction);
        }
        return json;
    }

    public static Map<String, String> toMap(Buffer buffer) {
        Map<String, String> map = new HashMap<>();
        try {
            map = new ObjectMapper().readValue(buffer.toString(), new TypeReference<HashMap<String, String>>(){});
        } catch (IOException e) {
            logger.error("Cannot map auction json: {}", buffer.toString());
        }
        return map;
    }
}
