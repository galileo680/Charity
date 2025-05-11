package com.bartek.Charity.dto.request;

import com.bartek.Charity.domain.enums.Currency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFundraisingEventRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Account currency is required")
        Currency accountCurrency
) {}