package com.sirgoingfar.currencyconverter.models.data;

import java.math.BigDecimal;

public class Currency implements Cloneable {
    private String code, name, flagUrl;
    private BigDecimal rate, conversionValue;

    public Currency(String code, String name, String flagUrl) {
        this.code = code;
        this.name = name;
        this.flagUrl = flagUrl;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setConversionValue(BigDecimal conversionValue) {
        this.conversionValue = conversionValue;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getConversionValue() {
        return conversionValue;
    }
}
