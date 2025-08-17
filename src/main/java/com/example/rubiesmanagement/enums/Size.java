package com.example.rubiesmanagement.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Size {
    XS, S, M, L, XL, XXL;

    @JsonCreator
    public static Size from(String value) {
        return Size.valueOf(value.toUpperCase()); // cho phép L, l, xL,...
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
