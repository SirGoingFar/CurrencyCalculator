package com.sirgoingfar.currencyconverter.database.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "latest_rate")
public class LatestRateEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String code;

    private double rate;

    private long time;

    @Ignore
    public LatestRateEntity(String code, double rate, long time) {
        this.code = code;
        this.rate = rate;
        this.time = time;
    }

    public LatestRateEntity(int id, String code, double rate, long time) {
        this.id = id;
        this.code = code;
        this.rate = rate;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public double getRate() {
        return rate;
    }

    public long getTime() {
        return time;
    }
}
