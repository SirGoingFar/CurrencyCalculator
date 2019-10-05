package com.sirgoingfar.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sirgoingfar.currencyconverter.utils.FontUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class CalculatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        View inputView = findViewById(R.id.layout_input);
        inputView.findViewById(R.id.tv_value_input).setVisibility(View.GONE);
        View conversionView = findViewById(R.id.layout_input_conversion);
        conversionView.findViewById(R.id.et_value_input).setVisibility(View.GONE);
        View fromView = findViewById(R.id.ll_from_currency);
        View toView = findViewById(R.id.ll_to_currency);
        CardView periodSelectorView = findViewById(R.id.cv_container);
        View periodSelector = periodSelectorView.findViewById(R.id.ll_selector);
        View period30Days = periodSelector.findViewById(R.id.ll_period_30_days);
        View period90Days = periodSelector.findViewById(R.id.ll_period_90_days);

        TextView tv1 = findViewById(R.id.tv_sign_up_label);
        TextView tv2 = findViewById(R.id.tv_currency_label);
        TextView tv3 = findViewById(R.id.tv_calculator_label);
        TextView tvInputCurrency = inputView.findViewById(R.id.tv_currency_sym);
        TextView tvConversionCurrency = conversionView.findViewById(R.id.tv_currency_sym);
        TextView tvConversionValue = conversionView.findViewById(R.id.tv_value_input);
        TextView tvFromCurrencySym = fromView.findViewById(R.id.tv_currency_sym);
        TextView tvToCurrencySym = toView.findViewById(R.id.tv_currency_sym);
        TextView tvTimestamp = findViewById(R.id.tv_timestamp_warning);
        String text = getString(R.string.text_time_stamp);
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        tvTimestamp.setText(content);
        TextView period30Label = period30Days.findViewById(R.id.tv_period_label);
        TextView period90Label = period90Days.findViewById(R.id.tv_period_label);
        TextView tvEmailNotif = findViewById(R.id.tv_email_notif);
        text = getString(R.string.text_email_notif);
        content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        tvEmailNotif.setText(content);
        period30Label.setText(getString(R.string.text_past_x_days, 30));
        period90Label.setText(getString(R.string.text_past_x_days, 90));
        period30Label.setSelected(true);
        period90Label.setSelected(false);
        View period30Indicator = period30Days.findViewById(R.id.view_indicator_dot);
        View period90Indicator = period90Days.findViewById(R.id.view_indicator_dot);
        period90Indicator.setVisibility(View.GONE);
        EditText etValueInput = inputView.findViewById(R.id.et_value_input);
        CardView btnConvert = findViewById(R.id.btn_convert);
        CircleImageView ivFromCurrencySym = fromView.findViewById(R.id.iv_currency_sym);
        CircleImageView ivToCurrencySym = toView.findViewById(R.id.iv_currency_sym);

        FontUtils.applyDefaultFont(this, tv1, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(this, tv2, FontUtils.STYLE_BOLD);
        FontUtils.applyDefaultFont(this, tv3, FontUtils.STYLE_BOLD);
        FontUtils.applyDefaultFont(this, tvInputCurrency, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(this, tvConversionCurrency, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(this, tvConversionValue, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(this, tvTimestamp, FontUtils.STYLE_MEDIUM);
        FontUtils.applyDefaultFont(this, etValueInput, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(this, tvFromCurrencySym, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(this, tvToCurrencySym, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(this, period30Label, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(this, period90Label, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(this, btnConvert, FontUtils.STYLE_REGULAR);
        FontUtils.applyDefaultFont(this, tvEmailNotif, FontUtils.STYLE_MEDIUM);

        Glide.with(this)
                .load("https://api.backendless.com/2F26DFBF-433C-51CC-FF56-830CEA93BF00/473FB5A9-D20E-8D3E-FF01-E93D9D780A00/files/CountryFlags/afg.svg")
                .into(ivFromCurrencySym);

        Glide.with(this)
                .load("https://api.backendless.com/2F26DFBF-433C-51CC-FF56-830CEA93BF00/473FB5A9-D20E-8D3E-FF01-E93D9D780A00/files/CountryFlags/dza.svg")
                .into(ivToCurrencySym);

        text = getString(R.string.text_calculator);
        String targetedText = ".";
        int indexOfTargetedTextInText = text.indexOf(targetedText);
        SpannableString colorSb = new SpannableString(text);
        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(Color.parseColor("#00D998"));
        colorSb.setSpan(foregroundSpan, indexOfTargetedTextInText, indexOfTargetedTextInText + targetedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv3.setText(colorSb);
    }
}
