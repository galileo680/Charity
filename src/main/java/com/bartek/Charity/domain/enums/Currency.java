package com.bartek.Charity.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Currency {
    PLN,
    EUR,
    USD;


    @JsonCreator
    public static Currency fromString(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Currency.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}