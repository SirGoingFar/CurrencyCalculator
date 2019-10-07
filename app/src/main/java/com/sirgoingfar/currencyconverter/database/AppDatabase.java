package com.sirgoingfar.currencyconverter.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sirgoingfar.currencyconverter.database.daos.AppDao;
import com.sirgoingfar.currencyconverter.database.entities.HistoricalRateEntity;
import com.sirgoingfar.currencyconverter.database.entities.LatestRateEntity;

/**
 * The class instantiates and gives access to the database CRUD functions .
 */
@Database(entities = {LatestRateEntity.class, HistoricalRateEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "currency_calculator_db";
    private static AppDatabase sInstance;

    /**
     * This function creates (if not already created) the database and returns a single instance of the database
     *
     * @param context the context of the caller
     * @return the single instance of the database object
     */
    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }

        return sInstance;
    }

    /**
     * @return an instance of the AppDao for database operations
     */
    public abstract AppDao getDao();
}
