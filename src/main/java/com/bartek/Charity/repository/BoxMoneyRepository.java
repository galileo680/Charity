package com.bartek.Charity.repository;

import com.bartek.Charity.domain.BoxMoney;
import com.bartek.Charity.domain.CollectionBox;
import com.bartek.Charity.domain.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoxMoneyRepository extends JpaRepository<BoxMoney, Long> {
    Optional<BoxMoney> findByCollectionBoxAndCurrency(CollectionBox collectionBox, Currency currency);
}