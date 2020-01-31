package com.nhn.couponad.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nhn.couponad.base.AdNetwork;
import com.nhn.couponad.coupon.EntryType;
import com.nhn.couponad.sample.terms.TermsFlowTestActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button btnGoCampaign;
    private Button btnTermsAgreementProcess;
    private TextView txtBadgeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();

        requestBadgeType();
    }

    private void initView() {
        txtBadgeType = findViewById(R.id.txt_badge_type);
        btnGoCampaign = findViewById(R.id.btn_go_campaign);
        btnTermsAgreementProcess = findViewById(R.id.btn_terms_agreement_process);

        btnGoCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AdNetwork.createIntentForAdNetworkHome(MainActivity.this, EntryType.NORMAL);
                startActivity(intent);
            }
        });

        btnTermsAgreementProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TermsFlowTestActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 보유한 맞춤쿠폰의 뱃지 타입을 요청하고, ["HOT", "NEW", ..] 형태의 뱃지 타입을 받아옵니다.
     */
    private void requestBadgeType() {
        AdNetwork.getBadgeType(new AdNetwork.OnNewBadgeExistenceListener() {
            @Override
            public void onSuccess(String badgeType) {
                // 뱃지타입을 받아온 뒤, UI에 적절하게 노출
                if (TextUtils.isEmpty(badgeType)) {
                    txtBadgeType.setVisibility(View.GONE);
                } else {
                    txtBadgeType.setText(badgeType);
                    txtBadgeType.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(String message) {
                txtBadgeType.setVisibility(View.GONE);
                Log.d(TAG, "requestBadgeType() | onFail(). message = " + message);
            }
        });
    }

}
