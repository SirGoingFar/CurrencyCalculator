package com.sirgoingfar.currencyconverter.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.ikmich.numberformat.NumberFormatterTextWatcher;
import com.sirgoingfar.currencyconverter.App;
import com.sirgoingfar.currencyconverter.R;
import com.sirgoingfar.currencyconverter.database.entities.LatestRateEntity;
import com.sirgoingfar.currencyconverter.dialog_fragments.CurrencyPickerDialogFragment;
import com.sirgoingfar.currencyconverter.models.CalculatorViewModel;
import com.sirgoingfar.currencyconverter.models.data.Currency;
import com.sirgoingfar.currencyconverter.models.data.Option;
import com.sirgoingfar.currencyconverter.utils.NumberFormatUtil;
import com.sirgoingfar.currencyconverter.utils.StringUtil;
import com.sirgoingfar.currencyconverter.views.CalculatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CalculatorActivity extends AppCompatActivity implements CalculatorView.ActionListener, NumberFormatterTextWatcher.InputListener, CurrencyPickerDialogFragment.SingleChoiceListener {

    private CalculatorView viewHolder;
    private CalculatorViewModel model;
    private EventBus eventBus;

    private List<Currency> allCurrencyList = new ArrayList<>();
    private List<LatestRateEntity> latestRateEntities = new ArrayList<>();

    private Currency currencyFrom;
    private Currency currencyTo;

    private BigDecimal inputValue = new BigDecimal(0);

    private boolean isSourceCurrency;
    private int currencyIndex;

    private CurrencyPickerDialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        eventBus = App.getEventBusInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //register EventBus
        eventBus.register(this);

        //initialize components
        viewHolder = new CalculatorView(this, getWindow().getDecorView().findViewById(android.R.id.content), this, this);
        model = new CalculatorViewModel(App.getInstance());

        //observe data changes
        model.getCurrencyListObserver().observe(this, currencies -> {
            if (currencies == null || currencies.isEmpty())
                return;

            allCurrencyList = currencies;
            setupScreen();
        });

        model.getLatestRateLiveData().observe(this, latestRateEntities -> {
            if (latestRateEntities == null || latestRateEntities.isEmpty())
                return;

            CalculatorActivity.this.latestRateEntities = latestRateEntities;
            computeConversionValue();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        //unregister EventBus
        eventBus.unregister(this);
    }

    private void setupScreen() {
        if (allCurrencyList.size() < 2) {
            toastMsg(getString(R.string.text_something_went_wrong));
            finish();
        }

        //assign initial currency pair
        currencyFrom = allCurrencyList.get(0);
        currencyTo = allCurrencyList.get(1);

        //assign the pair
        viewHolder.changeCurrentFromViews(currencyFrom);
        viewHolder.changeCurrentToViews(currencyTo);

        //fetch latest rate data
        fetchLatestRates();

        //Todo
        viewHolder.setupTrendChart();
    }

    private void fetchLatestRates() {
        model.fetchLatestRate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pending(String text) {
    }

    private void toastMsg(String msg) {
        if (TextUtils.isEmpty(msg))
            return;

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void scheduleLatestRatePoll() {

    }

    @Override
    public void onChange(String unformatted, String formatted) {
        if (TextUtils.isEmpty(unformatted))
            updateDestCurrencyValue("");

        inputValue = NumberFormatUtil.parseAmount(unformatted);
    }

    @Override
    public void onCurrencySelectorClick(boolean isSourceCurrency) {
        this.isSourceCurrency = isSourceCurrency;

        List<Option> optionData = getOtherCurrenciesAside(isSourceCurrency ? currencyTo.getCode() : currencyFrom.getCode(),
                isSourceCurrency ? currencyFrom.getName() : currencyTo.getName());

        if (optionData.isEmpty())
            return;

        dialog = CurrencyPickerDialogFragment.newInstance(
                this, optionData, this, getString(R.string.text_select_a_currency), currencyIndex
        );

        dialog.show(getSupportFragmentManager(), CurrencyPickerDialogFragment.class.getName());
    }

    private List<Option> getOtherCurrenciesAside(String excludeCode, String countryName) {
        List<Option> list = new ArrayList<>();

        for (Currency currency : allCurrencyList) {
            if (!TextUtils.equals(excludeCode, currency.getCode())) {
                String desc = currency.getName().concat(" (").concat(currency.getCode()).concat(")");
                Option option = new Option(desc, currency.getName(), currency.getFlagUrl());
                list.add(option);

                if (TextUtils.equals(countryName, currency.getName())) {
                    currencyIndex = list.indexOf(option);
                    option.setSelected(true);
                }
            }
        }

        return list;
    }

    private Currency getCurrencyByName(String name) {
        for (Currency currency : allCurrencyList) {
            if (TextUtils.equals(currency.getName(), name))
                return currency;
        }

        return null;
    }

    @Override
    public void onPeriodSelectorClicked(boolean isPeriod30) {
        viewHolder.toggleGraphPeriodSelector(isPeriod30);
    }

    @Override
    public void onConvertBtnClick() {
        viewHolder.toggleLoader(true);
        computeConversionValue();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private void computeConversionValue() {
        if (isLatestRateAvailable()) {

            updateTimestamp();

            if (TextUtils.isEmpty(viewHolder.getInputValue())) {
                updateDestCurrencyValue("");
                return;
            }

            LatestRateEntity sourceCurrency = getEntityForCurrencyWithCode(currencyFrom.getCode());
            LatestRateEntity destCurrency = getEntityForCurrencyWithCode(currencyTo.getCode());

            BigDecimal conversionValue = (inputValue.divide(new BigDecimal(sourceCurrency.getRate()), RoundingMode.HALF_EVEN)
                    .multiply(new BigDecimal(destCurrency.getRate())));

            updateDestCurrencyConversionValue(conversionValue);
        } else {
            fetchLatestRates();
        }
    }

    private void updateTimestamp() {
        String time = StringUtil.getUTCTimeFrom(model.getLastRateFetchTime());
        viewHolder.updateTimeStamp(getString(R.string.text_time_stamp, time));
    }

    private boolean isLatestRateAvailable() {
        return latestRateEntities != null && !latestRateEntities.isEmpty();
    }

    private LatestRateEntity getEntityForCurrencyWithCode(String currencyCode) {
        for (LatestRateEntity entity : latestRateEntities) {
            if (TextUtils.equals(entity.getCode(), currencyCode))
                return entity;
        }

        return null;
    }

    private void updateDestCurrencyConversionValue(BigDecimal value) {
        String text = NumberFormatUtil.format(value);
        updateDestCurrencyValue(text);
    }

    private void updateDestCurrencyValue(String text) {
        viewHolder.setDestCurrencyValue(text);
        viewHolder.toggleLoader(false);
    }

    private void updateScreen() {

        if (isSourceCurrency)
            viewHolder.changeCurrentFromViews(currencyFrom);
        else
            viewHolder.changeCurrentToViews(currencyTo);

        computeConversionValue();
    }

    @Override
    public void onCurrencyOptionSelected(Option option, int position, boolean isOptionSelected) {
        Currency selectedCurrency = getCurrencyByName(option.getName());

        if (isSourceCurrency)
            currencyFrom = selectedCurrency;
        else
            currencyTo = selectedCurrency;

        updateScreen();
    }

}
