package com.bartek.Charity.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterCollectionBoxRequest(
        @NotBlank(message = "Identifier is required")
        String identifier
) {}