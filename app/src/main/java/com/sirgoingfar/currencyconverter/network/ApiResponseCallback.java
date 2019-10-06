package com.sirgoingfar.currencyconverter.network;

public interface ApiResponseCallback {

    <T> void onSuccess(T response);

    <T> void onFailure(T response);

}
