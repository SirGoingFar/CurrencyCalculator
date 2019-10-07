package com.sirgoingfar.currencyconverter.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.ikmich.numberformat.NumberFormatterTextWatcher;
import com.ikmich.numberformat.NumberInputFormatter;
import com.sirgoingfar.currencyconverter.App;
import com.sirgoingfar.currencyconverter.R;
import com.sirgoingfar.currencyconverter.customs.TrendChartMarkerView;
import com.sirgoingfar.currencyconverter.models.data.Currency;
import com.sirgoingfar.currencyconverter.utils.DateUtil;
import com.sirgoingfar.currencyconverter.utils.FontUtils;
import com.sirgoingfar.currencyconverter.utils.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

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
    private ImageView ivFromCurrencySym;
    private ImageView ivToCurrencySym;
    private ProgressBar pbLoader;
    private ConstraintLayout periodSelectorView;
    private CardView btnConvert;

    private LineChart trendChart;
    private YAxis yAxis;
    private XAxis xAxis;

    private NumberFormatterTextWatcher.InputListener inputListener;

    public CalculatorView(Context context, View parentView, ActionListener listener, NumberFormatterTextWatcher.InputListener inputListener) {
        this.parentView = parentView;
        this.context = context;
        this.listener = listener;
        this.inputListener = inputListener;

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

        period30Label = period30Days.findViewById(R.id.tv_period_label);
        period90Label = period90Days.findViewById(R.id.tv_period_label);
        tvEmailNotif = parentView.findViewById(R.id.tv_email_notif);
        String text = context.getString(R.string.text_email_notif);
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        tvEmailNotif.setText(content);
        tvEmailNotif.setOnClickListener(v -> Toast.makeText(context, context.getString(R.string.text_email_subscription_success_msg), Toast.LENGTH_SHORT).show());

        updateTimeStamp(context.getString(R.string.text_time_stamp));

        period30Label.setText(context.getString(R.string.text_past_x_days, 30));
        period90Label.setText(context.getString(R.string.text_past_x_days, 90));

        etValueInput = inputView.findViewById(R.id.et_value_input);
        btnConvert = parentView.findViewById(R.id.btn_convert);
        ivFromCurrencySym = fromView.findViewById(R.id.iv_currency_sym);
        ivToCurrencySym = toView.findViewById(R.id.iv_currency_sym);

        pbLoader = parentView.findViewById(R.id.pb_loading);
        trendChart = parentView.findViewById(R.id.lc_currency_trend);

        text = context.getString(R.string.text_calculator);
        String targetedText = ".";
        int indexOfTargetedTextInText = text.indexOf(targetedText);
        SpannableString colorSb = new SpannableString(text);
        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(Color.parseColor("#00D998"));
        colorSb.setSpan(foregroundSpan, indexOfTargetedTextInText, indexOfTargetedTextInText + targetedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCalculatorLabel.setText(colorSb);

        NumberInputFormatter inputFormatter = new NumberInputFormatter.Builder().buildFor(etValueInput);
        inputFormatter.setInputListener(inputListener);
        //Todo: Make he boolean dynamic
        inputFormatter.setup(false);

        //set click listener on action views
        period30Days.setOnClickListener(v -> listener.onPeriodSelectorClicked(true));
        period90Days.setOnClickListener(v -> listener.onPeriodSelectorClicked(false));
        btnConvert.setOnClickListener(v -> listener.onConvertBtnClick());
        btnConvert.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    btnConvert.setAlpha(0.6f);
                    break;

                case MotionEvent.ACTION_UP:
                    btnConvert.setAlpha(1f);
                    break;
            }

            return false;
        });

        fromView.setOnClickListener(v -> onCurrencySelectorClick(true));
        fromView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    fromView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGray98));
                    break;

                case MotionEvent.ACTION_UP:
                    fromView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
                    break;
            }

            return false;
        });

        toView.setOnClickListener(v -> onCurrencySelectorClick(false));
        toView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    toView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGray98));
                    break;

                case MotionEvent.ACTION_UP:
                    toView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
                    break;
            }

            return false;
        });

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

    public void setupTrendChart(int numOdDaysInPeriod, String currencyCode) {
        // // Chart Style // //

        // disable description text
        trendChart.getDescription().setEnabled(false);

        // enable touch gestures
        trendChart.setTouchEnabled(true);

        // set listeners
        trendChart.setOnChartValueSelectedListener(listener);

        // enable scaling and dragging
        trendChart.setDragEnabled(false);
        trendChart.setScaleEnabled(false);

        // force pinch zoom along both axis
        trendChart.setPinchZoom(true);

        //set Marker
        trendChart.setMarker(new TrendChartMarkerView(context, R.layout.layout_trend_chart_marker, currencyCode, numOdDaysInPeriod));

        trendChart.setDrawGridBackground(false);

        trendChart.setNoDataText(FontUtils.createTypefaceSpan(context, context.getString(R.string.text_empty_chart_text)).toString());
        trendChart.setNoDataTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        setupXAxis(numOdDaysInPeriod);
        setupYAxis();
    }

    private void setupXAxis(int numOfDaysInPeriod) {
        // // X-Axis Style // //
        xAxis = trendChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTypeface(FontUtils.selectTypeface(context, FontUtils.STYLE_LIGHT));
        xAxis.setLabelCount(5, true);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.parseColor("#A6DFF4"));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return StringUtil.getChartXAxisLabel(value, numOfDaysInPeriod);
            }
        });

    }

    private void setupYAxis() {
        // // Y-Axis Style // //
        yAxis = trendChart.getAxisLeft();

        trendChart.getAxisRight().setEnabled(false);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);
        yAxis.setAxisMinimum(0f);
        yAxis.setTypeface(FontUtils.selectTypeface(context, FontUtils.STYLE_REGULAR));
    }

    public void bindTrendData(Map<Integer, Float> data) {

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        trendChart.clear();

        ArrayList<Entry> entryList = processDataMap(data);

        LineDataSet netWorthYouDataSet = makeDataSet(entryList, "LABEL_YOU",
                ContextCompat.getColor(context, R.color.colorPrimaryDark),
                ContextCompat.getDrawable(context, R.drawable.trend_fade));

        dataSets.add(netWorthYouDataSet);

        renderDataSets(dataSets);
    }

    private ArrayList<Entry> processDataMap(Map<Integer, Float> data) {
        ArrayList<Entry> entryList = new ArrayList<>();

        if (data == null || data.isEmpty())
            return entryList;

        for (Map.Entry<Integer, Float> entry : data.entrySet())
            entryList.add(new Entry(entry.getKey(), entry.getValue()));

        return entryList;
    }

    private void renderDataSets(ArrayList<ILineDataSet> dataSets) {
        if (dataSets == null || dataSets.isEmpty()) {
            return;
        }

        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        // set data
        trendChart.setData(data);

        // draw points over time
        // trendChart.animateX(1000);

        // get the legend (only possible after setting data)
        Legend l = trendChart.getLegend();
        l.setForm(Legend.LegendForm.NONE); // Don't show any legend.
        l.setEnabled(false);
    }

    private LineDataSet makeDataSet(ArrayList<Entry> values, String tag, int lineColor, Drawable fillDrawable) {

        LineDataSet set;

        LineData chartData = trendChart.getData();
        if (chartData != null && chartData.getDataSetCount() > 0) {
            set = (LineDataSet) chartData.getDataSetByLabel(tag, true);
            set.setValues(values);
            set.notifyDataSetChanged();
            chartData.notifyDataChanged();
            trendChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set = new LineDataSet(values, tag);

            set.setDrawIcons(false);

            // line thickness and point size
            set.setLineWidth(2f);

            // draw points as solid circles
            set.setDrawCircleHole(false);
            set.setDrawCircles(false); // No circles

            set.setDrawValues(false);

            // draw selection line as dashed
            set.enableDashedHighlightLine(10f, 5f, 0f);

            set.setFillFormatter((dataSet, dataProvider) -> trendChart.getAxisLeft().getAxisMinimum());
        }

        set.setColor(lineColor);

        if (fillDrawable == null) {
            set.setDrawFilled(false);
            set.setFillDrawable(null);
        } else {
            set.setDrawFilled(true);
            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                set.setFillDrawable(fillDrawable);
            } else {
                set.setFillColor(Color.TRANSPARENT);
            }
        }

        return set;
    }

    private void onCurrencySelectorClick(boolean isSourceCurrency) {
        listener.onCurrencySelectorClick(isSourceCurrency);
    }

    public void toggleLoader(boolean show) {
        pbLoader.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void toggleGraphPeriodSelector(boolean isPeriod30Days) {
        period30Label.setSelected(isPeriod30Days);
        period90Label.setSelected(!isPeriod30Days);
        period30Indicator.setVisibility(isPeriod30Days ? View.VISIBLE : View.GONE);
        period90Indicator.setVisibility(isPeriod30Days ? View.GONE : View.VISIBLE);
    }

    public void updateTimeStamp(String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        tvTimestamp.setText(content);
    }

    public void setSourceCurrencyFlag(String url) {
        if (TextUtils.isEmpty(url)) return;

        Glide.with(context)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivFromCurrencySym);
    }

    public void setDestCurrencyFlag(String url) {
        if (TextUtils.isEmpty(url)) return;

        Glide.with(context)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivToCurrencySym);

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

    public void changeCurrentFromViews(String code, String flagUrl) {
        setSourceCurrencyText(code);
        setSourceCurrencyFlag(flagUrl);
    }

    public void changeCurrentToViews(String code, String flagUrl) {
        setDestCurrencyText(code);
        setDestCurrencyFlag(flagUrl);
    }

    public void setDestCurrencyValue(String text) {
        if (TextUtils.isEmpty(text)) {
            tvConversionValue.setText("");
            return;
        }

        tvConversionValue.setText(text);
    }

    public String getInputValue() {

        Editable editable = etValueInput.getText();

        if (editable == null)
            return null;

        return editable.toString();
    }

    public interface ActionListener extends OnChartValueSelectedListener {

        void onCurrencySelectorClick(boolean isSourceCurrency);

        void onPeriodSelectorClicked(boolean isPeriod30);

        void onConvertBtnClick();

        @Override
        void onValueSelected(Entry e, Highlight h);

        @Override
        void onNothingSelected();
    }
}
