package com.sirgoingfar.currencyconverter.customs;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.sirgoingfar.currencyconverter.R;
import com.sirgoingfar.currencyconverter.utils.FontUtils;
import com.sirgoingfar.currencyconverter.utils.StringUtil;

public class TrendChartMarkerView extends MarkerView {

    private final int numOfDaysInPeriod;
    private String currencyCode;

    private TextView tvLabel;
    private TextView tvValue;

    private Context context;

    private MPPointF mOffset;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public TrendChartMarkerView(Context context, int layoutResource, String currencyCode, int numOfDaysInPeriod) {

        super(context, layoutResource);

        this.context = context;
        this.currencyCode = currencyCode;
        this.numOfDaysInPeriod = numOfDaysInPeriod;

        tvLabel = findViewById(R.id.tv_label);
        tvValue = findViewById(R.id.tv_value);

        FontUtils.applyDefaultFont(context, tvLabel, FontUtils.STYLE_BOLD);
        FontUtils.applyDefaultFont(context, tvValue, FontUtils.STYLE_REGULAR);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        float x = e.getX();
        float y = e.getY();

        tvLabel.setText(StringUtil.getChartXAxisLabel(x, numOfDaysInPeriod));
        tvValue.setText(StringUtil.getChartMarkerBody(context, y, currencyCode));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(getWidth()/128, getHeight());
        }
        return mOffset;
    }
}
