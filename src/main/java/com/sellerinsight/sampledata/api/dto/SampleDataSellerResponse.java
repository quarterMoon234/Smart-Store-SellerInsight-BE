package com.sellerinsight.sampledata.api.dto;

public record SampleDataSellerResponse(
        Long sellerId,
        String externalSellerId,
        String sellerName,
        int productCount,
        int orderCount
) {
}
