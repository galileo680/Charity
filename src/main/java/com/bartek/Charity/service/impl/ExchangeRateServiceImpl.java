package com.bartek.Charity.service.impl;

import com.bartek.Charity.domain.enums.Currency;
import com.bartek.Charity.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
    @Value("${url.exchange-api}")
    private String API_URL;
    private final RestTemplate restTemplate;

    public BigDecimal getExchangeRate(Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency == toCurrency) {
            return BigDecimal.ONE;
        }

        try {
            String url = API_URL + fromCurrency.name();
            ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

            if (response != null && response.getRates() != null) {
                BigDecimal rate = response.getRates().get(toCurrency.name());
                if (rate != null) {
                    return rate;
                }
            }
        } catch (RestClientException e) {
            log.error("Failed to fetch exchange rate from {} to {}", fromCurrency, toCurrency, e);
        }

        // Funkcja zwroci zhardcodowane wartosci jesli api nie dziala / nie jest dostepne
        return getHardcodedRate(fromCurrency, toCurrency);
    }

    public BigDecimal convertAmount(BigDecimal amount, Currency from, Currency to) {
        BigDecimal rate = getExchangeRate(from, to);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getHardcodedRate(Currency from, Currency to) {
        Map<String, BigDecimal> rates = Map.of(
                "EUR_USD", new BigDecimal("1.10"),
                "EUR_PLN", new BigDecimal("4.50"),
                "USD_EUR", new BigDecimal("0.91"),
                "USD_PLN", new BigDecimal("4.10"),
                "PLN_EUR", new BigDecimal("0.22"),
                "PLN_USD", new BigDecimal("0.24")
        );

        String key = from + "_" + to;
        return rates.getOrDefault(key, BigDecimal.ONE);
    }

    private static class ExchangeRateResponse {
        private Map<String, BigDecimal> rates;

        public Map<String, BigDecimal> getRates() {
            return rates;
        }

        public void setRates(Map<String, BigDecimal> rates) {
            this.rates = rates;
        }
    }
}
