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
import com.bartek.Charity.exception.InvalidOperationException;
import com.bartek.Charity.exception.ResourceNotFoundException;
import com.bartek.Charity.mapper.CollectionBoxMapper;
import com.bartek.Charity.repository.BoxMoneyRepository;
import com.bartek.Charity.repository.CollectionBoxRepository;
import com.bartek.Charity.service.ExchangeRateService;
import com.bartek.Charity.service.FundraisingEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionBoxServiceImplTest {

    @Mock
    private CollectionBoxRepository collectionBoxRepository;

    @Mock
    private BoxMoneyRepository boxMoneyRepository;

    @Mock
    private FundraisingEventService fundraisingEventService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private CollectionBoxMapper collectionBoxMapper;

    @InjectMocks
    private CollectionBoxServiceImpl collectionBoxService;

    private CollectionBox collectionBox;
    private FundraisingEvent fundraisingEvent;
    private RegisterCollectionBoxRequest registerRequest;
    private AssignCollectionBoxRequest assignRequest;
    private AddMoneyRequest addMoneyRequest;
    private CollectionBoxResponse collectionBoxResponse;

    @BeforeEach
    void setUp() {
        collectionBox = new CollectionBox();
        collectionBox.setId(1L);
        collectionBox.setIdentifier("BOX001");
        collectionBox.setIsAssigned(false);
        collectionBox.setBoxMoneySet(new HashSet<>());

        fundraisingEvent = new FundraisingEvent();
        fundraisingEvent.setId(1L);
        fundraisingEvent.setName("Test Event");
        fundraisingEvent.setAccountCurrency(Currency.EUR);
        fundraisingEvent.setAccountBalance(BigDecimal.ZERO);

        registerRequest = new RegisterCollectionBoxRequest();
        registerRequest.setIdentifier("BOX001");

        assignRequest = new AssignCollectionBoxRequest();
        assignRequest.setFundraisingEventId(1L);

        addMoneyRequest = new AddMoneyRequest();
        addMoneyRequest.setCurrency(Currency.EUR);
        addMoneyRequest.setAmount(BigDecimal.valueOf(100.00));

        collectionBoxResponse = new CollectionBoxResponse();
        collectionBoxResponse.setId(1L);
        collectionBoxResponse.setIdentifier("BOX001");
        collectionBoxResponse.setIsAssigned(false);
        collectionBoxResponse.setIsEmpty(true);
    }

    @Test
    void registerCollectionBox_ShouldRegisterNewBox() {
        when(collectionBoxRepository.existsByIdentifier(anyString())).thenReturn(false);
        when(collectionBoxMapper.toEntity(any(RegisterCollectionBoxRequest.class))).thenReturn(collectionBox);
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(collectionBox);
        when(collectionBoxMapper.toResponse(any(CollectionBox.class))).thenReturn(collectionBoxResponse);

        CollectionBoxResponse result = collectionBoxService.registerCollectionBox(registerRequest);

        assertNotNull(result);
        assertEquals(collectionBoxResponse.getId(), result.getId());
        assertEquals(collectionBoxResponse.getIdentifier(), result.getIdentifier());
        assertEquals(collectionBoxResponse.getIsAssigned(), result.getIsAssigned());
        assertEquals(collectionBoxResponse.getIsEmpty(), result.getIsEmpty());

        verify(collectionBoxRepository).existsByIdentifier("BOX001");
        verify(collectionBoxMapper).toEntity(registerRequest);
        verify(collectionBoxRepository).save(collectionBox);
        verify(collectionBoxMapper).toResponse(collectionBox);
    }

    @Test
    void registerCollectionBox_WithDuplicateIdentifier_ShouldThrowBusinessException() {
        when(collectionBoxRepository.existsByIdentifier(anyString())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            collectionBoxService.registerCollectionBox(registerRequest);
        });

        assertEquals("Collection box with identifier BOX001 already exists", exception.getMessage());
        verify(collectionBoxRepository).existsByIdentifier("BOX001");
        verifyNoInteractions(collectionBoxMapper);
        verify(collectionBoxRepository, never()).save(any(CollectionBox.class));
    }

    @Test
    void listAllCollectionBoxes_ShouldReturnAllBoxes() {
        List<CollectionBox> boxes = Arrays.asList(collectionBox);
        when(collectionBoxRepository.findAll()).thenReturn(boxes);
        when(collectionBoxMapper.toResponse(any(CollectionBox.class))).thenReturn(collectionBoxResponse);

        List<CollectionBoxResponse> result = collectionBoxService.listAllCollectionBoxes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(collectionBoxResponse, result.get(0));

        verify(collectionBoxRepository).findAll();
        verify(collectionBoxMapper).toResponse(collectionBox);
    }

    @Test
    void unregisterCollectionBox_ShouldRemoveBox() {
        when(collectionBoxRepository.findByIdentifier(anyString())).thenReturn(Optional.of(collectionBox));

        collectionBoxService.unregisterCollectionBox("BOX001");

        assertTrue(collectionBox.getBoxMoneySet().isEmpty());
        verify(collectionBoxRepository).findByIdentifier("BOX001");
        verify(collectionBoxRepository).delete(collectionBox);
    }

    @Test
    void unregisterCollectionBox_WithNonExistingIdentifier_ShouldThrowResourceNotFoundException() {
        when(collectionBoxRepository.findByIdentifier(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            collectionBoxService.unregisterCollectionBox("BOX999");
        });

        assertEquals("Collection box not found with identifier: BOX999", exception.getMessage());
        verify(collectionBoxRepository).findByIdentifier("BOX999");
        verify(collectionBoxRepository, never()).delete(any(CollectionBox.class));
    }

    @Test
    void assignCollectionBox_ToEmptyBox_ShouldAssignToEvent() {
        when(collectionBoxRepository.findByIdentifierWithMoney(anyString())).thenReturn(Optional.of(collectionBox));
        when(fundraisingEventService.findById(anyLong())).thenReturn(fundraisingEvent);
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(collectionBox);
        when(collectionBoxMapper.toResponse(any(CollectionBox.class))).thenReturn(collectionBoxResponse);

        CollectionBoxResponse result = collectionBoxService.assignCollectionBox("BOX001", assignRequest);

        assertNotNull(result);
        assertTrue(collectionBox.getIsAssigned());
        assertEquals(fundraisingEvent, collectionBox.getFundraisingEvent());

        verify(collectionBoxRepository).findByIdentifierWithMoney("BOX001");
        verify(fundraisingEventService).findById(1L);
        verify(collectionBoxRepository).save(collectionBox);
        verify(collectionBoxMapper).toResponse(collectionBox);
    }

    @Test
    void assignCollectionBox_ToAlreadyAssignedBox_ShouldThrowBusinessException() {
        collectionBox.setIsAssigned(true);
        when(collectionBoxRepository.findByIdentifierWithMoney(anyString())).thenReturn(Optional.of(collectionBox));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            collectionBoxService.assignCollectionBox("BOX001", assignRequest);
        });

        assertEquals("Collection box is already assigned to a fundraising event", exception.getMessage());
        verify(collectionBoxRepository).findByIdentifierWithMoney("BOX001");
        verifyNoInteractions(fundraisingEventService);
        verify(collectionBoxRepository, never()).save(any(CollectionBox.class));
    }

    @Test
    void assignCollectionBox_ToNonEmptyBox_ShouldThrowBusinessException() {
        BoxMoney boxMoney = new BoxMoney();
        boxMoney.setCollectionBox(collectionBox);
        boxMoney.setCurrency(Currency.EUR);
        boxMoney.setAmount(BigDecimal.valueOf(100.00));
        collectionBox.getBoxMoneySet().add(boxMoney);

        when(collectionBoxRepository.findByIdentifierWithMoney(anyString())).thenReturn(Optional.of(collectionBox));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            collectionBoxService.assignCollectionBox("BOX001", assignRequest);
        });

        assertEquals("Collection box must be empty before assignment", exception.getMessage());
        verify(collectionBoxRepository).findByIdentifierWithMoney("BOX001");
        verifyNoInteractions(fundraisingEventService);
        verify(collectionBoxRepository, never()).save(any(CollectionBox.class));
    }

    @Test
    void addMoneyToBox_WithExistingCurrency_ShouldAddAmount() {
        BoxMoney existingBoxMoney = new BoxMoney();
        existingBoxMoney.setCollectionBox(collectionBox);
        existingBoxMoney.setCurrency(Currency.EUR);
        existingBoxMoney.setAmount(BigDecimal.valueOf(50.00));

        when(collectionBoxRepository.findByIdentifier(anyString())).thenReturn(Optional.of(collectionBox));
        when(boxMoneyRepository.findByCollectionBoxAndCurrency(any(CollectionBox.class), any(Currency.class)))
                .thenReturn(Optional.of(existingBoxMoney));

        collectionBoxService.addMoneyToBox("BOX001", addMoneyRequest);

        assertEquals(BigDecimal.valueOf(150.00), existingBoxMoney.getAmount());
        verify(collectionBoxRepository).findByIdentifier("BOX001");
        verify(boxMoneyRepository).findByCollectionBoxAndCurrency(collectionBox, Currency.EUR);
        verify(boxMoneyRepository).save(existingBoxMoney);
    }

    @Test
    void addMoneyToBox_WithNewCurrency_ShouldCreateNewBoxMoney() {
        when(collectionBoxRepository.findByIdentifier(anyString())).thenReturn(Optional.of(collectionBox));
        when(boxMoneyRepository.findByCollectionBoxAndCurrency(any(CollectionBox.class), any(Currency.class)))
                .thenReturn(Optional.empty());

        collectionBoxService.addMoneyToBox("BOX001", addMoneyRequest);

        verify(collectionBoxRepository).findByIdentifier("BOX001");
        verify(boxMoneyRepository).findByCollectionBoxAndCurrency(collectionBox, Currency.EUR);
        verify(boxMoneyRepository).save(any(BoxMoney.class));
    }

    @Test
    void emptyCollectionBox_WithAssignedBoxWithMoney_ShouldTransferFunds() {
        collectionBox.setIsAssigned(true);
        collectionBox.setFundraisingEvent(fundraisingEvent);

        BoxMoney eurMoney = new BoxMoney();
        eurMoney.setCollectionBox(collectionBox);
        eurMoney.setCurrency(Currency.EUR);
        eurMoney.setAmount(BigDecimal.valueOf(100.00));

        BoxMoney usdMoney = new BoxMoney();
        usdMoney.setCollectionBox(collectionBox);
        usdMoney.setCurrency(Currency.USD);
        usdMoney.setAmount(BigDecimal.valueOf(50.00));

        collectionBox.getBoxMoneySet().add(eurMoney);
        collectionBox.getBoxMoneySet().add(usdMoney);

        when(collectionBoxRepository.findByIdentifierWithMoney(anyString())).thenReturn(Optional.of(collectionBox));
        when(exchangeRateService.convertAmount(
                eq(BigDecimal.valueOf(100.00)), eq(Currency.EUR), eq(Currency.EUR)))
                .thenReturn(BigDecimal.valueOf(100.00));
        when(exchangeRateService.convertAmount(
                eq(BigDecimal.valueOf(50.00)), eq(Currency.USD), eq(Currency.EUR)))
                .thenReturn(BigDecimal.valueOf(45.00));

        MoneyTransferResponse result = collectionBoxService.emptyCollectionBox("BOX001");

        assertNotNull(result);
        assertEquals("BOX001", result.getCollectionBoxId());
        assertEquals("Test Event", result.getFundraisingEventName());
        assertEquals(Currency.EUR, result.getTargetCurrency());
        assertEquals(BigDecimal.valueOf(145.00), result.getTotalAmountInTargetCurrency());
        assertEquals(BigDecimal.valueOf(145.00), fundraisingEvent.getAccountBalance());

        assertEquals(BigDecimal.ZERO, eurMoney.getAmount());
        assertEquals(BigDecimal.ZERO, usdMoney.getAmount());

        verify(collectionBoxRepository).findByIdentifierWithMoney("BOX001");
        verify(exchangeRateService).convertAmount(BigDecimal.valueOf(100.00), Currency.EUR, Currency.EUR);
        verify(exchangeRateService).convertAmount(BigDecimal.valueOf(50.00), Currency.USD, Currency.EUR);
    }

    @Test
    void emptyCollectionBox_WithUnassignedBox_ShouldThrowBusinessException() {
        when(collectionBoxRepository.findByIdentifierWithMoney(anyString())).thenReturn(Optional.of(collectionBox));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            collectionBoxService.emptyCollectionBox("BOX001");
        });

        assertEquals("Collection box is not assigned to any fundraising event", exception.getMessage());
        verify(collectionBoxRepository).findByIdentifierWithMoney("BOX001");
        verifyNoInteractions(exchangeRateService);
    }

    @Test
    void emptyCollectionBox_WithEmptyBox_ShouldThrowInvalidOperationException() {
        collectionBox.setIsAssigned(true);
        collectionBox.setFundraisingEvent(fundraisingEvent);

        when(collectionBoxRepository.findByIdentifierWithMoney(anyString())).thenReturn(Optional.of(collectionBox));

        InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> {
            collectionBoxService.emptyCollectionBox("BOX001");
        });

        assertEquals("Collection box is empty", exception.getMessage());
        verify(collectionBoxRepository).findByIdentifierWithMoney("BOX001");
        verifyNoInteractions(exchangeRateService);
    }

    @Test
    void emptyCollectionBox_WithZeroAmounts_ShouldThrowInvalidOperationException() {
        collectionBox.setIsAssigned(true);
        collectionBox.setFundraisingEvent(fundraisingEvent);

        BoxMoney eurMoney = new BoxMoney();
        eurMoney.setCollectionBox(collectionBox);
        eurMoney.setCurrency(Currency.EUR);
        eurMoney.setAmount(BigDecimal.ZERO);

        collectionBox.getBoxMoneySet().add(eurMoney);

        when(collectionBoxRepository.findByIdentifierWithMoney(anyString())).thenReturn(Optional.of(collectionBox));

        InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> {
            collectionBoxService.emptyCollectionBox("BOX001");
        });

        assertEquals("Collection box is empty", exception.getMessage());
        verify(collectionBoxRepository).findByIdentifierWithMoney("BOX001");
        verifyNoInteractions(exchangeRateService);
    }
}