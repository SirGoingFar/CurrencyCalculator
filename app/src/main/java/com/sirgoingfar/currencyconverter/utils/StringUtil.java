package com.sirgoingfar.currencyconverter.utils;

import android.text.TextUtils;

import java.util.Calendar;
import java.util.Locale;

public class StringUtil {

    public static String getUTCTimeFrom(Long timeInMillis) {

        if (timeInMillis <= 0)
            return "--:--";

        Locale locale = Locale.getDefault();
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTimeInMillis(timeInMillis);

        long hr = calendar.get(Calendar.HOUR_OF_DAY);
        long min = calendar.get(Calendar.MINUTE);

        String utcTime = String.format(locale, "%02d", hr).concat(":").concat(String.format(locale, "%02d", min));

        if (TextUtils.isEmpty(utcTime) || utcTime.length() < 5)
            return "--:--";

        return utcTime;
    }

}
