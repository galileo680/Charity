package com.bartek.Charity.dto.response;

public record CollectionBoxResponse(
        Long id,
        String identifier,
        Boolean isAssigned,
        Boolean isEmpty
) {}