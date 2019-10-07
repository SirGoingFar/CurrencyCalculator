package com.sirgoingfar.currencyconverter.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sirgoingfar.currencyconverter.BuildConfig;
import com.sirgoingfar.currencyconverter.models.data.HistoricalRateData;
import com.sirgoingfar.currencyconverter.models.data.LatestRateData;
import com.sirgoingfar.currencyconverter.utils.RetrofitUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This class is responsible for the actual API calls
 */
public class ApiCaller {

    private Context context;
    private ApiResponseCallback callback;
    private Retrofit retrofit;

    public ApiCaller(Context context, ApiResponseCallback callback) {
        this.context = context;
        this.callback = callback;
        this.retrofit = RetrofitUtil.getRetrofitInstance();
    }

    /**
     * This function makes an API call to fetch Latest Rates for 'symbols'
     *
     * @param symbols is the comma-separated list of currency strings whose latest rate will be pulled
     */
    public void fetchLatestRateFor(String symbols) {
        retrofit.create(RatesEndpoint.class)
                .getLatestRateFor(BuildConfig.FIXER_IO_ACCESS_KEY, symbols)
                .enqueue(new Callback<LatestRateData>() {
                    @Override
                    public void onResponse(@NonNull Call<LatestRateData> call, @NonNull Response<LatestRateData> response) {
                        if (response.isSuccessful())
                            callback.onSuccess(response.body());
                        else
                            callback.onFailure(call.request().body());
                    }

                    @Override
                    public void onFailure(Call<LatestRateData> call, Throwable t) {
                        callback.onFailure(call.request().body());
                    }
                });
    }

    /**
     * This function makes an API call to fetch Historical Rates for 'symbols'
     *
     * @param symbols is the comma-separated list of currency strings whose latest rate will be pulled
     */
    public void getHistoricalRateFor(String symbols, String date) {
        retrofit.create(RatesEndpoint.class)
                .getHistoricalRateDataFor(date, BuildConfig.FIXER_IO_ACCESS_KEY, symbols)
                .enqueue(new Callback<HistoricalRateData>() {
                    @Override
                    public void onResponse(Call<HistoricalRateData> call, Response<HistoricalRateData> response) {
                        if (response.isSuccessful())
                            callback.onSuccess(response.body());
                        else
                            callback.onFailure(call.request().body());
                    }

                    @Override
                    public void onFailure(Call<HistoricalRateData> call, Throwable t) {
                        callback.onFailure(call.request().body());
                    }
                });
    }
}
