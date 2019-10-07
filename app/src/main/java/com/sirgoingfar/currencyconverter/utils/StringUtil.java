package com.sirgoingfar.currencyconverter.utils;

import android.text.TextUtils;

import com.sirgoingfar.currencyconverter.App;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class StringUtil {

    public static String getUTCTimeFrom(Long timeInMillis) {

        if (timeInMillis <= 0)
            return "--:--";

        Calendar calendar = Calendar.getInstance(App.LOCALE);
        calendar.setTimeInMillis(timeInMillis);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        long hr = calendar.get(Calendar.HOUR_OF_DAY);
        long min = calendar.get(Calendar.MINUTE);

        String utcTime = String.format(App.LOCALE, "%02d", hr).concat(":").concat(String.format(App.LOCALE, "%02d", min));

        if (TextUtils.isEmpty(utcTime) || utcTime.length() < 5)
            return "--:--";

        return utcTime;
    }

    public static String getDateStringFor(long timeInMillis) {
        if (timeInMillis <= 0)
            return null;

        Calendar cal = App.getCalendarInstance();
        cal.setTimeInMillis(timeInMillis);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", App.LOCALE);
        return formatter.format(cal.getTime());
    }

    public static String getFormattedDateText(Date date) {
        String text = "";
        if (date == null)
            return text;

        Calendar start = Calendar.getInstance();
        start.setTime(date);

        String dateMonthName = start.getDisplayName(Calendar.MONTH, Calendar.LONG, App.LOCALE);
        text = text.concat(String.valueOf(start.get(Calendar.DAY_OF_MONTH)));
        text = text.concat(" ");
        text = text.concat(dateMonthName.length() > 3 ? dateMonthName.substring(0, 3).concat(".") : dateMonthName);

        return text;
    }

}
