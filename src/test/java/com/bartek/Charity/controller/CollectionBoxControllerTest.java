package com.bartek.Charity.controller;

import com.bartek.Charity.config.TestConfig;
import com.bartek.Charity.domain.enums.Currency;
import com.bartek.Charity.dto.request.AddMoneyRequest;
import com.bartek.Charity.dto.request.AssignCollectionBoxRequest;
import com.bartek.Charity.dto.request.RegisterCollectionBoxRequest;
import com.bartek.Charity.dto.response.CollectionBoxResponse;
import com.bartek.Charity.dto.response.MoneyTransferResponse;
import com.bartek.Charity.exception.ResourceNotFoundException;
import com.bartek.Charity.service.CollectionBoxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(CollectionBoxController.class)
@Import(TestConfig.class)
class CollectionBoxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CollectionBoxService collectionBoxService;

    private RegisterCollectionBoxRequest registerRequest;
    private AssignCollectionBoxRequest assignRequest;
    private AddMoneyRequest addMoneyRequest;
    private CollectionBoxResponse collectionBoxResponse;
    private List<CollectionBoxResponse> collectionBoxResponses;
    private MoneyTransferResponse moneyTransferResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterCollectionBoxRequest();
        registerRequest.setIdentifier("BOX001");

        assignRequest = new AssignCollectionBoxRequest();
        assignRequest.setFundraisingEventId(1L);

        addMoneyRequest = new AddMoneyRequest();
        addMoneyRequest.setCurrency(Currency.EUR);
        addMoneyRequest.setAmount(BigDecimal.valueOf(100.00));

        collectionBoxResponse = new CollectionBoxResponse();
        collectionBoxResponse.setId(1L);
        collectionBoxResponse.setIdentifier("BOX001");
        collectionBoxResponse.setIsAssigned(false);
        collectionBoxResponse.setIsEmpty(true);

        CollectionBoxResponse collectionBoxResponse2 = new CollectionBoxResponse();
        collectionBoxResponse2.setId(2L);
        collectionBoxResponse2.setIdentifier("BOX002");
        collectionBoxResponse2.setIsAssigned(true);
        collectionBoxResponse2.setIsEmpty(false);

        collectionBoxResponses = Arrays.asList(collectionBoxResponse, collectionBoxResponse2);

        Map<Currency, BigDecimal> transferredAmounts = new HashMap<>();
        transferredAmounts.put(Currency.EUR, BigDecimal.valueOf(100.00));
        transferredAmounts.put(Currency.USD, BigDecimal.valueOf(50.00));

        moneyTransferResponse = new MoneyTransferResponse();
        moneyTransferResponse.setCollectionBoxId("BOX001");
        moneyTransferResponse.setFundraisingEventName("Test Event");
        moneyTransferResponse.setTransferredAmounts(transferredAmounts);
        moneyTransferResponse.setTargetCurrency(Currency.EUR);
        moneyTransferResponse.setTotalAmountInTargetCurrency(BigDecimal.valueOf(145.00));
    }

    @Test
    void registerCollectionBox_WithValidRequest_ShouldReturnCreated() throws Exception {
        when(collectionBoxService.registerCollectionBox(any(RegisterCollectionBoxRequest.class)))
                .thenReturn(collectionBoxResponse);

        mockMvc.perform(post("/collection-boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.identifier").value("BOX001"))
                .andExpect(jsonPath("$.isAssigned").value(false))
                .andExpect(jsonPath("$.isEmpty").value(true));
    }

    @Test
    void listAllCollectionBoxes_ShouldReturnAllBoxes() throws Exception {
        when(collectionBoxService.listAllCollectionBoxes()).thenReturn(collectionBoxResponses);

        mockMvc.perform(get("/collection-boxes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].identifier").value("BOX001"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].identifier").value("BOX002"));
    }

    @Test
    void unregisterCollectionBox_WithExistingIdentifier_ShouldReturnNoContent() throws Exception {
        doNothing().when(collectionBoxService).unregisterCollectionBox(anyString());

        mockMvc.perform(delete("/collection-boxes/{identifier}", "BOX001"))
                .andExpect(status().isNoContent());

        verify(collectionBoxService).unregisterCollectionBox("BOX001");
    }

    @Test
    void unregisterCollectionBox_WithNonExistingIdentifier_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Collection box not found with identifier: BOX999"))
                .when(collectionBoxService).unregisterCollectionBox("BOX999");

        mockMvc.perform(delete("/collection-boxes/{identifier}", "BOX999"))
                .andExpect(status().isNotFound());

        verify(collectionBoxService).unregisterCollectionBox("BOX999");
    }

    @Test
    void assignCollectionBox_WithValidRequest_ShouldReturnAssignedBox() throws Exception {
        CollectionBoxResponse assignedResponse = new CollectionBoxResponse();
        assignedResponse.setId(1L);
        assignedResponse.setIdentifier("BOX001");
        assignedResponse.setIsAssigned(true);
        assignedResponse.setIsEmpty(true);

        when(collectionBoxService.assignCollectionBox(anyString(), any(AssignCollectionBoxRequest.class)))
                .thenReturn(assignedResponse);

        mockMvc.perform(put("/collection-boxes/{identifier}/assign", "BOX001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.identifier").value("BOX001"))
                .andExpect(jsonPath("$.isAssigned").value(true));

        verify(collectionBoxService).assignCollectionBox("BOX001", assignRequest);
    }

    @Test
    void addMoneyToBox_WithValidRequest_ShouldReturnOk() throws Exception {
        doNothing().when(collectionBoxService).addMoneyToBox(anyString(), any(AddMoneyRequest.class));

        mockMvc.perform(post("/collection-boxes/{identifier}/money", "BOX001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addMoneyRequest)))
                .andExpect(status().isOk());

        verify(collectionBoxService).addMoneyToBox("BOX001", addMoneyRequest);
    }


    @Test
    void emptyCollectionBox_WithValidIdentifier_ShouldReturnTransferDetails() throws Exception {
        when(collectionBoxService.emptyCollectionBox(anyString())).thenReturn(moneyTransferResponse);

        mockMvc.perform(post("/collection-boxes/{identifier}/empty", "BOX001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collectionBoxId").value("BOX001"))
                .andExpect(jsonPath("$.fundraisingEventName").value("Test Event"))
                .andExpect(jsonPath("$.targetCurrency").value("EUR"))
                .andExpect(jsonPath("$.totalAmountInTargetCurrency").value(145.00));

        verify(collectionBoxService).emptyCollectionBox("BOX001");
    }
}