package com.bartek.Charity.mapper;

import com.bartek.Charity.domain.FundraisingEvent;
import com.bartek.Charity.dto.request.CreateFundraisingEventRequest;
import com.bartek.Charity.dto.response.FundraisingEventResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FundraisingEventMapper {

    FundraisingEvent toEntity(CreateFundraisingEventRequest request);

    FundraisingEventResponse toResponse(FundraisingEvent entity);
}