package com.nhn.couponad.sample.terms;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nhn.couponad.api.json.model.AdNetworkTerms;
import com.nhn.couponad.sample.R;

import java.util.ArrayList;
import java.util.List;

public class AdNetworkTermsView extends FrameLayout {
    private static final String TAG = "AdNetworkTermsView";

    // NOTE:
    // 약관동의 UI 구현을 위한 커스텀뷰 예시입니다.
    // 약관 UI는 연동하는 앱의 테마/UX에 맞게 자유롭게 구현해주세요.

    private View btnCheckAll;
    private TextView txtCheckAll;
    private LinearLayout termsListWrap;
    private Button btnSubmit;
    private AdNetworkTermsDetailDialog termsDetailDialog;

    private OnAdNetworkTermsListener listener;

    public interface OnAdNetworkTermsListener {
        void onClickSubmit(List<String> checkedTermsCodes, boolean isAllChecked);
    }


    public AdNetworkTermsView(@NonNull Context context) {
        this(context, null, 0);
    }

    public AdNetworkTermsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdNetworkTermsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View rootView = inflate(getContext(), R.layout.adnetwork_terms_view, this);

        btnCheckAll = rootView.findViewById(R.id.btn_check_all);
        txtCheckAll = rootView.findViewById(R.id.txt_check_all);
        termsListWrap = rootView.findViewById(R.id.wrap_terms_list);
        btnSubmit = rootView.findViewById(R.id.btn_submit);

        btnCheckAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toggleAllChecked = !isAllChecked();

                changeCheckStateAllTerms(toggleAllChecked);
                changeCheckAllBtnState(toggleAllChecked);
            }
        });

        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickSubmit(getCheckedTermsCodes(), isAllChecked());
                }
            }
        });

        termsDetailDialog = new AdNetworkTermsDetailDialog(getContext());
        termsDetailDialog.setButton(AlertDialog.BUTTON_POSITIVE, "확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void setOnAdNetworkTermsListener(OnAdNetworkTermsListener listener) {
        this.listener = listener;
    }

    // 약관 목록 설정
    public void setTermsList(List<AdNetworkTerms> termsList) {
        if (termsList == null || termsList.isEmpty())
            return;

        termsListWrap.removeAllViews();

        for (final AdNetworkTerms termsInfo : termsList) {
            AdNetworkTermsItemView termsItemView = new AdNetworkTermsItemView(getContext(), termsInfo);
            termsItemView.setOnTermsCheckedChangeListener(new AdNetworkTermsItemView.OnTermsCheckedChangeListener() {
                @Override
                public void onTermsCheckedChange(boolean isChecked) {
                    boolean isAllChecked = isAllChecked();
                    changeCheckAllBtnState(isAllChecked);
                }

                @Override
                public void onClickTermsDetail(String detailUrl) {
                    Log.d(TAG, "AdNetworkTermsView :: onClickTermsDetail() | url = " + detailUrl);
                    termsDetailDialog.loadUrl(detailUrl);
                    termsDetailDialog.show();
                }
            });

            termsListWrap.addView(termsItemView);
        }
    }

    // 모든 약관 체크상태 변경
    private void changeCheckStateAllTerms(boolean isChecked) {
        final int termsCount = termsListWrap.getChildCount();

        for (int i = 0; i < termsCount; ++i) {
            AdNetworkTermsItemView termsItemView = (AdNetworkTermsItemView) termsListWrap.getChildAt(i);
            termsItemView.setCheck(isChecked);
        }
    }

    // 체크된 약관 코드 리스트 반환
    private List<String> getCheckedTermsCodes() {
        List<String> checkedTermsCodes = new ArrayList<>();

        final int termsCount = termsListWrap.getChildCount();
        for (int i = 0; i < termsCount; ++i) {
            AdNetworkTermsItemView termsItemView = (AdNetworkTermsItemView) termsListWrap.getChildAt(i);
            if (termsItemView.isChecked()) {
                checkedTermsCodes.add(termsItemView.getTermsCode());
            }
        }

        return checkedTermsCodes;
    }

    // 선택/필수 약관 모두 체크했는지 여부
    private boolean isAllChecked() {
        final int termsCount = termsListWrap.getChildCount();
        for (int i = 0; i < termsCount; ++i) {
            AdNetworkTermsItemView termsItemView = (AdNetworkTermsItemView) termsListWrap.getChildAt(i);
            if ( !termsItemView.isChecked() )
                return false;
        }

        return true;
    }

    // 전체동의 버튼 상태 변경
    private void changeCheckAllBtnState(boolean isAllChecked) {
        final int textColor = Color.parseColor(isAllChecked ? "#fa2828" : "#333333");
        final int chkAllBtnBgResId = isAllChecked ?
                R.drawable.border_red_fa2828_bg_radius_1dp :
                R.drawable.border_gray_b3e3e5e9_bg_transparent_radius_1dp;

        txtCheckAll.setTextColor(textColor);
        btnCheckAll.setBackgroundResource(chkAllBtnBgResId);
    }

}
