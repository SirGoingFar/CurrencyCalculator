package com.sirgoingfar.currencyconverter.models.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CurrencyData {

    @SerializedName("Response")
    private List<CountryCurrency> response;

    public List<CountryCurrency> getCountryCurrencies() {
        return response;
    }


    public class CountryCurrency {

        @SerializedName("Flag")
        private String flag;

        @SerializedName("CurrencyCode")
        private String currencyCode;

        @SerializedName("Name")
        private String name;

        public String getFlag() {
            return flag;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public String getName() {
            return name;
        }
    }
}