package com.sirgoingfar.currencyconverter.models;

import android.app.Application;
import android.text.TextUtils;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.sirgoingfar.currencyconverter.App;
import com.sirgoingfar.currencyconverter.database.DatabaseTxn;
import com.sirgoingfar.currencyconverter.database.entities.HistoricalRateEntity;
import com.sirgoingfar.currencyconverter.database.entities.LatestRateEntity;
import com.sirgoingfar.currencyconverter.models.data.Currency;
import com.sirgoingfar.currencyconverter.models.data.CurrencyData;
import com.sirgoingfar.currencyconverter.models.data.HistoricalRateData;
import com.sirgoingfar.currencyconverter.models.data.LatestRateData;
import com.sirgoingfar.currencyconverter.network.ApiCaller;
import com.sirgoingfar.currencyconverter.network.ApiResponseCallback;
import com.sirgoingfar.currencyconverter.utils.DateUtil;
import com.sirgoingfar.currencyconverter.utils.JsonUtil;
import com.sirgoingfar.currencyconverter.utils.Pref;
import com.sirgoingfar.currencyconverter.utils.StringUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalculatorViewModel extends AndroidViewModel implements ApiResponseCallback {

    private Application application;
    private ApiCaller apiCaller;
    private DatabaseTxn dbTxn;
    private Pref pref = Pref.getsInstance();

    private MutableLiveData<List<Currency>> currencyListLiveData = new MutableLiveData<>();

    private long start;
    private long end;

    public CalculatorViewModel(Application application) {
        super(application);
        this.application = application;
        apiCaller = new ApiCaller(application, this);
        dbTxn = new DatabaseTxn();
        init();
    }

    private void init() {
        prepareCurrencyList();
    }

    private void prepareCurrencyList() {

        if (pref.isCurrencyListInCache()) {
            postCurrencyList(pref.getCurrencyList());
            return;
        }

        //parse JSON string to List<Currency>
        String currencyDataJson = JsonUtil.getCurrencyDataString(application);
        currencyDataJson = JsonUtil.sanitizeJsonString(currencyDataJson);

        if (!TextUtils.isEmpty(currencyDataJson)) {
            try {

                CurrencyData data = new Gson().fromJson(currencyDataJson, new TypeToken<CurrencyData>() {
                }.getType());

                List<CurrencyData.CountryCurrency> countryCurrencies = data.getCountryCurrencies();

                //Generate the currency objects
                if (countryCurrencies.isEmpty())
                    return;

                ArrayList<Currency> currenciesArrayList = new ArrayList<>();
                String currencyString = "";
                for (CurrencyData.CountryCurrency countryCurrency : countryCurrencies) {
                    currencyString = currencyString.concat(countryCurrency.getCurrencyCode()).concat(",");
                    currenciesArrayList.add(new Currency(countryCurrency.getCurrencyCode(), countryCurrency.getName(), countryCurrency.getFlag()));
                }

                //cache the resulting list of currencies
                pref.saveCurrencyList(currenciesArrayList);
                pref.saveCurrencyString(currencyString);
                postCurrencyList(currenciesArrayList);

            } catch (JsonParseException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void getHistoricalRateData() {
        if (!pref.wasHistoricalRateDataPollSuccessful()) {
            dbTxn.deleteHistoricalRates();
            pollAllHistoricalData();
        } else if (pref.canPollYesterdayHistoricalRateData()) {
            pollYesterdayHistoricalRateData();
        }
    }

    private void pollAllHistoricalData() {
        this.start = DateUtil.get90DaysAgoLatestTimeInMillis();
        this.end = DateUtil.getYstDayLatestTimeInMillis();
        pollHistoricalDataBtw(start, end);
    }

    private void pollYesterdayHistoricalRateData() {
        this.start = DateUtil.getYstDayEarliestTimeInMillis();
        this.end = DateUtil.getYstDayLatestTimeInMillis();
        pollHistoricalDataBtw(start, end);
    }

    private void retryHistoricalRateDataPoll() {
        pollHistoricalDataBtw(start, end);
    }

    private void pollHistoricalDataBtw(long start, long end) {
        if (end > start)
            apiCaller.getHistoricalRateFor(pref.getCurrencyString(), StringUtil.getDateStringFor(end));
        else {
            pref.setHistoricalRateDataPollSuccessful(true);
            pref.saveHistoricalRateDataLastPollTime(System.currentTimeMillis());
        }
    }

    public LiveData<List<Currency>> getCurrencyListObserver() {
        return currencyListLiveData;
    }

    public Long getLastRateFetchTime() {
        return pref.getLatestRatePollTimestamp();
    }

    private void postCurrencyList(List<Currency> data) {
        if (data == null || data.isEmpty())
            return;

        currencyListLiveData.postValue(data);
    }

    public void fetchLatestRate() {
        apiCaller.fetchLatestRateFor(pref.getCurrencyString());
    }

    public LiveData<List<LatestRateEntity>> getLatestRateLiveData() {
        return App.getAppDao().getLatestRates();
    }

    public LiveData<List<HistoricalRateEntity>> getHistoricalRateLiveData(String code, long minTime) {
        return App.getAppDao().getHistoricalRates(code, minTime);
    }

    @Override
    public <T> void onSuccess(T response) {
        if (response instanceof LatestRateData) {
            handleLatestRateData((LatestRateData) response);
        } else if (response instanceof HistoricalRateData) {
            handleHistoricalRateData((HistoricalRateData) response);
        }
    }

    @Override
    public <T> void onFailure(T response) {
        if (response instanceof HistoricalRateData) {
            //Todo: Implement max retry count
            retryHistoricalRateDataPoll();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    private void handleLatestRateData(LatestRateData data) {
        pref.saveLatestRatePollTimestamp(data.getTimestamp());
        List<LatestRateEntity> latestRateDataList = processLatestData(data);

        if (latestRateDataList == null || latestRateDataList.isEmpty())
            return;

        dbTxn.addLatestRates(latestRateDataList);
    }

    private List<LatestRateEntity> processLatestData(LatestRateData data) {
        List<LatestRateEntity> list = new ArrayList<>();

        if (data == null || data.getRates() == null || data.getRates().isEmpty())
            return null;

        Map<String, Double> rateMap = data.getRates();

        for (Map.Entry<String, Double> entry : rateMap.entrySet())
            list.add(new LatestRateEntity(entry.getKey(), entry.getValue(), data.getTimestamp()));

        return list;
    }

    private void handleHistoricalRateData(HistoricalRateData data) {
        List<HistoricalRateEntity> historicalRateDataList = processHistoricalData(data);


        if (historicalRateDataList.isEmpty())
            return;

        dbTxn.addHistoricalRates(historicalRateDataList);

        this.end -= DateUtil.A_DAY_MILLIS;
        pollHistoricalDataBtw(start, end);
    }

    private List<HistoricalRateEntity> processHistoricalData(HistoricalRateData data) {
        List<HistoricalRateEntity> list = new ArrayList<>();

        if (data == null || data.getRates() == null || data.getRates().isEmpty())
            return null;

        Map<String, Double> rateMap = data.getRates();

        for (Map.Entry<String, Double> entry : rateMap.entrySet())
            list.add(new HistoricalRateEntity(entry.getKey(), entry.getValue(), data.getTimestamp()));

        return list;
    }
}
