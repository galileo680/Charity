package com.bartek.Charity.controller;

import com.bartek.Charity.config.TestConfig;
import com.bartek.Charity.domain.enums.Currency;
import com.bartek.Charity.dto.request.CreateFundraisingEventRequest;
import com.bartek.Charity.dto.response.FinancialReportItemResponse;
import com.bartek.Charity.dto.response.FundraisingEventResponse;
import com.bartek.Charity.service.FundraisingEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(FundraisingEventController.class)
@Import(TestConfig.class)
class FundraisingEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FundraisingEventService fundraisingEventService;

    private CreateFundraisingEventRequest createRequest;
    private FundraisingEventResponse fundraisingEventResponse;
    private List<FinancialReportItemResponse> financialReport;

    @BeforeEach
    void setUp() {
        createRequest = new CreateFundraisingEventRequest();
        createRequest.setName("Charity Event");
        createRequest.setAccountCurrency(Currency.EUR);

        fundraisingEventResponse = new FundraisingEventResponse();
        fundraisingEventResponse.setId(1L);
        fundraisingEventResponse.setName("Charity Event");
        fundraisingEventResponse.setAccountCurrency(Currency.EUR);

        FinancialReportItemResponse item1 = new FinancialReportItemResponse();
        item1.setFundraisingEventName("Charity One");
        item1.setCurrency(Currency.EUR);
        item1.setAmount(BigDecimal.valueOf(2048.00));

        FinancialReportItemResponse item2 = new FinancialReportItemResponse();
        item2.setFundraisingEventName("All for hope");
        item2.setCurrency(Currency.PLN);
        item2.setAmount(BigDecimal.valueOf(512.64));

        financialReport = Arrays.asList(item1, item2);
    }

    @Test
    void createFundraisingEvent_WithValidRequest_ShouldReturnCreated() throws Exception {
        when(fundraisingEventService.createFundraisingEvent(any(CreateFundraisingEventRequest.class)))
                .thenReturn(fundraisingEventResponse);

        mockMvc.perform(post("/fundraising-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Charity Event"))
                .andExpect(jsonPath("$.accountCurrency").value("EUR"));
    }

    @Test
    void getFinancialReport_ShouldReturnReportItems() throws Exception {
        when(fundraisingEventService.getFinancialReport()).thenReturn(financialReport);

        mockMvc.perform(get("/fundraising-events/financial-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fundraisingEventName").value("Charity One"))
                .andExpect(jsonPath("$[0].currency").value("EUR"))
                .andExpect(jsonPath("$[0].amount").value(2048.00))
                .andExpect(jsonPath("$[1].fundraisingEventName").value("All for hope"))
                .andExpect(jsonPath("$[1].currency").value("PLN"))
                .andExpect(jsonPath("$[1].amount").value(512.64));
    }
}