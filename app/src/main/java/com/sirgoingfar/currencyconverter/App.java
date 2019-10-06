package com.sirgoingfar.currencyconverter;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.sirgoingfar.currencyconverter.database.AppDatabase;
import com.sirgoingfar.currencyconverter.database.daos.AppDao;
import com.sirgoingfar.currencyconverter.utils.AppExecutors;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Locale;

public class App extends MultiDexApplication {

    private static Application sApplication;
    private static EventBus sEventBus;
    private static AppDatabase sAppDatabase;
    private static AppExecutors sExecutors;

    @SuppressLint("ConstantLocale")
    public static final Locale LOCALE = Locale.getDefault();

    @Override
    public void onCreate() {
        super.onCreate();

        //instantiate app-wide variables
        sApplication = this;
        sEventBus = EventBus.builder()
                .sendNoSubscriberEvent(true)
                .build();
        sAppDatabase = AppDatabase.getInstance(this);
        sExecutors = AppExecutors.getInstance();
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

    public static AppExecutors getExecutors() {
        return sExecutors;
    }

    public static Calendar getCalendarInstance() {
        return Calendar.getInstance(LOCALE);
    }
}
