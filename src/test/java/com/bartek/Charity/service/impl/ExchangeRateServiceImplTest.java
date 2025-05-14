package com.bartek.Charity.service.impl;

import com.bartek.Charity.domain.enums.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    @Test
    void getExchangeRate_SameCurrency_ReturnsOne() {
        BigDecimal result = exchangeRateService.getExchangeRate(Currency.EUR, Currency.EUR);

        assertEquals(BigDecimal.ONE, result);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getExchangeRate_ApiSuccess_ReturnsApiRate() {
        ExchangeRateServiceImpl.ExchangeRateResponse response = new ExchangeRateServiceImpl.ExchangeRateResponse();
        response.setRates(Map.of(
                "USD", new BigDecimal("1.12"),
                "PLN", new BigDecimal("4.55")
        ));

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateServiceImpl.ExchangeRateResponse.class)))
                .thenReturn(response);

        BigDecimal result = exchangeRateService.getExchangeRate(Currency.EUR, Currency.USD);

        assertEquals(new BigDecimal("1.12"), result);
        verify(restTemplate).getForObject(contains("EUR"), eq(ExchangeRateServiceImpl.ExchangeRateResponse.class));
    }

    @Test
    void getExchangeRate_ApiFailure_ReturnsHardcodedRate() {
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateServiceImpl.ExchangeRateResponse.class)))
                .thenThrow(new RestClientException("API unavailable"));

        BigDecimal result = exchangeRateService.getExchangeRate(Currency.EUR, Currency.PLN);

        assertEquals(new BigDecimal("4.50"), result);
        verify(restTemplate).getForObject(contains("EUR"), eq(ExchangeRateServiceImpl.ExchangeRateResponse.class));
    }

    @Test
    void getExchangeRate_ApiReturnsNull_ReturnsHardcodedRate() {
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateServiceImpl.ExchangeRateResponse.class)))
                .thenReturn(null);

        BigDecimal result = exchangeRateService.getExchangeRate(Currency.USD, Currency.PLN);

        assertEquals(new BigDecimal("4.10"), result);
        verify(restTemplate).getForObject(contains("USD"), eq(ExchangeRateServiceImpl.ExchangeRateResponse.class));
    }

    @Test
    void getExchangeRate_ApiReturnsEmptyRates_ReturnsHardcodedRate() {
        ExchangeRateServiceImpl.ExchangeRateResponse response = new ExchangeRateServiceImpl.ExchangeRateResponse();
        response.setRates(Map.of());

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateServiceImpl.ExchangeRateResponse.class)))
                .thenReturn(response);

        BigDecimal result = exchangeRateService.getExchangeRate(Currency.PLN, Currency.EUR);

        assertEquals(new BigDecimal("0.22"), result);
        verify(restTemplate).getForObject(contains("PLN"), eq(ExchangeRateServiceImpl.ExchangeRateResponse.class));
    }

    @Test
    void convertAmount_ConvertsWithCorrectRate() {
        BigDecimal amount = new BigDecimal("100.00");
        Currency from = Currency.EUR;
        Currency to = Currency.PLN;

        ExchangeRateServiceImpl spyService = spy(exchangeRateService);
        doReturn(new BigDecimal("4.50")).when(spyService).getExchangeRate(from, to);

        BigDecimal result = spyService.convertAmount(amount, from, to);

        assertEquals(new BigDecimal("450.00").setScale(2, RoundingMode.HALF_UP), result);
        verify(spyService).getExchangeRate(from, to);
    }

    @Test
    void convertAmount_HandlesZeroAmount() {
        BigDecimal amount = BigDecimal.ZERO;

        BigDecimal result = exchangeRateService.convertAmount(amount, Currency.EUR, Currency.PLN);

        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), result);
    }

    @Test
    void convertAmount_RoundsToTwoDecimalPlaces() {
        BigDecimal amount = new BigDecimal("100.00");
        Currency from = Currency.EUR;
        Currency to = Currency.PLN;

        ExchangeRateServiceImpl spyService = spy(exchangeRateService);
        doReturn(new BigDecimal("4.567")).when(spyService).getExchangeRate(from, to);

        BigDecimal result = spyService.convertAmount(amount, from, to);

        assertEquals(new BigDecimal("456.70"), result);
        verify(spyService).getExchangeRate(from, to);
    }
}