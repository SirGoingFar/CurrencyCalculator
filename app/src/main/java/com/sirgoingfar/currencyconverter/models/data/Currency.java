package com.sirgoingfar.currencyconverter.models.data;

import java.math.BigDecimal;

public class Currency implements Cloneable {
    private String code, flagUrl;
    private BigDecimal rate, conversionValue;

    public Currency(String code, String flagUrl) {
        this.code = code;
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

    public String getFlagUrl() {
        return flagUrl;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getConversionValue() {
        return conversionValue;
    }

    public Currency copy() {
        Currency clone = null;
        try {
            clone = (Currency) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
