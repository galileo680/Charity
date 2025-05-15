package com.bartek.Charity.mapper;

import com.bartek.Charity.domain.FundraisingEvent;
import com.bartek.Charity.domain.enums.Currency;
import com.bartek.Charity.dto.request.CreateFundraisingEventRequest;
import com.bartek.Charity.dto.response.FundraisingEventResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FundraisingEventMapperTest {

    @InjectMocks
    private FundraisingEventMapper fundraisingEventMapper;

    private CreateFundraisingEventRequest createRequest;
    private FundraisingEvent fundraisingEvent;

    @BeforeEach
    void setUp() {
        createRequest = new CreateFundraisingEventRequest();
        createRequest.setName("Charity Event");
        createRequest.setAccountCurrency(Currency.EUR);

        fundraisingEvent = new FundraisingEvent();
        fundraisingEvent.setId(1L);
        fundraisingEvent.setName("Charity Event");
        fundraisingEvent.setAccountCurrency(Currency.EUR);
        fundraisingEvent.setAccountBalance(BigDecimal.valueOf(1000.00));
    }

    @Test
    void toEntity_WithValidRequest_ShouldMapCorrectly() {
        FundraisingEvent result = fundraisingEventMapper.toEntity(createRequest);

        assertNotNull(result);
        assertEquals(createRequest.getName(), result.getName());
        assertEquals(createRequest.getAccountCurrency(), result.getAccountCurrency());
        assertEquals(BigDecimal.ZERO, result.getAccountBalance());
    }

    @Test
    void toEntity_WithNullRequest_ShouldReturnNull() {
        FundraisingEvent result = fundraisingEventMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void toResponse_WithValidEntity_ShouldMapCorrectly() {
        FundraisingEventResponse result = fundraisingEventMapper.toResponse(fundraisingEvent);

        assertNotNull(result);
        assertEquals(fundraisingEvent.getId(), result.getId());
        assertEquals(fundraisingEvent.getName(), result.getName());
        assertEquals(fundraisingEvent.getAccountCurrency(), result.getAccountCurrency());
        assertEquals(fundraisingEvent.getAccountBalance(), result.getAccountBalance());
    }

    @Test
    void toResponse_WithNullEntity_ShouldReturnNull() {
        FundraisingEventResponse result = fundraisingEventMapper.toResponse(null);

        assertNull(result);
    }
}