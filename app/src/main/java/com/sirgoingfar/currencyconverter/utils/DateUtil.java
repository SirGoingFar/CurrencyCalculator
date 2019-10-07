package com.sirgoingfar.currencyconverter.utils;

import com.sirgoingfar.currencyconverter.App;

import java.util.Calendar;
import java.util.TimeZone;

public class DateUtil {

    public static final int CONST_30 = 30;
    public static final int CONST_90 = 90;
    public static final long A_DAY_MILLIS = (24 * 60 * 60 * 1000) - 1000; //minus 1second - cross over time
    public static final long THIRTY_DAYS = CONST_30 * A_DAY_MILLIS;
    public static final long NINTY_DAYS = CONST_90 * A_DAY_MILLIS;

    /**
     *
     * @return the earliest time (in milliseconds) of 30 days ago
     *
     * */
    public static long getThirtyDaysAgoEarliestTime() {
        Calendar cal = App.getCalendarInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        long timeInMillis = cal.getTimeInMillis();
        return toUnixEpoch(timeInMillis - THIRTY_DAYS);
    }

    /**
     *
     * @return the earliest time (in milliseconds) of 90 days ago
     *
     * */
    public static long getNintyDaysAgoEarliestTime() {
        Calendar cal = App.getCalendarInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        long timeInMillis = cal.getTimeInMillis();
        return toUnixEpoch(timeInMillis - NINTY_DAYS);
    }

    /**
     *
     * This function checks if 'time' value lies in two days ago
     *
     * @return flag
     *
     * */
    public static boolean isTimeTwoDaysAgoOrMore(long time) {

        Calendar cal = App.getCalendarInstance();
        cal.setTimeInMillis(time);
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

    /**
     *
     * @return the latest time (in milliseconds) of 90 days ago
     *
     * */
    public static long get90DaysAgoLatestTimeInMillis() {
        long ystTimeMillis = getYstDayLatestTimeInMillis();
        return (ystTimeMillis - (89 * A_DAY_MILLIS));
    }

    /**
     *
     * @return the earliest time (in milliseconds) of yesterday
     *
     * */
    public static long getYstDayLatestTimeInMillis() {
        Calendar cal = App.getCalendarInstance();
        cal.set(Calendar.DAY_OF_YEAR, (cal.get(Calendar.DAY_OF_YEAR) - 1));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTimeInMillis();
    }

    /**
     *
     * @return the earliest time (in milliseconds) of yesterday
     *
     * */
    public static long getYstDayEarliestTimeInMillis() {
        Calendar cal = App.getCalendarInstance();
        cal.set(Calendar.DAY_OF_YEAR, (cal.get(Calendar.DAY_OF_YEAR) - 1));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 1);
        return cal.getTimeInMillis();
    }

    /**
     *
     * @param timeMillis the time in milliseconds
     *
     * @return the Unix Epoch second of 'timeMillis'
     *
     * */
    public static long toUnixEpoch(long timeMillis) {
        return timeMillis / 1000;
    }

    /**
     *
     * @param epochTime the time in milliseconds
     *
     * @return the milliSeconds of 'epochTime'
     *
     * */
    public static long toMillis(long epochTime) {
        return epochTime * 1000;
    }
}
