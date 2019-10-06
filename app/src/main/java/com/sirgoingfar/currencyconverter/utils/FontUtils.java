package com.sirgoingfar.currencyconverter.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sirgoingfar.currencyconverter.customs.CustomTypeFaceSpan;

import java.util.HashMap;
import java.util.Map;

public class FontUtils {

    private static String THIN = "Montserrat-Thin.otf";
    private static String LIGHT = "Montserrat-Light.otf";
    private static String REGULAR = "Montserrat-Regular.otf";
    private static String MEDIUM = "Montserrat-Medium.otf";
    private static String SEMIBOLD = "Montserrat-SemiBold.otf";
    private static String BOLD = "Montserrat-Bold.otf";

    private static Map<String, Typeface> sCachedFonts = new HashMap<>();

    private static Typeface getTypeface(Context context, String assetPath) {
        if (!sCachedFonts.containsKey(assetPath)) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), assetPath);
            sCachedFonts.put(assetPath, tf);
        }

        return sCachedFonts.get(assetPath);
    }

    public static final int STYLE_THIN = 0;
    public static final int STYLE_LIGHT = 1;
    public static final int STYLE_REGULAR = 2;
    public static final int STYLE_MEDIUM = 3;
    public static final int STYLE_SEMIBOLD = 4;
    public static final int STYLE_BOLD = 5;

    public static Typeface selectTypeface(Context context, int textStyle) {
        String prefix = "fonts/";
        String font;
        switch (textStyle) {
            case STYLE_SEMIBOLD:
                font = FontUtils.SEMIBOLD;
                break;
            case STYLE_BOLD:
                font = FontUtils.BOLD;
                break;
            case STYLE_MEDIUM:
                font = FontUtils.MEDIUM;
                break;
            case STYLE_REGULAR:
                font = FontUtils.REGULAR;
                break;
            case STYLE_LIGHT:
                font = FontUtils.LIGHT;
                break;
            default:
                font = FontUtils.THIN;
                break;
        }
        return FontUtils.getTypeface(context, prefix + font);
    }

    public static void applyDefaultFont(Context context, EditText view) {
        applyDefaultFont(context, view, FontUtils.STYLE_REGULAR);
    }

    public static void applyDefaultFont(Context context, EditText view, int style) {
        Typeface typeface = selectTypeface(context, style);
        view.setTypeface(typeface);
    }

    public static void applyDefaultFont(Context context, View[] views, int style) {
        if (views == null) {
            return;
        }

        for (View view : views) {
            applyDefaultFont(context, view, style);
        }
    }

    public static void applyDefaultFont(Context context, Object view, int style) {
        if (view == null)
            return;

        Typeface typeface = selectTypeface(context, style);
        if (view instanceof EditText) {
            ((EditText) view).setTypeface(typeface);
        } else if (view instanceof TextView) {
            ((TextView) view).setTypeface(typeface);
        } else if (view instanceof Dialog) {
            applyDefaultFont(context, ((Dialog) view));
        }
    }

    public static void applyDefaultFont(Context context, Dialog dialog) {
        try {
            if (dialog.isShowing()) {
                Typeface tfRegular = selectTypeface(context, FontUtils.STYLE_REGULAR);
                Typeface tfBold = selectTypeface(context, FontUtils.STYLE_BOLD);
                Typeface tfMedium = selectTypeface(context, FontUtils.STYLE_MEDIUM);

                TextView tv1 = dialog.findViewById(android.R.id.message);
                tv1.setTypeface(tfRegular);

                TextView tv2 = dialog.findViewById(android.R.id.title);
                tv2.setTypeface(tfBold);

                TextView tv3 = dialog.findViewById(android.R.id.button1);
                tv3.setTypeface(tfMedium);

                TextView tv4 = dialog.findViewById(android.R.id.button2);
                tv4.setTypeface(tfMedium);
            }
        } catch (Exception ex) {
        }
    }

    public static SpannableStringBuilder createTypefaceSpan(Context context, String text, int type) {
        Typeface font = FontUtils.selectTypeface(context, type);
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(new CustomTypeFaceSpan("", font), 0, ssb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ssb;
    }

    public static SpannableStringBuilder createTypefaceSpan(Context context, String text) {
        return createTypefaceSpan(context, text, FontUtils.STYLE_REGULAR);
    }

    public static CharSequence getCharSequence(Context context, String s, int type) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }

        SpannableStringBuilder typefaceSpan = createTypefaceSpan(context, s, type);

        return typefaceSpan.subSequence(0, s.length());
    }

    public static CharSequence getCharSequence(Context context, String s) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        return createTypefaceSpan(context, s).subSequence(0, s.length());
    }
}
