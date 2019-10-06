package com.sirgoingfar.currencyconverter.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class NumberFormatUtil {

    public static DecimalFormat sDecimalFormat;

    private static DecimalFormat getDecimalFormat(){

        if(sDecimalFormat == null){
            sDecimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
            sDecimalFormat.setParseBigDecimal(true);
            sDecimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        }

        return sDecimalFormat;
    }

    public static BigDecimal parseAmount(String valueString){

        BigDecimal bd = new BigDecimal(0);

        if(TextUtils.isEmpty(valueString)){
            return bd;
        }

        try {
            bd = (BigDecimal) getDecimalFormat().parse(valueString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return bd;

    }

    public static String format(BigDecimal value) {
        return getDecimalFormat().format(value.doubleValue());
    }
}
