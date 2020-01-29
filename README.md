

# 1. History

| 버전 | 날짜 | 내용 |
| --- | --- | --- |
| v1.0.1 | 2019.12.27 | 약관 동의 Flow 추가 |
| v1.0.0 | 2019.11.29 |  |

<br>

# 2. dependencies

## 2-1. 원격저장소(jcenter)에서 다운받아 연동하는 경우

제휴네트워크SDK는 jcenter maven repository에 업로드되어 있습니다.
```gradle
// project > build.gradle
allprojects {
    repositories {
        jcenter()
        ...
    }
}
```
프로젝트 빌드시 jcenter에서 SDK를 다운받을 수 있도록,
위와같이 jcenter 저장소를 명시합니다.

```gradle
// app > build.gradle
dependencies {
    implementation 'com.nhn.couponad:adnetwork:1.0@aar'

    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.google.android.gms:play-services-base:16.1.0'
    implementation 'com.google.android.gms:play-services-ads:17.2.0'
    implementation 'com.google.android.gms:play-services-location:16.0.0'
    implementation 'com.toast.android:toast-logger:0.19.2'
    ...
}
```
모듈 레벨의 build.gradle에서 연동하는 SDK의 버전을 명시하여 의존성을 선언합니다.
하위 종속 라이브러리들은 아래의 2-3에서 이어서 설명합니다.

## 2-2. arr 파일을 직접 연동하는 경우

```gradle
// app > build.gradle
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    implementation name: 'adnetwork-1.0', ext: 'aar'

    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.google.android.gms:play-services-base:16.1.0'
    implementation 'com.google.android.gms:play-services-ads:17.2.0'
    implementation 'com.google.android.gms:play-services-location:16.0.0'
    implementation 'com.toast.android:toast-logger:0.19.2'
    ...
}
```

.arr파일을 **[app > libs]** 경로에 복사한 뒤, 파일명을 name 필드에 명시하여 의존성을 선언합니다.
하위 종속 라이브러리들은 아래의 2-3에서 이어서 설명합니다.


## 2-3. 의존성 관련 참고사항

### # 하위 종속성 라이브러리
제휴네트워크SDK는 쿠폰함 화면 구성을 위해 appcomat-v7을 사용합니다.
또한, 광고ID와 위치정보를 기반으로 사용자별 맞춤쿠폰을 제공하기 위해 play-services 라이브러리들을 사용하며,
SDK내에서 발생하는 예외 상황을 로그로 감지하기위해 toast-logger를 사용합니다.

종속 라이브러리의 버전은 프로젝트의 상황에 맞게 적절하게 선택바랍니다.

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
<manifest ...>
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
            android:name="com.nhn.couponad.coupon.AdNetworkActivity"
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

<br>

# 7. 약관동의 및 타겟배너 노출 Flow

맞춤쿠폰 관련 타겟배너를 노출하고 배너 클릭시 캠페인 화면으로 이동합니다.
단, 맞춤쿠폰 서비스 관련 약관에 동의한 사용자에 한해서만 타겟배너를 노출해야하며, 약관 미동의 사용자에게는 디폴트배너를 노출해야합니다.

* 디폴트배너 : 약관 미동의 사용자에게 노출. 디자인은 자유롭게 구현.
* 타겟배너 : 약관 동의한 사용자에게만 노출. SDK에서 제공하는 AdNetworkTargetBannerView를 통해 배너컨텐츠가 자동으로 로딩됨.

SDK를 연동하는 앱의 테마/디자인을 고려하여 약관 동의 화면 UI는 SDK에서 제공하지 않습니다. UI는 앱의 디자인에 맞게 자유롭게 구현바랍니다.
미동의 약관 정보는 SDK에서 제공하는 메서드를 통해 조회할 수 있습니다.

미동의 약관 조회/동의 및 타겟배너를 노출하는 전 과정은 샘플앱에도 구현되어 있으며, TermsFlowTestActivity의 코드를 참고바랍니다.

## 7-1. 약관 목록 조회

