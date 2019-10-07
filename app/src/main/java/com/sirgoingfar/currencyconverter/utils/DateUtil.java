package com.sirgoingfar.currencyconverter.utils;

import com.sirgoingfar.currencyconverter.App;

import java.util.Calendar;

public class DateUtil {

    public static final long A_DAY_MILLIS = (24 * 60 * 60 * 1000) - 1000; //minus 1second - cross over time
    public static final long THIRTY_DAYS_AGO = System.currentTimeMillis() - (30 * A_DAY_MILLIS);
    public static final long NINETY_DAYS_AGO = System.currentTimeMillis() - (90 * A_DAY_MILLIS);

    public static boolean isTimeTwoDaysAgoOrMore(long lastPollTime) {

        Calendar cal = App.getCalendarInstance();
        cal.setTimeInMillis(lastPollTime);
        Calendar now = Calendar.getInstance();

        long timeDay = cal.get(Calendar.DAY_OF_YEAR);
        long timeMonth = cal.get(Calendar.MONTH);
        long timeYear = cal.get(Calendar.YEAR);

        long nowDay = now.get(Calendar.DAY_OF_YEAR);
        long nowMonth = now.get(Calendar.MONTH);
        long nowYear = now.get(Calendar.YEAR);

        if (nowYear > timeYear)
            return true;

        if (nowYear >= timeYear && nowMonth > timeMonth)
            return true;

        return nowDay - 1 > timeDay;
    }

    public static long get90DaysAgoLatestTimeInMillis() {
        long ystTimeMillis = getYstDayLatestTimeInMillis();
        return (ystTimeMillis - (89 * A_DAY_MILLIS));
    }

    public static long getYstDayLatestTimeInMillis() {
        Calendar cal = App.getCalendarInstance();
        cal.set(Calendar.DAY_OF_YEAR, (cal.get(Calendar.DAY_OF_YEAR) - 1));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTimeInMillis();
    }

    public static long getYstDayEarliestTimeInMillis() {
        Calendar cal = App.getCalendarInstance();
        cal.set(Calendar.DAY_OF_YEAR, (cal.get(Calendar.DAY_OF_YEAR) - 1));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 1);
        return cal.getTimeInMillis();
    }
}