package com.bartek.Charity.controller;

import com.bartek.Charity.dto.request.CreateFundraisingEventRequest;
import com.bartek.Charity.dto.response.FinancialReportItemResponse;
import com.bartek.Charity.dto.response.FundraisingEventResponse;
import com.bartek.Charity.service.FundraisingEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fundraising-events")
@RequiredArgsConstructor
public class FundraisingEventController {

    private final FundraisingEventService fundraisingEventService;

    @PostMapping
    public ResponseEntity<FundraisingEventResponse> createFundraisingEvent(
            @Valid @RequestBody CreateFundraisingEventRequest request
    ) {
        FundraisingEventResponse response = fundraisingEventService.createFundraisingEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/financial-report")
    public ResponseEntity<List<FinancialReportItemResponse>> getFinancialReport() {
        List<FinancialReportItemResponse> report = fundraisingEventService.getFinancialReport();
        return ResponseEntity.ok(report);
    }
}