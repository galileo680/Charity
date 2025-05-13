package com.bartek.Charity.service;

import com.bartek.Charity.domain.enums.Currency;

import java.math.BigDecimal;

public interface ExchangeRateService {
    BigDecimal getExchangeRate(Currency fromCurrency, Currency toCurrency);
    BigDecimal convertAmount(BigDecimal amount, Currency from, Currency to);
}
