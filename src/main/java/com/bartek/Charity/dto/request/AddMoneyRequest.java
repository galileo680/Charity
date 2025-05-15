package com.bartek.Charity.dto.request;

import com.bartek.Charity.domain.enums.Currency;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMoneyRequest {
        @NotNull(message = "Currency is required")
        private Currency currency;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be positive")
        @DecimalMax(value = "1000000.00", inclusive = true, message = "Amount cannot exceed 1,000,000.00")
        @Digits(integer = 7, fraction = 2, message = "Amount must have at most 7 digits before the decimal point and 2 after")
        private BigDecimal amount;
}