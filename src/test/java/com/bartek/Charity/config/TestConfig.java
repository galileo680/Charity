package com.bartek.Charity.config;

import com.bartek.Charity.service.CollectionBoxService;
import com.bartek.Charity.service.FundraisingEventService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {
    @Bean
    public FundraisingEventService fundraisingEventService() {
        return mock(FundraisingEventService.class);
    }
    @Bean
    public CollectionBoxService collectionBoxService() {
        return mock(CollectionBoxService.class);
    }
}