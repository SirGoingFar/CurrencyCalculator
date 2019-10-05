package com.sirgoingfar.currencyconverter.utils;

import android.content.Context;

import com.sirgoingfar.currencyconverter.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.regex.Pattern;

public class JsonUtil {

    public static String getCurrencyDataString(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.country_props);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        try {

            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int n;

            while ((n = reader.read(buffer)) != -1)
                writer.write(buffer, 0, n);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer.toString();
    }

    public static String sanitizeJsonString(String json){
        json = json.replace(Pattern.quote("\r"), "");
        json = json.replace(Pattern.quote("\n"), "");
        json = json.replace(Pattern.quote("\\"), "");
        return json;
    }
}
