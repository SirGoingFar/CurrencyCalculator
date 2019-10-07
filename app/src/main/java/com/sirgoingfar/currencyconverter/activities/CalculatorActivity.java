package com.sirgoingfar.currencyconverter.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.ikmich.numberformat.NumberFormatterTextWatcher;
import com.sirgoingfar.currencyconverter.App;
import com.sirgoingfar.currencyconverter.R;
import com.sirgoingfar.currencyconverter.database.entities.HistoricalRateEntity;
import com.sirgoingfar.currencyconverter.database.entities.LatestRateEntity;
import com.sirgoingfar.currencyconverter.dialog_fragments.CurrencyPickerDialogFragment;
import com.sirgoingfar.currencyconverter.models.CalculatorViewModel;
import com.sirgoingfar.currencyconverter.models.data.Currency;
import com.sirgoingfar.currencyconverter.models.data.Option;
import com.sirgoingfar.currencyconverter.utils.DateUtil;
import com.sirgoingfar.currencyconverter.utils.NumberFormatUtil;
import com.sirgoingfar.currencyconverter.utils.StringUtil;
import com.sirgoingfar.currencyconverter.views.CalculatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the Controller of the Currency Calculator
 */
public class CalculatorActivity extends AppCompatActivity implements CalculatorView.ActionListener, NumberFormatterTextWatcher.InputListener, CurrencyPickerDialogFragment.SingleChoiceListener {

    private CalculatorView viewHolder;
    private CalculatorViewModel model;
    private EventBus eventBus;

    private List<Currency> allCurrencyList = new ArrayList<>();
    private List<LatestRateEntity> latestRateEntities = new ArrayList<>();
    private List<HistoricalRateEntity> historicalRateEntities = new ArrayList<>();

    private Currency currencyFrom;
    private Currency currencyTo;

    private BigDecimal inputValue = new BigDecimal(0);

    private boolean isPeriod30 = true;
    private boolean isSourceCurrency;
    private int currencyIndex;

    private CurrencyPickerDialogFragment dialog;

