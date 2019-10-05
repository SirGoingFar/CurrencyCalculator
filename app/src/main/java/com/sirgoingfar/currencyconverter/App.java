package com.sirgoingfar.currencyconverter;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.sirgoingfar.currencyconverter.database.AppDatabase;
import com.sirgoingfar.currencyconverter.database.daos.AppDao;

import org.greenrobot.eventbus.EventBus;

public class App extends MultiDexApplication {

    private static Application sApplication;
    private static EventBus sEventBus;
    private static AppDatabase sAppDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        //instantiate app-wide variables
        sApplication = this;
        sEventBus = EventBus.builder()
                .sendNoSubscriberEvent(true)
                .build();
        sAppDatabase = AppDatabase.getInstance(this);
    }

    public static Application getInstance() {
        return sApplication;
    }

    public static EventBus getEventBusInstance() {
        return sEventBus;
    }

    public static AppDatabase getAppDatabase() {
        return sAppDatabase;
    }

    public static AppDao getAppDao() {
        return sAppDatabase.getDao();
    }

}
