package com.bartek.Charity.service.impl;

import com.bartek.Charity.domain.BoxMoney;
import com.bartek.Charity.domain.CollectionBox;
import com.bartek.Charity.domain.FundraisingEvent;
import com.bartek.Charity.domain.enums.Currency;
import com.bartek.Charity.dto.request.AddMoneyRequest;
import com.bartek.Charity.dto.request.AssignCollectionBoxRequest;
import com.bartek.Charity.dto.request.RegisterCollectionBoxRequest;
import com.bartek.Charity.dto.response.CollectionBoxResponse;
import com.bartek.Charity.dto.response.MoneyTransferResponse;
import com.bartek.Charity.exception.BusinessException;
import com.bartek.Charity.exception.ResourceNotFoundException;
import com.bartek.Charity.mapper.CollectionBoxMapper;
import com.bartek.Charity.repository.BoxMoneyRepository;
import com.bartek.Charity.repository.CollectionBoxRepository;
import com.bartek.Charity.service.CollectionBoxService;
import com.bartek.Charity.service.ExchangeRateService;
import com.bartek.Charity.service.FundraisingEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionBoxServiceImpl implements CollectionBoxService {

    private final CollectionBoxRepository collectionBoxRepository;
    private final BoxMoneyRepository boxMoneyRepository;
    private final FundraisingEventService fundraisingEventService;
    private final ExchangeRateService exchangeRateService;
    private final CollectionBoxMapper collectionBoxMapper;

    @Override
    @Transactional
    public CollectionBoxResponse registerCollectionBox(RegisterCollectionBoxRequest request) {
        if (collectionBoxRepository.existsByIdentifier(request.identifier())) {
            throw new BusinessException("Collection box with identifier " + request.identifier() + " already exists");
        }

        CollectionBox box = collectionBoxMapper.toEntity(request);
        CollectionBox savedBox = collectionBoxRepository.save(box);
        return collectionBoxMapper.toResponse(savedBox);
    }

    public List<CollectionBoxResponse> listAllCollectionBoxes() {
        return collectionBoxRepository.findAll().stream()
                .map(collectionBoxMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void unregisterCollectionBox(String identifier) {
        CollectionBox box = findByIdentifier(identifier);

        box.getBoxMoneySet().clear();

        collectionBoxRepository.delete(box);
    }

    @Override
    @Transactional
    public CollectionBoxResponse assignCollectionBox(String identifier, AssignCollectionBoxRequest request) {
        CollectionBox box = findByIdentifierWithMoney(identifier);

        if (box.getIsAssigned()) {
            throw new BusinessException("Collection box is already assigned to a fundraising event");
        }

        if (!isBoxEmpty(box)) {
            throw new BusinessException("Collection box must be empty before assignment");
        }

        FundraisingEvent event = fundraisingEventService.findById(request.fundraisingEventId());

        box.setFundraisingEvent(event);
        box.setIsAssigned(true);

        CollectionBox savedBox = collectionBoxRepository.save(box);
        return collectionBoxMapper.toResponse(savedBox);
    }

    @Override
    @Transactional
    public void addMoneyToBox(String identifier, AddMoneyRequest request) {
        CollectionBox box = findByIdentifier(identifier);

        BoxMoney boxMoney = boxMoneyRepository.findByCollectionBoxAndCurrency(box, request.currency())
                .orElseGet(() -> BoxMoney.builder()
                        .collectionBox(box)
                        .currency(request.currency())
                        .amount(BigDecimal.ZERO)
                        .build());

        boxMoney.setAmount(boxMoney.getAmount().add(request.amount()));
        boxMoneyRepository.save(boxMoney);
    }

    @Override
    @Transactional
    public MoneyTransferResponse emptyCollectionBox(String identifier) {
        CollectionBox box = findByIdentifierWithMoney(identifier);

        if (!box.getIsAssigned()) {
            throw new BusinessException("Collection box is not assigned to any fundraising event");
        }

        FundraisingEvent event = box.getFundraisingEvent();
        Map<Currency, BigDecimal> transferredAmounts = new HashMap<>();
        BigDecimal totalInTargetCurrency = BigDecimal.ZERO;

        for (BoxMoney boxMoney : box.getBoxMoneySet()) {
            if (boxMoney.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                transferredAmounts.put(boxMoney.getCurrency(), boxMoney.getAmount());

                BigDecimal convertedAmount = exchangeRateService.convertAmount(
                        boxMoney.getAmount(),
                        boxMoney.getCurrency(),
                        event.getAccountCurrency()
                );

                totalInTargetCurrency = totalInTargetCurrency.add(convertedAmount);

                boxMoney.setAmount(BigDecimal.ZERO);
            }
        }

        event.setAccountBalance(event.getAccountBalance().add(totalInTargetCurrency));

        return MoneyTransferResponse.builder()
                .collectionBoxId(box.getIdentifier())
                .fundraisingEventName(event.getName())
                .transferredAmounts(transferredAmounts)
                .targetCurrency(event.getAccountCurrency())
                .totalAmountInTargetCurrency(totalInTargetCurrency)
                .build();
    }

    private CollectionBox findByIdentifier(String identifier) {
        return collectionBoxRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Collection box not found with identifier: " + identifier));
    }

    private CollectionBox findByIdentifierWithMoney(String identifier) {
        return collectionBoxRepository.findByIdentifierWithMoney(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Collection box not found with identifier: " + identifier));
    }

    private boolean isBoxEmpty(CollectionBox box) {
        return box.getBoxMoneySet().stream()
                .allMatch(money -> money.getAmount().compareTo(BigDecimal.ZERO) == 0);
    }
}