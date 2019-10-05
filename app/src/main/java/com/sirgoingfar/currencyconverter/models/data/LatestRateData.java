package com.sirgoingfar.currencyconverter.models.data;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class LatestRateData {

    @SerializedName("rates")
    private Map<String, Double> rates;

    @SerializedName("timestamp")
    private long timestamp;

    public Map<String, Double> getRates() {
        return rates;
    }

    public long getTimestamp() {
        return timestamp;
    }
}