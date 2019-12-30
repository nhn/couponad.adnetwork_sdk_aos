package com.nhn.couponad.sample.terms;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nhn.couponad.api.json.model.AdNetworkTerms;
import com.nhnent.adplatform.adnetwork.couponadsample.R;

public class AdNetworkTermsItemView extends FrameLayout {

    private AdNetworkTerms terms;

    private CheckBox chkTerms;
    private TextView txtShowDetail;
    private OnTermsCheckedChangeListener listener;

    interface OnTermsCheckedChangeListener {
        void onTermsCheckedChange(boolean isChecked);
        void onClickTermsDetail(String detailUrl);
    }

    public AdNetworkTermsItemView(Context context, AdNetworkTerms terms) {
        this(context);

        this.terms = terms;

        initView();
    }

    public AdNetworkTermsItemView(@NonNull Context context) {
        super(context);
    }

    private void initView() {
        View rootView = inflate(getContext(), R.layout.adnetwork_terms_item_view, this);

        chkTerms = rootView.findViewById(R.id.check_box_terms);
        chkTerms.setText(terms.getName());
        chkTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    listener.onTermsCheckedChange(isChecked);
                }
            }
        });

        txtShowDetail = rootView.findViewById(R.id.txt_show_terms_detail);
        txtShowDetail.setPaintFlags(txtShowDetail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtShowDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickTermsDetail(terms.getUrl());
                }
            }
        });
    }

    public void setOnTermsCheckedChangeListener(OnTermsCheckedChangeListener listener) {
        this.listener = listener;
    }

    public String getTermsCode() {
        return terms.getCode();
    }

    public boolean isChecked() {
        return chkTerms.isChecked();
    }

    public void setCheck(boolean isChecked) {
        chkTerms.setChecked(isChecked);
    }

}