    private LiveData<List<HistoricalRateEntity>> historicalRateObserver;

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
            computeConversionValueAndUpdateScreen();
        });

        //poll historical rate data
        model.getHistoricalRateData();
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
        viewHolder.changeCurrentFromViews(currencyFrom.getCode(), currencyFrom.getFlagUrl());
        viewHolder.changeCurrentToViews(currencyTo.getCode(), currencyTo.getFlagUrl());

        //fetch latest rate data
        fetchLatestRates();

        //set up chart
        viewHolder.setupTrendChart(isPeriod30 ? DateUtil.CONST_30 : DateUtil.CONST_90, currencyTo.getCode());
        onPeriodSelectorClicked(isPeriod30);
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

    /**
     * This is a callback function that is invoked when there is
     * a text change on the amount input EditText.
     * <p>
     * The function sets the variable that holds the amount input value.
     *
     * @param formatted   is the NumberFormat#format output version of the amount input
     * @param unformatted is the raw amount input
     */
    @Override
    public void onChange(String unformatted, String formatted) {
        if (TextUtils.isEmpty(unformatted))
            updateDestCurrencyValue("");

        inputValue = NumberFormatUtil.parseAmount(unformatted);
    }

    /**
     * This is a callback function that is invoked when there is a change
     * in either the source or destination currency.
     * <p>
     * The function generates the available currencies data and show the the
     * Currency Picker dialog
     *
     * @param isSourceCurrency is the flag to know which of the currency selector is clicked
     */
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

    /**
     * This function generates a list of currency objects excluding the currency with code "excludeCode".
     *
     * @param excludeCode is the currency code for the currency whose object is to be excluded from the list
     * @param countryName is the country name for the currently selected currency
     * @return a list of currency options
     */
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


    /**
     * This function finds a Currency with name 'name' from the list of all available currencies.
     *
     * @param name is the name of the currency
     * @return the Currency object whose name is 'name'
     */
    private Currency getCurrencyByName(String name) {
        for (Currency currency : allCurrencyList) {
            if (TextUtils.equals(currency.getName(), name))
                return currency;
        }

        return null;
    }

    /**
     * This is the callback function that is invoked when the Currency Trend Chart selector is clicked.
     * <p>
     * The function makes a database call to fetch the historical dataset for the time frame.
     * <p>
     * On a successful data pull, the trend chart is updated.
     *
     * @param isPeriod30 is the flag to differentiate which of the selection option that is clicked.
     */
    @Override
    public void onPeriodSelectorClicked(boolean isPeriod30) {
        this.isPeriod30 = isPeriod30;
        viewHolder.toggleGraphPeriodSelector(isPeriod30);

        viewHolder.setupTrendChart(isPeriod30 ? DateUtil.CONST_30 : DateUtil.CONST_90, currencyTo.getCode());

        if (currencyTo == null)
            return;

        long minTime;

        if (isPeriod30)
            minTime = DateUtil.getThirtyDaysAgoEarliestTime();
        else
            minTime = DateUtil.getNintyDaysAgoEarliestTime();

        historicalRateObserver = model.getHistoricalRateLiveData(currencyTo.getCode(), minTime);
        historicalRateObserver.observe(this, historicalRateEntities -> {
            if (historicalRateEntities == null || historicalRateEntities.isEmpty())
                return;

            CalculatorActivity.this.historicalRateEntities = historicalRateEntities;
            updateTrendChart();
        });
    }

    /**
     * This is the callback function that is invoked when the `CONVERT` button is clicked.
     * <p>
     * This function initiates the currency conversion operation.
     */
    @Override
    public void onConvertBtnClick() {
        viewHolder.toggleLoader(true);
        computeConversionValueAndUpdateScreen();
    }

    /**
     * This is the callback function that is called when a Currency is selected from the Currency Picker dialog.
     * <p>
     * The function refreshes the screen accordingly.
     *
     * @param option           is the Currency clicked.
     * @param position         is the position of the currency clicked on the list
     * @param isOptionSelected is the flag that indicates whether an item is selected or mnot.
     */
    @Override
    public void onCurrencyOptionSelected(Option option, int position, boolean isOptionSelected) {
        Currency selectedCurrency = getCurrencyByName(option.getName());

        if (isSourceCurrency)
            currencyFrom = selectedCurrency;
        else {
            currencyTo = selectedCurrency;
            onPeriodSelectorClicked(isPeriod30);
        }

        updateScreen();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    /**
     * This function is the one that performs the conversion computation.
     * <p>
     * It updates the screen accordingly on a successful computation.
     */
    private void computeConversionValueAndUpdateScreen() {
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

    /**
     * The function updates the last rate poll time.
     */
    private void updateTimestamp() {
        String time = StringUtil.getUTCTimeFrom(DateUtil.toMillis(model.getLastRateFetchTime()));
        viewHolder.updateTimeStamp(getString(R.string.text_time_stamp, time));
    }

    /**
     * The function determines if there is a rate data.
     *
     * @return a flag to determine if the rate data is available or not.
     */
    private boolean isLatestRateAvailable() {
        return latestRateEntities != null && !latestRateEntities.isEmpty();
    }

    /**
     * This function gets a LatestRateEntity object that has code 'currencyCode'.
     *
     * @param currencyCode is the code for the Currency to be returned.
     * @return an object of LatestRateEntity whose code is `currencyCode`
     */
    private LatestRateEntity getEntityForCurrencyWithCode(String currencyCode) {
        for (LatestRateEntity entity : latestRateEntities) {
            if (TextUtils.equals(entity.getCode(), currencyCode))
                return entity;
        }

        return null;
    }

    /**
     * This function updates the destination currency view.
     *
     * @param value is the equivalence of the
     */
    private void updateDestCurrencyConversionValue(BigDecimal value) {
        String text = NumberFormatUtil.format(value);
        updateDestCurrencyValue(text);
    }


    /**
     * This function does the destination currency view update.
     *
     * @param text is the text to update the view with.
     */
    private void updateDestCurrencyValue(String text) {
        viewHolder.setDestCurrencyValue(text);
        viewHolder.toggleLoader(false);
    }

    /**
     * The function updates the screen (currency code and logo)
     *
     * It, in turn, triggers the conversion computation.
     *
     */
    private void updateScreen() {

        if (isSourceCurrency)
            viewHolder.changeCurrentFromViews(currencyFrom.getCode(), currencyFrom.getFlagUrl());
        else
            viewHolder.changeCurrentToViews(currencyTo.getCode(), currencyTo.getFlagUrl());

        computeConversionValueAndUpdateScreen();
    }

    /**
     *
     * The function updates the Trend chart by bind data to the view (chart).
     *
     * */
    private void updateTrendChart() {
        viewHolder.bindTrendData(formMapFromList(this.historicalRateEntities));
    }


    /**
     *
     * This function generates a map from a List.
     *
     * @param list to generate the Map from.
     *
     * @return the Map of <Integer, Float>
     *
     * */
    private Map<Integer, Float> formMapFromList(List<HistoricalRateEntity> list) {
        Map<Integer, Float> map = new HashMap<>();
        int count = 1;

        for (HistoricalRateEntity entity : list) {
            map.put(count, (float) entity.getRate());
            ++count;
        }

        return map;
    }

}
