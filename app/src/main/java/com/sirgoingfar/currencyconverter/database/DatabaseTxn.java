package com.sirgoingfar.currencyconverter.database;

import com.sirgoingfar.currencyconverter.App;
import com.sirgoingfar.currencyconverter.database.daos.AppDao;
import com.sirgoingfar.currencyconverter.database.entities.HistoricalRateEntity;
import com.sirgoingfar.currencyconverter.database.entities.LatestRateEntity;
import com.sirgoingfar.currencyconverter.utils.AppExecutors;

import java.util.List;

/**
 * The class is responsible for the database transactions.
 */
public class DatabaseTxn {

    private AppDao dao;
    private AppExecutors executors;

    /**
     * @constructor
     */
    public DatabaseTxn() {
        dao = App.getAppDao();
        executors = App.getExecutors();
    }

    public void deleteLatestRates() {
        dao.deleteLatestRates();
    }

    public void addLatestRates(List<LatestRateEntity> data) {
        executors.diskIO().execute(() -> {
            if (data == null || data.isEmpty())
                return;

            deleteLatestRates();
            dao.bulkInsertLatestRate(data);
        });
    }

    public void addHistoricalRates(List<HistoricalRateEntity> data) {
        executors.diskIO().execute(() -> {
            if (data == null || data.isEmpty())
                return;

            dao.bulkInsertHistoricalData(data);
        });
    }

    public void deleteHistoricalRates() {
        executors.diskIO().execute(() -> dao.deleteHistoricalRates());
    }
}
