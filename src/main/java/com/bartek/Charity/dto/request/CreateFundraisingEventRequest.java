package com.bartek.Charity.dto.request;

import com.bartek.Charity.domain.enums.Currency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFundraisingEventRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotNull(message = "Account currency is required")
        private Currency accountCurrency;
}