package com.sirgoingfar.currencyconverter.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sirgoingfar.currencyconverter.App;
import com.sirgoingfar.currencyconverter.R;
import com.sirgoingfar.currencyconverter.database.entities.LatestRateEntity;
import com.sirgoingfar.currencyconverter.models.CalculatorViewModel;
import com.sirgoingfar.currencyconverter.models.data.Currency;
import com.sirgoingfar.currencyconverter.views.CalculatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CalculatorActivity extends AppCompatActivity implements CalculatorView.ActionListener/*, NumberFormatterTextWatcher.InputListener*/ {

    private CalculatorView viewHolder;
    private CalculatorViewModel model;
    private EventBus eventBus;

    private List<Currency> allCurrencyList = new ArrayList<>();
    private List<LatestRateEntity> latestRateEntities = new ArrayList<>();

    private Currency currencyFrom;
    private Currency currencyTo;

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
        viewHolder = new CalculatorView(this, getWindow().getDecorView().findViewById(android.R.id.content), this/*, this*/);
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
//            computeConversionValue
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

    /*@Override
    public void onChange(String unformatted, String formatted) {

    }*/
}
