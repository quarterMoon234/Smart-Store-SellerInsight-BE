package com.sellerinsight.seller.api;

import com.sellerinsight.common.api.ApiResponse;
import com.sellerinsight.seller.api.dto.SellerCredentialResponse;
import com.sellerinsight.seller.api.dto.UpsertSellerCredentialRequest;
import com.sellerinsight.seller.application.SellerCredentialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Seller Credential", description = "판매자 연동 정보 API")
@RestController
@RequestMapping("/api/v1/sellers/{sellerId}/credentials")
@RequiredArgsConstructor
public class SellerCredentialController {

    private final SellerCredentialService sellerCredentialService;

    @Operation(summary = "판매자 연동 정보 저장 또는 갱신")
    @PutMapping
    public ApiResponse<SellerCredentialResponse> upsert(
            @PathVariable Long sellerId,
            @Valid @RequestBody UpsertSellerCredentialRequest request
    ) {
        return ApiResponse.ok(sellerCredentialService.upsert(sellerId, request));
    }

    @Operation(summary = "판매자 연동 정보 조회")
    @GetMapping
    public ApiResponse<SellerCredentialResponse> get(@PathVariable Long sellerId) {
        return ApiResponse.ok(sellerCredentialService.get(sellerId));
    }
}
