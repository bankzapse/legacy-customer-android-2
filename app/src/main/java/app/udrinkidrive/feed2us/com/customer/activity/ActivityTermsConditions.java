package app.udrinkidrive.feed2us.com.customer.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;

import app.udrinkidrive.feed2us.com.customer.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class ActivityTermsConditions extends FragmentActivity {

    private String TAG = ActivityTermsConditions.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.ivClose) ImageView ivClose;
    @BindView(R.id.webview) WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_termsconditions);
        ButterKnife.bind(this);

        setInitView();
        setInitEvent();
    }

    private void setInitView() {
        mContext = this;

        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(getString(R.string.policy));
    }

    private void setInitEvent() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}


