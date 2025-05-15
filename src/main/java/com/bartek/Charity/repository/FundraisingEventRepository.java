package com.bartek.Charity.repository;

import com.bartek.Charity.domain.FundraisingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, Long> {
    boolean existsByNameIgnoreCase(String name);
}