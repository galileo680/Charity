package com.bartek.Charity.service.impl;

import com.bartek.Charity.domain.FundraisingEvent;
import com.bartek.Charity.dto.request.CreateFundraisingEventRequest;
import com.bartek.Charity.dto.response.FinancialReportItemResponse;
import com.bartek.Charity.dto.response.FundraisingEventResponse;
import com.bartek.Charity.exception.BusinessException;
import com.bartek.Charity.exception.ResourceNotFoundException;
import com.bartek.Charity.mapper.FundraisingEventMapper;
import com.bartek.Charity.repository.FundraisingEventRepository;
import com.bartek.Charity.service.FundraisingEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FundraisingEventServiceImpl implements FundraisingEventService {

    private final FundraisingEventRepository fundraisingEventRepository;
    private final FundraisingEventMapper fundraisingEventMapper;

    @Override
    @Transactional
    public FundraisingEventResponse createFundraisingEvent(CreateFundraisingEventRequest request) {
        if (fundraisingEventRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException(
                    "Fundraising event with name '" + request.getName() + "' already exists");
        }

        FundraisingEvent event = fundraisingEventMapper.toEntity(request);
        FundraisingEvent savedEvent = fundraisingEventRepository.save(event);
        return fundraisingEventMapper.toResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public FundraisingEvent findById(Long id) {
        return fundraisingEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fundraising event not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialReportItemResponse> getFinancialReport() {
        return fundraisingEventRepository.findAll().stream()
                .map(event -> FinancialReportItemResponse.builder()
                        .fundraisingEventName(event.getName())
                        .amount(event.getAccountBalance())
                        .currency(event.getAccountCurrency())
                        .build())
                .collect(Collectors.toList());
    }
}