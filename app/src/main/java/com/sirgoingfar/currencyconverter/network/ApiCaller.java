package com.sirgoingfar.currencyconverter.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sirgoingfar.currencyconverter.BuildConfig;
import com.sirgoingfar.currencyconverter.models.data.LatestRateData;
import com.sirgoingfar.currencyconverter.utils.RetrofitUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiCaller {

    private Context context;
    private ApiResponseCallback callback;
    private Retrofit retrofit;

    public ApiCaller(Context context, ApiResponseCallback callback) {
        this.context = context;
        this.callback = callback;
        this.retrofit = RetrofitUtil.getRetrofitInstance();
    }

    public void fetchLatestRate(String symbols) {
        retrofit.create(RatesEndpoint.class)
                .getLatestRateFor(BuildConfig.FIXER_IO_ACCESS_KEY, symbols)
                .enqueue(new Callback<LatestRateData>() {
                    @Override
                    public void onResponse(@NonNull Call<LatestRateData> call, @NonNull Response<LatestRateData> response) {
                        if (response.isSuccessful())
                            callback.onSuccess(response.body());
                        else
                            callback.onFailure(response.errorBody());
                    }

                    @Override
                    public void onFailure(Call<LatestRateData> call, Throwable t) {

                    }
                });
    }
}
