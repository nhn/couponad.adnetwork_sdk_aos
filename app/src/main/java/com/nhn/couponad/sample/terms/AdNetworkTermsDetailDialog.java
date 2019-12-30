package com.nhn.couponad.sample.terms;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.nhnent.adplatform.adnetwork.couponadsample.R;


public class AdNetworkTermsDetailDialog extends AlertDialog {
    private static final String TAG = "TermsDetailDialog";

    private WebView webViewTermsDetail;
    private ProgressBar circularProgress;


    protected AdNetworkTermsDetailDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.adnetwork_terms_detail_contents_view, null, false);

        webViewTermsDetail = rootView.findViewById(R.id.web_view_terms_detail);
        webViewTermsDetail.setWebViewClient(new AdNetworkTermsWebViewClient());

        circularProgress = rootView.findViewById(R.id.progress_bar);

        this.setView(rootView);
    }

    public void loadUrl(String url) {
        webViewTermsDetail.loadUrl(url);
    }


    private class AdNetworkTermsWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            circularProgress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            circularProgress.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            circularProgress.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "AdNetworkWebViewClient :: shouldOverrideUrlLoading() | url = " + url);

            if (TextUtils.isEmpty(url)) {
                return false;
            }

            // 웹페이지
            if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }

}
