package com.bartek.Charity.domain;

import jakarta.persistence.*;
import lombok.*;
import com.bartek.Charity.domain.enums.Currency;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "fundraising_event")
public class FundraisingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_currency", nullable = false)
    private Currency accountCurrency;

    @Column(name = "account_balance", precision = 19, scale = 2)
    private BigDecimal accountBalance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "fundraisingEvent")
    @ToString.Exclude
    private Set<CollectionBox> collectionBoxes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FundraisingEvent that = (FundraisingEvent) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}