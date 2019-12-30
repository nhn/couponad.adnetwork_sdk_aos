

# 1. History

| 버전 | 날짜 | 내용 |
| --- | --- | --- |
| v1.0.0 | 2019.11.29 |  |
| v1.0.1 | 2019.12.27 | 약관 동의 Flow 추가 |

<br>

# 2. dependencies

## 2-1. 라이브러리 연동

### # .arr 파일을 직접 다운받는 경우

```gradle
// app > build.gradle
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    implementation name: 'adnetwork-1.0', ext: 'aar'
}
```

.arr파일을 **[app > libs]** 경로에 복사한 뒤에 위와 같이 선언하여 연동합니다.


### # jcenter에서 원격으로 다운받는 경우

* ㅡ


## 2-2. 종속 라이브러리 선언

```gradle
dependencies {
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.google.android.gms:play-services-base:16.1.0'
    implementation 'com.google.android.gms:play-services-ads:17.2.0'
    implementation 'com.google.android.gms:play-services-location:16.0.0'
    implementation 'com.toast.android:toast-logger:0.19.2'
}
```

제휴네트워크SDK는 쿠폰함 화면 구성을 위해 support-v7:28.0.0을 사용합니다.
또한, 광고ID와 위치정보를 기반으로 사용자별 맞춤쿠폰을 제공하기 위해 play-services 라이브러리들을 사용하며,
SDK내에서 발생하는 예외 상황을 로그로 감지하기위해 toast-logger를 사용합니다.


### # play-services-ads v17.x.x 관련
17.x.x 버전 이상의 play-services-ads를 사용하는 경우, 앱 실행시 아래와 같은 오류가 발생할 수 있습니다.
> This step is required as of Google Mobile Ads SDK version 17.0.0. Failure to add this tag results in a crash with the message: "The Google Mobile Ads SDK was initialized incorrectly

애드몹에서 발급받은 APPLICATION_ID를 아래와 같이 명시하거나([구글가이드](https://developers.google.com/admob/android/quick-start#update_your_androidmanifestxml)),
```xml
<manifest>
    <application>
        <!-- Sample AdMob App ID: ca-app-pub-3940256099942544~3347511713 -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
    </application>
</manifest>
```

AdManagerApp임을 명시하는 아래 코드를 추가하여 해결할 수 있습니다.([구글가이드](https://developers.google.com/ad-manager/mobile-ads-sdk/android/quick-start))
```xml
<manifest>
    <application>
        <meta-data
            android:name="com.google.android.gms.ads.AD_MANAGER_APP"
            android:value="true"/>
    </application>
</manifest>
```

### # toast-logger 관련
앱에서 이미 ToastSDK를 사용중인 경우에 Manifest에서 ToastProjectId를 선언시,
meta-data 중복으로 빌드가 정상적으로 진행되지 않을 수 있습니다.
이 경우, 아래와 같이 tools:replace="value" 옵션을 추가하여 기존에 사용중인 ToastProjectId를 그대로 사용하도록 할 수 있습니다.

```xml
<meta-data
    android:name="com.toast.sdk.ToastProjectId"
    android:value="TOAST_PROJECT_ID"
    tools:replace="value" />
```

<br>

# 3. AndroidManifest.xml

## 3-1. 권한
```xml
<manifest  ...>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

    <application ... />
</manifest>
```

제휴네트워크 SDK는 쿠폰 정보를 받아오기위해 네트워크를 사용하며,
사용자의 현재 위치와 관련된 쿠폰을 제공하기 위해 위치 권한을 필요로 합니다.


## 3-2. 쿠폰함 화면 선언
```xml
<manifest>
    <application ...>

        <activity
            android:name="com.nhnent.adplatform.adnetwork.coupon.AdNetworkActivity"
            android:theme="@style/AdNetworkCompat.NoActionBar" />

    </application>
</manifest>
```

맞춤쿠폰 캠페인 화면으로 진입하기위해 반드시 위 Activity를 선언해야 합니다.

<br>

# 4. SDK 초기화
```java
public class CouponAdSampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // SDK 초기화 메서드 호출시, 발급받은 pubId를 전달
        AdNetwork.config(this, "sdktestreal");
    }

}
```

Application 클래스의 onCreate() 시점에 config() 메서드를 호출하여 SDK를 초기화합니다.
이 때, 반드시 발급받은 pubId를 함께 전달합니다.

<br>

# 5. 쿠폰 뱃지 받아오기
```java
/**
 * 보유한 맞춤쿠폰의 뱃지 타입을 요청하고, ["HOT", "NEW", ..] 형태의 뱃지 타입을 받아옵니다.
 */
private void requestBadgeType() {
    AdNetwork.getBadgeType(new AdNetwork.OnNewBadgeExistenceListener() {
        @Override
        public void onSuccess(String badgeType) {
            // 뱃지 타입을 받아온 뒤, UI에 적절하게 노출...
        }

        @Override
        public void onFail(Stirng message) {
        }
    });
}
```

AdNetwork.getBadgeType() 메서드를 통해 뱃지 타입을 받아옵니다.
onSuccess()에서 뱃지 타입을 받아오는데 성공한 뒤, UI에 적절하게 노출합니다.

<br>

# 6. 맞춤쿠폰 화면 진입
```java
btnTest.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, AdNetworkActivity.class);
        startActivity(intent);
    }
});
```

적절한 시점(사용자 버튼 클릭, ...)에 AdNetworkActivity로 진입하는 위 코드를 작성합니다.