```java
private void requestTermsList() {
    AdNetwork.getTermsList(new AdNetwork.OnAdNetworkTermsListener() {
        @Override
        public void onSuccess(List<AdNetworkTerms> termsList) {
            if (termsList == null) {
                // 약관 데이터가 null인 경우는 존재할 수 없음. 오류로 간주.
                // 디폴트배너 노출, 타겟배너 비노출.
                return;
            }

            if (termsList.isEmpty()) {
                // 이미 모든 약관에 동의한 상태.
                // 디폴트배너 비노출, 타겟배너 노출.
                return;
            }

            if (termsList.size() > 0) {
                // 미동의 약관이 존재하는 상태.
                // 디폴트배너 노출, 타겟배너 비노출.
                // 약관 동의 과정으로 이동 (팝업 or 화면)
            }
        }

        @Override
        public void onFail(String message) {
            // API 오류. 디폴트배너 노출, 타겟배너 비노출.
        }
    });
}

@Override
protected void onResume() {
    super.onResume();
    requestTermsList();
}
```

`AdNetwork.getTermsList()`를 통해 동의할 약관이 있는지 조회합니다.
* 모든 약관에 동의한 상태 : 타겟배너 노출.
* 동의할 약관이 있는 상태 : 디폴트배너 노출 및 약관 동의 과정으로 이동.

맞춤쿠폰 캠페인 화면에서 사용자가 언제든지 동의한 약관을 철회할 수 있습니다.
따라서, 화면이 갱신될 때마다 약관 동의여부를 조회하여 배너 노출상태를 변경할 수 있도록, onResume()에서 약관 동의여부를 매번 조회하도록 합니다.

또한, **[기기 설정 > Google > 광고]** 메뉴에서
**"광고 개인 최적화 선택 해제(isLimitAdTrackingEnabled)"** 옵션이 On 상태인 경우에는 약관 정보를 조회할 수 없습니다.
이 경우, 오류메시지와 함께 onFail() 메서드로 콜백됩니다.


## 7-2. 약관 동의 과정
7-1에서 응답받은 약관 정보는 아래의 데이터로 구성되어 있습니다.

```java
public class AdNetworkTerms implements Serializable {
    private String code;    // 약관 구분용 코드
    private String name;    // 사용자에게 노출할 약관 명칭
    private String url;     // 약관 상세내용 URL
}
```

위 데이터를 참고하여 약관 동의 화면(or 팝업)을 구현하면 됩니다.
샘플앱에서는 팝업 형태로 구현되어 있으며, TermsFlowTestActivity와 아래 클래스들을 참고하시면 됩니다.
* AdNewtworkTermsView : 약관 동의 뷰. '전체동의' 버튼 포함.
* AdNetworkTermsItemView : 단일 약관 1개를 표현한 뷰.
* AdNetworkTermsDetailDialog : 약관 상세 내용 보기를 눌렀을때 노출할 다이얼로그.

사용자가 약관에 체크한 뒤에 '확인' 버튼을 누르면, 동의하는 약관 정보를 서버로 전송해야 합니다.

```java
private void requestTermsAgreements(List<String> checkedTermsCodes) {
    AdNetwork.requestTermsAgreements(checkedTermsCodes, new AdNetwork.OnTermsAgreementsListener() {
        @Override
        public void onSuccess() {
            // 약관 동의 성공. 타겟배너 노출.
            targetBannerView.loadTargetBanner(bannerLoadingListener);
        }

        @Override
        public void onFail(String message) {
            // 약관 동의 실패. 디폴트배너 노출.
        }
    });
}
```

`AdNetwork.requestTermsAgreements()`를 통해 약관 동의 요청을 할 수 있으며, API가 성공한 이후에는 다시 타겟배너를 노출하도록 합니다.


## 7-3. 디폴트/타겟 배너 클릭시 캠페인 화면으로 이동

```java
private TextView txtDefaultBanner;
private AdNetworkTargetBannerView targetBannerView;


@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_terms_flow_test);

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
}
```

배너 클릭시, 맞춤쿠폰 캠페인 화면 이동은 SDK에서 제공해주는 메서드를 통해 Intent를 받도록 합니다.
메서드 호출시, 어떤 배너를 통해 진입하는지 구분하기 위해 EntryType 인자를 전달합니다.




