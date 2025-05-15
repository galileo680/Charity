package com.bartek.Charity.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignCollectionBoxRequest {
        @NotNull(message = "Fundraising event ID is required")
        @Positive(message = "Fundraising event ID must be a positive number")
        private Long fundraisingEventId;
}