package com.sirgoingfar.currencyconverter.utils;

import androidx.annotation.NonNull;

import com.sirgoingfar.currencyconverter.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    private final static String BASE_URL = "http://data.fixer.io/api/";
    private static Retrofit sRetrofit;

    public static Retrofit getRetrofitInstance() {

        //Create OkHttpClient Builder and HttpLoggingInterceptor
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        //Add logger to client if it's DEBUG
        if(BuildConfig.DEBUG)
            clientBuilder.addInterceptor(logger);

        if (sRetrofit == null)
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(clientBuilder.build())
                    .build();

        return sRetrofit;
    }
}
