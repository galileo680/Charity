package com.bartek.Charity.dto.response;

import com.bartek.Charity.domain.enums.Currency;

import java.math.BigDecimal;

public record FundraisingEventResponse(
        Long id,
        String name,
        Currency accountCurrency,
        BigDecimal accountBalance
) {}