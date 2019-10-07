package com.sirgoingfar.currencyconverter.network;

import com.sirgoingfar.currencyconverter.models.data.HistoricalRateData;
import com.sirgoingfar.currencyconverter.models.data.LatestRateData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The interface declares the API endpoints
 */
public interface RatesEndpoint {

    /**
     * The endpoint for fetching Latest Rate data
     *
     * @param accessKey    the API access key
     * @param symbolString the comma-separated list of currency strings whose latest rate will be pulled
     */
    @GET("/latest")
    Call<LatestRateData> getLatestRateFor(
            @Query("access_key") String accessKey,
            @Query("symbols") String symbolString
    );

    /**
     * The endpoint for fetching Historical Rate data
     *
     * @param date         the date string whose historical data is to be pulled
     * @param accessKey    the API access key
     * @param symbolString the comma-separated list of currency strings whose historical rate will be pulled
     */
    @GET("/{dateString}")
    Call<HistoricalRateData> getHistoricalRateDataFor(
            @Path("dateString") String date,
            @Query("access_key") String accessKey,
            @Query("symbols") String symbolString
    );
}
