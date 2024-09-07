package com.zerobase.storeapi.domain.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    CAKE("cake"),
    TART("tart"),
    MACARON("macaron"),
    BREAD("bread"),
    COOKIE("cookie"),
    CHOCOLATE("chocolate"),
    SCONE("scone"),;


    private final String type;

    Category(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return this.type;
    }
}
