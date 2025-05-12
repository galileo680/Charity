package com.bartek.Charity.service;

import com.bartek.Charity.domain.FundraisingEvent;
import com.bartek.Charity.dto.request.CreateFundraisingEventRequest;
import com.bartek.Charity.dto.response.FinancialReportItemResponse;
import com.bartek.Charity.dto.response.FundraisingEventResponse;

import java.util.List;

public interface FundraisingEventService {
    FundraisingEventResponse createFundraisingEvent(CreateFundraisingEventRequest request);
    FundraisingEvent findById(Long id);
    List<FinancialReportItemResponse> getFinancialReport();
}
