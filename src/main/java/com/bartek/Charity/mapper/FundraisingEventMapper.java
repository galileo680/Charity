package com.bartek.Charity.mapper;

import com.bartek.Charity.domain.FundraisingEvent;
import com.bartek.Charity.dto.request.CreateFundraisingEventRequest;
import com.bartek.Charity.dto.response.FundraisingEventResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FundraisingEventMapper {

    public FundraisingEvent toEntity(CreateFundraisingEventRequest request) {
        if (request == null) {
            return null;
        }

        FundraisingEvent event = new FundraisingEvent();
        event.setName(request.getName());
        event.setAccountCurrency(request.getAccountCurrency());
        event.setAccountBalance(BigDecimal.ZERO);
        return event;
    }

    public FundraisingEventResponse toResponse(FundraisingEvent entity) {
        if (entity == null) {
            return null;
        }

        return new FundraisingEventResponse(
                entity.getId(),
                entity.getName(),
                entity.getAccountCurrency(),
                entity.getAccountBalance()
        );
    }
}