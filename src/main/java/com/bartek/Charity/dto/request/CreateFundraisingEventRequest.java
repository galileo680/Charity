package com.bartek.Charity.dto.request;

import com.bartek.Charity.domain.enums.Currency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
        @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
        private String name;

        @NotNull(message = "Account currency is required")
        private Currency accountCurrency;
}