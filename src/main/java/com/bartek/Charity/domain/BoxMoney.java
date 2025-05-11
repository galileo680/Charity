package com.bartek.Charity.domain;

import com.bartek.Charity.domain.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "box_money",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"collection_box_id", "currency"})
        })
public class BoxMoney {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "collection_box_id", nullable = false)
    @ToString.Exclude
    private CollectionBox collectionBox;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoxMoney boxMoney = (BoxMoney) o;
        return id != null && id.equals(boxMoney.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}