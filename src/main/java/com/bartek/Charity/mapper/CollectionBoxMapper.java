package com.bartek.Charity.mapper;

import com.bartek.Charity.domain.BoxMoney;
import com.bartek.Charity.domain.CollectionBox;
import com.bartek.Charity.dto.request.RegisterCollectionBoxRequest;
import com.bartek.Charity.dto.response.CollectionBoxResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class CollectionBoxMapper {

    public CollectionBox toEntity(RegisterCollectionBoxRequest request) {
        if (request == null) {
            return null;
        }

        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier(request.getIdentifier());
        collectionBox.setIsAssigned(false);

        return collectionBox;
    }

    public CollectionBoxResponse toResponse(CollectionBox entity) {
        if (entity == null) {
            return null;
        }

        return new CollectionBoxResponse(
                entity.getId(),
                entity.getIdentifier(),
                entity.getIsAssigned(),
                isBoxEmpty(entity)
        );
    }

    private boolean isBoxEmpty(CollectionBox box) {
        Set<BoxMoney> moneySet = box.getBoxMoneySet();
        if (moneySet == null || moneySet.isEmpty()) {
            return true;
        }

        return moneySet.stream()
                .allMatch(money ->
                        money.getAmount() == null ||
                                money.getAmount().compareTo(BigDecimal.ZERO) == 0
                );
    }
}