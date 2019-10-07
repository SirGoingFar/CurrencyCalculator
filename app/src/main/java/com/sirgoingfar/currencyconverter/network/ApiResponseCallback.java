package com.sirgoingfar.currencyconverter.network;

/**
 * The interface functions are invoked on API response
 */
public interface ApiResponseCallback {

    /**
     * This function is invoked when there is a successful API call
     *
     * @param response is the API success data
     */
    <T> void onSuccess(T response);


    /**
     * This function is invoked when there is a failed API call
     *
     * @param response is the API error data
     */
    <T> void onFailure(T response);

}
