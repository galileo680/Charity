package com.bartek.Charity.mapper;

import com.bartek.Charity.domain.BoxMoney;
import com.bartek.Charity.domain.CollectionBox;
import com.bartek.Charity.domain.FundraisingEvent;
import com.bartek.Charity.domain.enums.Currency;
import com.bartek.Charity.dto.request.RegisterCollectionBoxRequest;
import com.bartek.Charity.dto.response.CollectionBoxResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CollectionBoxMapperTest {

    @InjectMocks
    private CollectionBoxMapper collectionBoxMapper;

    private RegisterCollectionBoxRequest registerRequest;
    private CollectionBox collectionBox;
    private CollectionBox collectionBoxWithMoney;
    private CollectionBox collectionBoxWithZeroMoney;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterCollectionBoxRequest();
        registerRequest.setIdentifier("BOX001");

        collectionBox = new CollectionBox();
        collectionBox.setId(1L);
        collectionBox.setIdentifier("BOX001");
        collectionBox.setIsAssigned(false);
        collectionBox.setBoxMoneySet(new HashSet<>());

        collectionBoxWithMoney = new CollectionBox();
        collectionBoxWithMoney.setId(2L);
        collectionBoxWithMoney.setIdentifier("BOX002");
        collectionBoxWithMoney.setIsAssigned(true);

        FundraisingEvent event = new FundraisingEvent();
        event.setId(1L);
        event.setName("Test Event");
        collectionBoxWithMoney.setFundraisingEvent(event);

        Set<BoxMoney> moneySet = new HashSet<>();
        BoxMoney boxMoney = new BoxMoney();
        boxMoney.setId(1L);
        boxMoney.setCollectionBox(collectionBoxWithMoney);
        boxMoney.setCurrency(Currency.EUR);
        boxMoney.setAmount(BigDecimal.valueOf(100.00));
        moneySet.add(boxMoney);

        collectionBoxWithMoney.setBoxMoneySet(moneySet);

        collectionBoxWithZeroMoney = new CollectionBox();
        collectionBoxWithZeroMoney.setId(3L);
        collectionBoxWithZeroMoney.setIdentifier("BOX003");
        collectionBoxWithZeroMoney.setIsAssigned(true);

        Set<BoxMoney> zeroMoneySet = new HashSet<>();
        BoxMoney zeroBoxMoney = new BoxMoney();
        zeroBoxMoney.setId(2L);
        zeroBoxMoney.setCollectionBox(collectionBoxWithZeroMoney);
        zeroBoxMoney.setCurrency(Currency.EUR);
        zeroBoxMoney.setAmount(BigDecimal.ZERO);
        zeroMoneySet.add(zeroBoxMoney);

        collectionBoxWithZeroMoney.setBoxMoneySet(zeroMoneySet);
    }

    @Test
    void toEntity_WithValidRequest_ShouldMapCorrectly() {
        CollectionBox result = collectionBoxMapper.toEntity(registerRequest);

        assertNotNull(result);
        assertEquals(registerRequest.getIdentifier(), result.getIdentifier());
        assertFalse(result.getIsAssigned());
    }

    @Test
    void toEntity_WithNullRequest_ShouldReturnNull() {
        CollectionBox result = collectionBoxMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void toResponse_WithEmptyBox_ShouldMapCorrectly() {
        CollectionBoxResponse result = collectionBoxMapper.toResponse(collectionBox);

        assertNotNull(result);
        assertEquals(collectionBox.getId(), result.getId());
        assertEquals(collectionBox.getIdentifier(), result.getIdentifier());
        assertEquals(collectionBox.getIsAssigned(), result.getIsAssigned());
        assertTrue(result.getIsEmpty());
    }

    @Test
    void toResponse_WithBoxContainingMoney_ShouldMapCorrectly() {
        CollectionBoxResponse result = collectionBoxMapper.toResponse(collectionBoxWithMoney);

        assertNotNull(result);
        assertEquals(collectionBoxWithMoney.getId(), result.getId());
        assertEquals(collectionBoxWithMoney.getIdentifier(), result.getIdentifier());
        assertEquals(collectionBoxWithMoney.getIsAssigned(), result.getIsAssigned());
        assertFalse(result.getIsEmpty());
    }

    @Test
    void toResponse_WithBoxContainingZeroMoney_ShouldBeConsideredEmpty() {
        CollectionBoxResponse result = collectionBoxMapper.toResponse(collectionBoxWithZeroMoney);

        assertNotNull(result);
        assertEquals(collectionBoxWithZeroMoney.getId(), result.getId());
        assertEquals(collectionBoxWithZeroMoney.getIdentifier(), result.getIdentifier());
        assertEquals(collectionBoxWithZeroMoney.getIsAssigned(), result.getIsAssigned());
        assertTrue(result.getIsEmpty());
    }

    @Test
    void toResponse_WithNullEntity_ShouldReturnNull() {
        CollectionBoxResponse result = collectionBoxMapper.toResponse(null);

        assertNull(result);
    }
}