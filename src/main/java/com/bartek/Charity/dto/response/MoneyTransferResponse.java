package com.bartek.Charity.dto.response;

import com.bartek.Charity.domain.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferResponse {
    private String collectionBoxId;
    private String fundraisingEventName;
    private Map<Currency, BigDecimal> transferredAmounts;
    private Currency targetCurrency;
    private BigDecimal totalAmountInTargetCurrency;
}