package com.sirgoingfar.currencyconverter.models.data;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class HistoricalRateData {

    @SerializedName("rates")
    private Map<String, Double> rates;

    @SerializedName("timestamp")
    private int timestamp;

    public Map<String, Double> getRates() {
        return rates;
    }

    public int getTimestamp() {
        return timestamp;
    }

}