package com.sirgoingfar.currencyconverter.dialog_fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sirgoingfar.currencyconverter.R;
import com.sirgoingfar.currencyconverter.models.data.Option;
import com.sirgoingfar.currencyconverter.utils.FontUtils;

import java.util.List;

import butterknife.ButterKnife;

/**
 * The class is responsible for controlling the Currency Picker dialog
 */
public class CurrencyPickerDialogFragment extends DialogFragment {

    private RecyclerView recyclerView;

    private Context context;
    private List<Option> optionContent;
    private CurrencyPickerAdapter recyclerAdapter;
    private SingleChoiceListener singleChoiceListener;
    private int previouslySelectedPosition;
    private String dialogTitle;

    /**
     * @param context                    the context of the caller
     * @param items                      the list of option items
     * @param listener                   the instance of the action listener class
     * @param dialogTitle                the header instruction text
     * @param previouslySelectedPosition the option initially selected
     */
    public static CurrencyPickerDialogFragment newInstance(Context context, List<Option> items, SingleChoiceListener
            listener, String dialogTitle, int previouslySelectedPosition) {
        CurrencyPickerDialogFragment dialog = new CurrencyPickerDialogFragment();
        dialog.context = context;
        dialog.optionContent = items;
        dialog.singleChoiceListener = listener;
        dialog.dialogTitle = dialogTitle;
        dialog.previouslySelectedPosition = previouslySelectedPosition;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        int screenHeight = (int) (metrics.heightPixels * 0.80);
        dialog.setContentView(R.layout.dialog_fragment_currency_picker);
        dialog.getWindow().setLayout(screenWidth, screenHeight);
        dialog.setCanceledOnTouchOutside(true);

        recyclerView = dialog.findViewById(R.id.rv_option);
        TextView headerText = dialog.findViewById(R.id.popup_dialog_header);
        headerText.setText(dialogTitle);
        FontUtils.applyDefaultFont(context, headerText, FontUtils.STYLE_BOLD);

        recyclerAdapter = new CurrencyPickerAdapter(optionContent);
        recyclerView.setAdapter(recyclerAdapter);

        return dialog;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        optionContent = null;
    }

    public void setSelectedOptionIndex(int selectedItemIndex) {
        if (selectedItemIndex >= 0)
            previouslySelectedPosition = selectedItemIndex;
    }

    class CurrencyPickerAdapter extends RecyclerView.Adapter<CurrencyPickerAdapter.Holder> {
        private List<Option> data;
        private boolean stateIndicator = false;

        CurrencyPickerAdapter(List<Option> data) {
            this.data = data;
        }

        @Override
        public CurrencyPickerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View optionView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_option, parent, false);
            ButterKnife.bind(this, optionView);
            return new Holder(optionView);
        }

        @Override
        public void onBindViewHolder(CurrencyPickerAdapter.Holder holder, int position) {
            Option currentItem = data.get(holder.getAdapterPosition());

            holder.ctvDesc.setText(currentItem.getText());

            Glide.with(context)
                    .load(currentItem.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivFlag);

            FontUtils.applyDefaultFont(getContext(), holder.ctvDesc, FontUtils.STYLE_MEDIUM);

            holder.ctvDesc.setChecked(holder.getCurrentItem().isSelected());
        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            private CheckedTextView ctvDesc;
            private ImageView ivFlag;

            Holder(View itemView) {
                super(itemView);

                ctvDesc = itemView.findViewById(R.id.optionItem);
                ivFlag = itemView.findViewById(R.id.iv_flag);

                itemView.setOnClickListener(view -> {
                    CheckedTextView ctv = view.findViewById(R.id.optionItem);
                    String value = ctv.getText().toString();
                    stateIndicator = !TextUtils.isEmpty(value)
                            && !ctv.isChecked();

                    data.get(getAdapterPosition()).setSelected(stateIndicator);
                    singleChoiceListener.onCurrencyOptionSelected(getCurrentItem(),
                            getAdapterPosition(),
                            stateIndicator);

                    if (getAdapterPosition() != previouslySelectedPosition && previouslySelectedPosition >= 0) {
                        dismiss();
                    }


                });
            }

            Option getCurrentItem() {
                return data.get(getAdapterPosition());
            }
        }

    }

    /**
     * The interface callback for the event function to be invoked when an option item is clicked
     */
    public interface SingleChoiceListener {

        void onCurrencyOptionSelected(Option option, int position, boolean isOptionSelected);

    }
}
