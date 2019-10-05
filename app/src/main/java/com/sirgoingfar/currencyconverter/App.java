package com.sirgoingfar.currencyconverter;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import org.greenrobot.eventbus.EventBus;

public class App extends MultiDexApplication {

    private static Application sApplication;
    private static EventBus sEventBus;

    @Override
    public void onCreate() {
        super.onCreate();

        //instantiate app-wide variables
        sApplication = this;
        sEventBus = EventBus.builder()
                .sendNoSubscriberEvent(true)
                .build();
    }

    public static Application getInstance() {
        return sApplication;
    }

    public static EventBus getEventBusInstance(){
        return sEventBus;
    }

}
