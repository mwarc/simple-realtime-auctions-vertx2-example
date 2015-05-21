package com.github.mwarc.realtimeauctions;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class AuctionValidator {

    public static boolean isBidPossible(
        ConcurrentMap<String, String> auctionSharedData,
        Map<String, String> auctionRequestBody
    ) {
        BigDecimal currentPrice = new BigDecimal(auctionSharedData.getOrDefault("price", "0"));
        BigDecimal newPrice = new BigDecimal(auctionRequestBody.get("price"));
        return currentPrice.compareTo(newPrice) == -1;
    }
}
