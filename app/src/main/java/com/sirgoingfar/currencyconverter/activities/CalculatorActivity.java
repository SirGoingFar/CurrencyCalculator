package com.sirgoingfar.currencyconverter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sirgoingfar.currencyconverter.App;
import com.sirgoingfar.currencyconverter.R;
import com.sirgoingfar.currencyconverter.models.CalculatorViewModel;
import com.sirgoingfar.currencyconverter.models.data.Currency;
import com.sirgoingfar.currencyconverter.utils.FontUtils;
import com.sirgoingfar.currencyconverter.utils.JsonUtil;
import com.sirgoingfar.currencyconverter.views.CalculatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CalculatorActivity extends AppCompatActivity implements CalculatorView.ActionListener {

    private CalculatorView viewHolder;
    private CalculatorViewModel model;
    private EventBus eventBus;

    private List<Currency> allCurrencyList = new ArrayList<>();

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
        viewHolder = new CalculatorView(this, getWindow().getDecorView().findViewById(android.R.id.content), this);
        model = new CalculatorViewModel(App.getInstance());

        //observe data changes
        model.getCurrencyListObserver().observe(this, currencies -> {
            if (currencies == null || currencies.isEmpty())
                return;

            Toast.makeText(this, "Data exist", Toast.LENGTH_SHORT).show();
            allCurrencyList = currencies;
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        //unregister EventBus
        eventBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pending(String text){
    }
}
