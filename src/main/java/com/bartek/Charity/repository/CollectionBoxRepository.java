package com.bartek.Charity.repository;

import com.bartek.Charity.domain.CollectionBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionBoxRepository extends JpaRepository<CollectionBox, Long> {
    Optional<CollectionBox> findByIdentifier(String identifier);
    boolean existsByIdentifier(String identifier);

    @Query("SELECT cb FROM CollectionBox cb LEFT JOIN FETCH cb.boxMoneySet WHERE cb.identifier = :identifier")
    Optional<CollectionBox> findByIdentifierWithMoney(String identifier);
}