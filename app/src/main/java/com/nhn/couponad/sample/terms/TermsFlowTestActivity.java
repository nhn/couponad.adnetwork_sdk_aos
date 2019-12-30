package com.nhn.couponad.sample.terms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.couponad.api.json.model.AdNetworkTerms;
import com.nhn.couponad.base.AdNetwork;
import com.nhn.couponad.coupon.EntryType;
import com.nhn.couponad.coupon.banner.AdNetworkTargetBannerView;
import com.nhn.couponad.coupon.banner.BannerType;
import com.nhnent.adplatform.adnetwork.couponadsample.R;

import java.util.List;

public class TermsFlowTestActivity extends AppCompatActivity {
    private static final String TAG = "TermsFlowTestActivity";

    private TextView txtDefaultBanner;
    private AdNetworkTargetBannerView targetBannerView;
    private AdNetworkTermsView termsView;
    private AlertDialog termsDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_terms_flow_test);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestTermsList();
    }

    private void initView() {
        // 디폴트 배너
        txtDefaultBanner = findViewById(R.id.txt_default_banner);
        AdNetwork.notifyBannerImpression(BannerType.DEFAULT);
        txtDefaultBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AdNetwork.createIntentForAdNetworkHome(TermsFlowTestActivity.this, EntryType.DEFAULT_BANNER);
                startActivity(intent);
            }
        });

        // 타겟 배너
        targetBannerView = findViewById(R.id.target_banner);
        targetBannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AdNetwork.createIntentForAdNetworkHome(TermsFlowTestActivity.this, EntryType.TARGET_BANNER);
                startActivity(intent);
            }
        });

        // 약관 동의 팝업
        termsView = new AdNetworkTermsView(this);
        termsView.setOnAdNetworkTermsListener(new AdNetworkTermsView.OnAdNetworkTermsListener() {
            @Override
            public void onClickSubmit(List<String> checkedTermsCodes, boolean isAllChecked) {
                if (isAllChecked) {
                    requestTermsAgreements(checkedTermsCodes);
                    termsDialog.dismiss();
                } else {
                    Toast.makeText(TermsFlowTestActivity.this, "모든 약관에 동의해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        termsDialog = new AlertDialog.Builder(this)
                .setView(termsView)
                .create();
    }

    /**
     * 미동의 약관 목록 조회
     * - 미동의 약관 존재 => 약관 동의창 팝업
     * - 이미 약관 동의 상태 => 타겟 배너 조회 및 타겟 배너 노출
     */
    private void requestTermsList() {
        AdNetwork.getTermsList(new AdNetwork.OnAdNetworkTermsListener() {
            @Override
            public void onSuccess(List<AdNetworkTerms> termsList) {
                Log.d(TAG, "TermsFlowTestActivity :: onSuccess() | termsList = " + termsList);

                // 약관 데이터가 null인 경우는 존재할 수 없음.
                // 비정상 응답으로 간주하여 타겟 배너를 노출하지 않음. (디폴트배너 노출 상태 유지)
                if (termsList == null) {
                    displayAdNetworkBanner(true, false);
                    return;
                }

                // 미동의 약관이 존재하는 상태.
                if (termsList.size() > 0) {
                    displayAdNetworkBanner(true, false);

                    termsView.setTermsList(termsList);
                    termsDialog.show();
                }

                // 동의할 약관이 없는 경우. 즉, 이미 약관에 동의한 상태.
                // 타겟배너 정보를 조회하여 [디폴트배너 => 타겟배너] 전환.
                else {
                    targetBannerView.loadTargetBanner(bannerLoadingListener);
                }
            }

            @Override
            public void onFail(String message) {
                Log.d(TAG, "TermsFlowTestActivity :: onFail() | message = " + message);
            }
        });
    }

    /**
     * 약관 동의하기
     *
     * @param checkedTermsCodes     동의하는 약관 코드 리스트
     */
    private void requestTermsAgreements(List<String> checkedTermsCodes) {
        AdNetwork.requestTermsAgreements(checkedTermsCodes, new AdNetwork.OnTermsAgreementsListener() {
            @Override
            public void onSuccess() {
                targetBannerView.loadTargetBanner(bannerLoadingListener);
            }

            @Override
            public void onFail(String message) {
                Log.d(TAG, "TermsFlowTestActivity :: onFail() | message = " + message);
            }
        });
    }

    private AdNetworkTargetBannerView.OnTargetBannerLoadingListener bannerLoadingListener = new AdNetworkTargetBannerView.OnTargetBannerLoadingListener() {
        @Override
        public void onSuccess() {
            displayAdNetworkBanner(false, true);
        }

        @Override
        public void onFail(String message) {
            displayAdNetworkBanner(true, false);
        }
    };

    private void displayAdNetworkBanner(boolean visibleDefaultBanner,
                                        boolean visibleTargetBanner) {
        txtDefaultBanner.setVisibility(visibleDefaultBanner ? View.VISIBLE : View.GONE);
        targetBannerView.setVisibility(visibleTargetBanner ? View.VISIBLE : View.GONE);
    }

}
