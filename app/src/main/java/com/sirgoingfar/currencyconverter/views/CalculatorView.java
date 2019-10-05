package com.sirgoingfar.currencyconverter.views;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.sirgoingfar.currencyconverter.R;
import com.sirgoingfar.currencyconverter.models.data.Currency;
import com.sirgoingfar.currencyconverter.utils.FontUtils;
import com.sirgoingfar.currencyconverter.utils.JsonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CalculatorView {

    private View parentView;
    private Context context;
    private ActionListener listener;

    private View inputView;
    private View conversionView;
    private View fromView;
    private View toView;
    private View periodSelector;
    private View period30Days;
    private View period90Days;
    private View period30Indicator;
    private View period90Indicator;
    private TextView tvInputCurrency;
    private TextView tvConversionCurrency;
    private TextView tvConversionValue;
    private TextView tvFromCurrencySym;
    private TextView tvToCurrencySym;
    private TextView tvTimestamp;
    private TextView tvSignUpLabel;
    private TextView tvCurrencyLabel;
    private TextView tvCalculatorLabel;
    private TextView period30Label;
    private TextView period90Label;
    private TextView tvEmailNotif;

    private EditText etValueInput;
    private CircleImageView ivFromCurrencySym;
    private CircleImageView ivToCurrencySym;
    private CardView periodSelectorView;
    private CardView btnConvert;

    public CalculatorView(Context context, View parentView, ActionListener listener) {
        this.parentView = parentView;
        this.context = context;
        this.listener = listener;

        init();
    }

    private void init() {

        inputView = parentView.findViewById(R.id.layout_input);
        conversionView = parentView.findViewById(R.id.layout_input_conversion);
        fromView = parentView.findViewById(R.id.ll_from_currency);
        toView = parentView.findViewById(R.id.ll_to_currency);
        periodSelector = parentView.findViewById(R.id.ll_selector);
        periodSelectorView = parentView.findViewById(R.id.cv_container);

        inputView.findViewById(R.id.tv_value_input).setVisibility(View.GONE);
        conversionView.findViewById(R.id.et_value_input).setVisibility(View.GONE);

        period30Days = periodSelector.findViewById(R.id.ll_period_30_days);
        period90Days = periodSelector.findViewById(R.id.ll_period_90_days);

        tvSignUpLabel = parentView.findViewById(R.id.tv_sign_up_label);
        tvCurrencyLabel = parentView.findViewById(R.id.tv_currency_label);
        tvCalculatorLabel = parentView.findViewById(R.id.tv_calculator_label);

        period30Indicator = period30Days.findViewById(R.id.view_indicator_dot);
        period90Indicator = period90Days.findViewById(R.id.view_indicator_dot);
        tvInputCurrency = inputView.findViewById(R.id.tv_currency_sym);
        tvConversionCurrency = conversionView.findViewById(R.id.tv_currency_sym);
        tvConversionValue = conversionView.findViewById(R.id.tv_value_input);
        tvFromCurrencySym = fromView.findViewById(R.id.tv_currency_sym);
        tvToCurrencySym = toView.findViewById(R.id.tv_currency_sym);
        tvTimestamp = parentView.findViewById(R.id.tv_timestamp_warning);
        String text = context.getString(R.string.text_time_stamp);
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        tvTimestamp.setText(content);

        period30Label = period30Days.findViewById(R.id.tv_period_label);
        period90Label = period90Days.findViewById(R.id.tv_period_label);
        tvEmailNotif = parentView.findViewById(R.id.tv_email_notif);
        text = context.getString(R.string.text_email_notif);
        content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        tvEmailNotif.setText(content);

        period30Label.setText(context.getString(R.string.text_past_x_days, 30));
        period90Label.setText(context.getString(R.string.text_past_x_days, 90));

        etValueInput = inputView.findViewById(R.id.et_value_input);
        btnConvert = parentView.findViewById(R.id.btn_convert);
        ivFromCurrencySym = fromView.findViewById(R.id.iv_currency_sym);
        ivToCurrencySym = toView.findViewById(R.id.iv_currency_sym);

        text = context.getString(R.string.text_calculator);
        String targetedText = ".";
        int indexOfTargetedTextInText = text.indexOf(targetedText);
        SpannableString colorSb = new SpannableString(text);
        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(Color.parseColor("#00D998"));
        colorSb.setSpan(foregroundSpan, indexOfTargetedTextInText, indexOfTargetedTextInText + targetedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCalculatorLabel.setText(colorSb);

        applyFontStyle();

        toggleGraphPeriodSelector(true);
    }

    private void applyFontStyle() {
        FontUtils.applyDefaultFont(context, tvSignUpLabel, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(context, tvCurrencyLabel, FontUtils.STYLE_BOLD);
        FontUtils.applyDefaultFont(context, tvCalculatorLabel, FontUtils.STYLE_BOLD);
        FontUtils.applyDefaultFont(context, tvInputCurrency, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(context, tvConversionCurrency, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(context, tvConversionValue, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(context, tvTimestamp, FontUtils.STYLE_MEDIUM);
        FontUtils.applyDefaultFont(context, etValueInput, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(context, tvFromCurrencySym, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(context, tvToCurrencySym, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(context, period30Label, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(context, period90Label, FontUtils.STYLE_SEMIBOLD);
        FontUtils.applyDefaultFont(context, btnConvert, FontUtils.STYLE_REGULAR);
        FontUtils.applyDefaultFont(context, tvEmailNotif, FontUtils.STYLE_MEDIUM);

    }

    public void toggleGraphPeriodSelector(boolean isPeriod30Days) {
        period30Label.setSelected(isPeriod30Days);
        period90Label.setSelected(!isPeriod30Days);
        period30Indicator.setVisibility(isPeriod30Days ? View.VISIBLE : View.GONE);
        period90Indicator.setVisibility(isPeriod30Days ? View.GONE : View.VISIBLE);
    }

    public void setSourceCurrencyFlag(String url) {
        if (TextUtils.isEmpty(url)) return;
        Glide.with(context).load(url).into(ivFromCurrencySym);
    }

    public void setDestCurrencyFlag(String url) {
        if (TextUtils.isEmpty(url)) return;
        Glide.with(context).load(url).into(ivToCurrencySym);
    }

    public void setSourceCurrencyText(String code) {
        if (TextUtils.isEmpty(code))
            return;

        tvInputCurrency.setText(code);
        tvFromCurrencySym.setText(code);
    }

    public void setDestCurrencyText(String code) {
        if (TextUtils.isEmpty(code))
            return;

        tvConversionCurrency.setText(code);
        tvToCurrencySym.setText(code);
    }

    public void changeCurrentFromViews(Currency currencyFrom) {
        setSourceCurrencyText(currencyFrom.getCode());
        setSourceCurrencyFlag(currencyFrom.getFlagUrl());
    }

    public void changeCurrentToViews(Currency currencyTo) {
        setDestCurrencyText(currencyTo.getCode());
        setDestCurrencyFlag(currencyTo.getFlagUrl());
    }

    public interface ActionListener {

    }
}
