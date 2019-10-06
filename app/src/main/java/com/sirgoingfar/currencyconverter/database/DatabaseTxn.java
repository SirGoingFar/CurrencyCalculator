package com.sirgoingfar.currencyconverter.database;

import com.sirgoingfar.currencyconverter.App;
import com.sirgoingfar.currencyconverter.database.daos.AppDao;
import com.sirgoingfar.currencyconverter.database.entities.LatestRateEntity;

import java.util.List;

public class DatabaseTxn {

    private AppDao dao;

    public DatabaseTxn() {
        dao = App.getAppDao();
    }

    public void deleteLatestRates() {
        dao.deleteLatestRates();
    }

    public void addLatestRates(List<LatestRateEntity> data) {
        if (data == null || data.isEmpty())
            return;

        dao.bulkInsertTodo(data);
    }
}
