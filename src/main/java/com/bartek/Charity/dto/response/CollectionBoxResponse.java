package com.bartek.Charity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionBoxResponse {
    private Long id;
    private String identifier;
    private Boolean isAssigned;
    private Boolean isEmpty;
}