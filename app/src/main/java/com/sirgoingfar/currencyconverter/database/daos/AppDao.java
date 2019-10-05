package com.sirgoingfar.currencyconverter.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.sirgoingfar.currencyconverter.database.entities.LatestRateEntity;

import java.util.List;

@Dao
public interface AppDao {

    //Latest Rate
    @Query("SELECT * FROM latest_rate")
    LiveData<List<LatestRateEntity>> getLatestRates();

    @Insert
    void bulkInsertTodo(List<LatestRateEntity> list);

    @Query("DELETE FROM latest_rate")
    void deleteLatestRates();

    //Historical Rate
}
