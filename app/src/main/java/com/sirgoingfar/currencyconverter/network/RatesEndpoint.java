package com.sirgoingfar.currencyconverter.network;

import com.sirgoingfar.currencyconverter.models.data.LatestRateData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RatesEndpoint {

    @GET("/latest")
    Call<LatestRateData> getLatestRateFor(
            @Query("access_key") String accessKey,
            @Query("symbols") String symbolString
    );

}
