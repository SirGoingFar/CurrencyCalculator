package com.sirgoingfar.currencyconverter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sirgoingfar.currencyconverter.App;
import com.sirgoingfar.currencyconverter.models.data.Currency;
import com.sirgoingfar.currencyconverter.models.data.CurrencyData;

import java.util.List;

public class Pref {

    private final String APP_PREF = "app_pref";

    private final String PREF_CURRENCY_DATA = "pref_currency_data";
    private final String PREF_CURRENCY_LIST = "pref_currency_list";

    private static Pref sInstance;
    private SharedPreferences mPref;

    private Gson mGson;

    public static Pref getsInstance() {

        if (sInstance == null)
            sInstance = new Pref();

        return sInstance;
    }

    private Pref() {
        Context context = App.getInstance();
        mPref = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    private Gson getGsonInstance() {
        return mGson;
    }

    public void saveCurrencyData(CurrencyData data) {
        mPref.edit().putString(PREF_CURRENCY_DATA, getGsonInstance().toJson(data, new TypeToken<CurrencyData>() {
        }.getType())).apply();
    }

    public CurrencyData getCurrencyData() {
        String jsonString = mPref.getString(PREF_CURRENCY_DATA, null);
        if (TextUtils.isEmpty(jsonString))
            return null;

        return getGsonInstance().fromJson(jsonString, new TypeToken<CurrencyData>() {
        }.getType());
    }

    public void saveCurrencyList(List<Currency> data) {
        if (data == null)
            return;

        mPref.edit().putString(PREF_CURRENCY_LIST, getGsonInstance().toJson(data, new TypeToken<List<Currency>>() {
        }.getType())).apply();
    }

    public List<Currency> getCurrencyList() {
        String jsonString = mPref.getString(PREF_CURRENCY_LIST, null);
        if (TextUtils.isEmpty(jsonString))
            return null;

        return getGsonInstance().fromJson(jsonString, new TypeToken<List<Currency>>() {
        }.getType());
    }

    public boolean isCurrencyListInCache() {
        List<Currency> data = getCurrencyList();
        return data != null && !data.isEmpty();
    }
}
