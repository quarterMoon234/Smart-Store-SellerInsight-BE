package com.sellerinsight.sampledata.api;

import com.sellerinsight.common.api.ApiResponse;
import com.sellerinsight.sampledata.api.dto.SampleDataBootstrapResponse;
import com.sellerinsight.sampledata.application.SampleDataBootstrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile({"local", "test", "demo"})
@Tag(name = "Admin Sample Data", description = "운영용 샘플 데이터 주입 API")
@RestController
@RequestMapping("/api/v1/admin/sample-data")
@RequiredArgsConstructor
public class AdminSampleDataController {

    private final SampleDataBootstrapService sampleDataBootstrapService;

    @Operation(summary = "기존 데이터를 초기화하고 샘플 데이터를 주입")
    @PostMapping("/bootstrap")
    public ApiResponse<SampleDataBootstrapResponse> bootstrap(
            @RequestParam(value = "scenario", defaultValue = "default") String scenario
    ) {
        return ApiResponse.ok(sampleDataBootstrapService.bootstrap(scenario));
    }
}
