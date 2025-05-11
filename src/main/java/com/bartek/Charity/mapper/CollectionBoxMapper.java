package com.bartek.Charity.mapper;

import com.bartek.Charity.domain.CollectionBox;
import com.bartek.Charity.dto.request.RegisterCollectionBoxRequest;
import com.bartek.Charity.dto.response.CollectionBoxResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CollectionBoxMapper {

    CollectionBox toEntity(RegisterCollectionBoxRequest request);

    @Mapping(target = "isEmpty", expression = "java(isBoxEmpty(entity))")
    CollectionBoxResponse toResponse(CollectionBox entity);

    default boolean isBoxEmpty(CollectionBox box) {
        return box.getBoxMoneySet().stream()
                .allMatch(money -> money.getAmount().compareTo(java.math.BigDecimal.ZERO) == 0);
    }
}