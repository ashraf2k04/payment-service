package com.ashraf.payment.controller;

import com.ashraf.payment.dto.*;
import com.ashraf.payment.exceptions.InvalidIsoStructureException;
import com.ashraf.payment.iso.IsoDocument;
import com.ashraf.payment.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Tag(name = "Payments", description = "Payment lifecycle management")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @Operation(
            summary = "Process ISO 20022 pacs.008 XML payment",
            description = "Parses ISO 20022 pacs.008 message and extracts settlement amount and currency"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/xml",
                    examples = @ExampleObject(
                            name = "ISO20022Example",
                            value = """
                        <Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08">
                            <FIToFICstmrCdtTrf>
                                <GrpHdr>
                                    <MsgId>MSG124</MsgId>
                                </GrpHdr>
                                <CdtTrfTxInf>
                                    <IntrBkSttlmAmt Ccy="INR">20000</IntrBkSttlmAmt>
                                </CdtTrfTxInf>
                            </FIToFICstmrCdtTrf>
                        </Document>
                        """
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ISO 20022 XML structure"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(value = "/iso", consumes = "application/xml")
    public ApiResult<PaymentResponse> processIso(@RequestBody IsoDocument doc) {

        var transaction = doc.transaction()
                .orElseThrow(() ->
                        new InvalidIsoStructureException("Invalid ISO 20022 structure")
                );

        var request = new PaymentRequest(
                transaction.requireAmount(),
                transaction.requireCurrency(),
                "ISO-%d".formatted(System.currentTimeMillis())
        );

        return ApiResult.success(service.createPayment(request));
    }
    @Operation(summary = "Create a new payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ApiResult<PaymentResponse> create(
            @Valid @RequestBody PaymentRequest request
    ) {
        return ApiResult.success(service.createPayment(request));
    }

    @Operation(summary = "Get payment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ApiResult<PaymentResponse> get(@PathVariable UUID id) {
        return ApiResult.success(service.getPayment(id));
    }


    @Operation(summary = "Get all payments")
    @ApiResponse(responseCode = "200", description = "List of payments")
    @GetMapping
    public ApiResult<List<PaymentResponse>> getAll() {
        return ApiResult.success(service.getAllPayments());
    }



    @Operation(summary = "Authorize a payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment authorized"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PostMapping("/{id}/authorize")
    public ApiResult<PaymentResponse> authorize(@PathVariable UUID id) {
        return ApiResult.success(service.authorizePayment(id));
    }


    @Operation(summary = "Capture an authorized payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment captured"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PostMapping("/{id}/capture")
    public ApiResult<PaymentResponse> capture(@PathVariable UUID id) {
        return ApiResult.success(service.capturePayment(id));
    }

    @Operation(
            summary = "Refund payment",
            description = "Admin-only endpoint to refund captured payments"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment refunded"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<PaymentResponse> refund(@PathVariable UUID id) {
        return ApiResult.success(service.refundPayment(id));
    }
}