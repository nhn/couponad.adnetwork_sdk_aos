package com.nhnent.adplatform.adnetwork.couponadsample;

import android.app.Application;

import com.nhnent.adplatform.adnetwork.base.AdNetwork;

public class CouponAdSampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // SDK 초기화 메서드 호출시, 발급받은 pubId를 전달
        AdNetwork.config(this, "sdktestreal");
    }

}
