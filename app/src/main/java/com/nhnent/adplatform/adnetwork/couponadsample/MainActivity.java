package com.nhnent.adplatform.adnetwork.couponadsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nhnent.adplatform.adnetwork.base.AdNetwork;
import com.nhnent.adplatform.adnetwork.coupon.AdNetworkActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnTest;
    private TextView txtBadgeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();
        requestBadgeType();
    }

    private void initView() {
        btnTest = findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdNetworkActivity.class);
                startActivity(intent);
            }
        });

        txtBadgeType = findViewById(R.id.txt_badge_type);
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
            public void onFail() {
                txtBadgeType.setVisibility(View.GONE);
            }
        });
    }

}
