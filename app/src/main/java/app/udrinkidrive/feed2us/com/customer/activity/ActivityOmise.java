package app.udrinkidrive.feed2us.com.customer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityOmise extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.llBack)
    ImageView llBack;
    WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omise);
        ButterKnife.bind(this);

        EventAction();
    }

    public void EventAction(){
        llBack.setOnClickListener(this);

        mWebView = (WebView) findViewById(R.id.webview_omise);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl("https://customer.udrinkidrive.co.th/member/payment?token="+SPUtils.getString(this, Value.API_KEY));
//        Log.d("Tag","API : "+SPUtils.getString(this, Value.API_KEY));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llBack:
                onBackPressed();
                break;
        }
    }
}
