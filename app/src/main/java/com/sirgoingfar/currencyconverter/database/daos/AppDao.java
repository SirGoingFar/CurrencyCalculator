package com.sirgoingfar.currencyconverter.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sirgoingfar.currencyconverter.database.entities.HistoricalRateEntity;
import com.sirgoingfar.currencyconverter.database.entities.LatestRateEntity;

import java.util.List;

/**
 *
 * The interface consists of functions that performs the CRUD operations on the database
 *
 * */
@Dao
public interface AppDao {

    //Latest Rate
    @Query("SELECT * FROM latest_rate")
    LiveData<List<LatestRateEntity>> getLatestRates();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsertLatestRate(List<LatestRateEntity> list);

    @Query("DELETE FROM latest_rate")
    void deleteLatestRates();

    //Historical Rate
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsertHistoricalData(List<HistoricalRateEntity> list);

    @Query("DELETE FROM historical_rate")
    void deleteHistoricalRates();

    @Query("SELECT * FROM historical_rate WHERE (code = :code AND time >= :minTime) ORDER BY time ASC")
    LiveData<List<HistoricalRateEntity>> getHistoricalRates(String code, long minTime);
}
