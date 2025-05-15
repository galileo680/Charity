package com.bartek.Charity.service.impl;

import com.bartek.Charity.domain.FundraisingEvent;
import com.bartek.Charity.domain.enums.Currency;
import com.bartek.Charity.dto.request.CreateFundraisingEventRequest;
import com.bartek.Charity.dto.response.FinancialReportItemResponse;
import com.bartek.Charity.dto.response.FundraisingEventResponse;
import com.bartek.Charity.exception.BusinessException;
import com.bartek.Charity.exception.ResourceNotFoundException;
import com.bartek.Charity.mapper.FundraisingEventMapper;
import com.bartek.Charity.repository.FundraisingEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundraisingEventServiceImplTest {

    @Mock
    private FundraisingEventRepository fundraisingEventRepository;

    @Mock
    private FundraisingEventMapper fundraisingEventMapper;

    @InjectMocks
    private FundraisingEventServiceImpl fundraisingEventService;

    private FundraisingEvent fundraisingEvent;
    private FundraisingEventResponse fundraisingEventResponse;
    private CreateFundraisingEventRequest createRequest;

    @BeforeEach
    void setUp() {
        fundraisingEvent = new FundraisingEvent();
        fundraisingEvent.setId(1L);
        fundraisingEvent.setName("Charity Event");
        fundraisingEvent.setAccountCurrency(Currency.EUR);
        fundraisingEvent.setAccountBalance(BigDecimal.valueOf(1000.00));

        fundraisingEventResponse = new FundraisingEventResponse();
        fundraisingEventResponse.setId(1L);
        fundraisingEventResponse.setName("Charity Event");
        fundraisingEventResponse.setAccountCurrency(Currency.EUR);

        createRequest = new CreateFundraisingEventRequest();
        createRequest.setName("Charity Event");
        createRequest.setAccountCurrency(Currency.EUR);
    }

    @Test
    void createFundraisingEvent_ShouldCreateAndReturnEvent() {
        when(fundraisingEventMapper.toEntity(any(CreateFundraisingEventRequest.class))).thenReturn(fundraisingEvent);
        when(fundraisingEventRepository.save(any(FundraisingEvent.class))).thenReturn(fundraisingEvent);
        when(fundraisingEventMapper.toResponse(any(FundraisingEvent.class))).thenReturn(fundraisingEventResponse);

        FundraisingEventResponse result = fundraisingEventService.createFundraisingEvent(createRequest);

        assertNotNull(result);
        assertEquals(fundraisingEventResponse.getId(), result.getId());
        assertEquals(fundraisingEventResponse.getName(), result.getName());
        assertEquals(fundraisingEventResponse.getAccountCurrency(), result.getAccountCurrency());

        verify(fundraisingEventMapper).toEntity(createRequest);
        verify(fundraisingEventRepository).save(fundraisingEvent);
        verify(fundraisingEventMapper).toResponse(fundraisingEvent);
    }

    @Test
    void createFundraisingEvent_WithDuplicateName_ShouldThrowBusinessException() {
        when(fundraisingEventRepository.existsByNameIgnoreCase("Charity Event")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> fundraisingEventService.createFundraisingEvent(createRequest));

        assertEquals("Fundraising event with name 'Charity Event' already exists", ex.getMessage());
        verify(fundraisingEventRepository).existsByNameIgnoreCase("Charity Event");
        verify(fundraisingEventRepository, never()).save(any());
    }

    @Test
    void findById_WithExistingId_ShouldReturnEvent() {
        Long id = 1L;
        when(fundraisingEventRepository.findById(id)).thenReturn(Optional.of(fundraisingEvent));

        FundraisingEvent result = fundraisingEventService.findById(id);

        assertNotNull(result);
        assertEquals(fundraisingEvent.getId(), result.getId());
        assertEquals(fundraisingEvent.getName(), result.getName());
        assertEquals(fundraisingEvent.getAccountCurrency(), result.getAccountCurrency());
        assertEquals(fundraisingEvent.getAccountBalance(), result.getAccountBalance());

        verify(fundraisingEventRepository).findById(id);
    }

    @Test
    void findById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        Long id = 999L;
        when(fundraisingEventRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            fundraisingEventService.findById(id);
        });

        assertEquals("Fundraising event not found with id: " + id, exception.getMessage());
        verify(fundraisingEventRepository).findById(id);
    }

    @Test
    void getFinancialReport_ShouldReturnListOfReportItems() {
        FundraisingEvent event1 = new FundraisingEvent();
        event1.setId(1L);
        event1.setName("Charity One");
        event1.setAccountCurrency(Currency.EUR);
        event1.setAccountBalance(BigDecimal.valueOf(2048.00));

        FundraisingEvent event2 = new FundraisingEvent();
        event2.setId(2L);
        event2.setName("All for hope");
        event2.setAccountCurrency(Currency.PLN);
        event2.setAccountBalance(BigDecimal.valueOf(512.64));

        List<FundraisingEvent> events = Arrays.asList(event1, event2);

        when(fundraisingEventRepository.findAll()).thenReturn(events);

        List<FinancialReportItemResponse> result = fundraisingEventService.getFinancialReport();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Charity One", result.get(0).getFundraisingEventName());
        assertEquals(Currency.EUR, result.get(0).getCurrency());
        assertEquals(BigDecimal.valueOf(2048.00), result.get(0).getAmount());

        assertEquals("All for hope", result.get(1).getFundraisingEventName());
        assertEquals(Currency.PLN, result.get(1).getCurrency());
        assertEquals(BigDecimal.valueOf(512.64), result.get(1).getAmount());

        verify(fundraisingEventRepository).findAll();
    }
}