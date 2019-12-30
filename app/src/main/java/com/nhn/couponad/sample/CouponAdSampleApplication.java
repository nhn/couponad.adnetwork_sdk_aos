package com.nhn.couponad.sample;

import android.app.Application;

import com.nhn.couponad.base.AdNetwork;


public class CouponAdSampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // SDK 초기화 메서드 호출시, 발급받은 pubId를 전달
        AdNetwork.config(this, "sdktestreal");
    }

}
