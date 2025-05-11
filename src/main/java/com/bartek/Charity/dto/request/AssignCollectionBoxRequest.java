package com.bartek.Charity.dto.request;

import jakarta.validation.constraints.NotNull;

public record AssignCollectionBoxRequest(
        @NotNull(message = "Fundraising event ID is required")
        Long fundraisingEventId
) {}
