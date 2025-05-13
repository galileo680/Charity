package com.bartek.Charity.service;

import com.bartek.Charity.dto.request.AddMoneyRequest;
import com.bartek.Charity.dto.request.AssignCollectionBoxRequest;
import com.bartek.Charity.dto.request.RegisterCollectionBoxRequest;
import com.bartek.Charity.dto.response.CollectionBoxResponse;
import com.bartek.Charity.dto.response.MoneyTransferResponse;

import java.util.List;

public interface CollectionBoxService {
    CollectionBoxResponse registerCollectionBox(RegisterCollectionBoxRequest request);
    List<CollectionBoxResponse> listAllCollectionBoxes();
    void unregisterCollectionBox(String identifier);
    CollectionBoxResponse assignCollectionBox(String identifier, AssignCollectionBoxRequest request);
    void addMoneyToBox(String identifier, AddMoneyRequest request);
    MoneyTransferResponse emptyCollectionBox(String identifier);
}
