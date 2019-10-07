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
    private final String PREF_CURRENCY_STRING = "pref_currency_string";
    private final String PREF_LATEST_RATE_DATA_POLL_TIME = "pref_latest_rate_data_poll_time";
    private final String PREF_LAST_HISTORICAL_RATE_DATA_POLL_TIME = "pref_historical_rate_data_poll_time";
    private final String PREF_HISTORICAL_RATE_DATA_POLL_SUCCESSFUL = "pref_historical_rate_data_poll_successful";

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

    public void saveCurrencyString(String listString) {
        if (TextUtils.isEmpty(listString))
            return;

        mPref.edit().putString(PREF_CURRENCY_STRING, listString).apply();
    }

    public String getCurrencyString() {
        return mPref.getString(PREF_CURRENCY_STRING, null);
    }

    public void saveLatestRatePollTimestamp(long timestamp) {
        mPref.edit().putLong(PREF_LATEST_RATE_DATA_POLL_TIME, timestamp).apply();
    }

    public long getLatestRatePollTimestamp() {
        return mPref.getLong(PREF_LATEST_RATE_DATA_POLL_TIME, 0L);
    }

    public void setHistoricalRateDataPollSuccessful(boolean successful) {
        mPref.edit().putBoolean(PREF_HISTORICAL_RATE_DATA_POLL_SUCCESSFUL, successful).apply();
    }

    public boolean wasHistoricalRateDataPollSuccessful() {
        return mPref.getBoolean(PREF_HISTORICAL_RATE_DATA_POLL_SUCCESSFUL, false);
    }

    public void saveHistoricalRateDataLastPollTime(long time) {
        mPref.edit().putLong(PREF_LAST_HISTORICAL_RATE_DATA_POLL_TIME, time).apply();
    }

    public long getLastHistoricalRateDataPollTime() {
        return mPref.getLong(PREF_LAST_HISTORICAL_RATE_DATA_POLL_TIME, 0);
    }

    public boolean canPollYesterdayHistoricalRateData() {
        long lastPollTime = getLastHistoricalRateDataPollTime();

        if (lastPollTime <= 0)
            return true;

        return DateUtil.isTimeTwoDaysAgoOrMore(lastPollTime);
    }
}
