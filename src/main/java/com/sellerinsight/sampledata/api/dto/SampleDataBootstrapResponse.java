package com.sellerinsight.sampledata.api.dto;

import java.time.LocalDate;
import java.util.List;

public record SampleDataBootstrapResponse(
        String scenario,
        LocalDate previousMetricDate,
        LocalDate targetMetricDate,
        int sellerCount,
        int productCount,
        int orderCount,
        int orderItemCount,
        List<SampleDataSellerResponse> sellers
) {
}
