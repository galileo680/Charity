package com.bartek.Charity.dto.request;

import jakarta.validation.constraints.NotNull;
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
        private Long fundraisingEventId;
}