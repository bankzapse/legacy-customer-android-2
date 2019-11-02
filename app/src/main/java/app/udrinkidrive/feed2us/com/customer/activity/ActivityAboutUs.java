package app.udrinkidrive.feed2us.com.customer.activity;

import android.content.Context;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import app.udrinkidrive.feed2us.com.customer.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class ActivityAboutUs extends FragmentActivity {

    private String TAG = ActivityAboutUs.class.getSimpleName();
    private Context mContext;
    private FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.llBack) ImageView llBack;
    @BindView(R.id.tvAbout) TextView tvAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_aboutus);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("AboutUs", bundle);

        setInitView();
        setInitEvent();
    }

    private void setInitView() {
        mContext = this;

        tvAbout.setMovementMethod(new ScrollingMovementMethod());
        tvAbout.setText(Html.fromHtml(getString(R.string.au_msg)
                        + "<br/><br/>"
                        + getString(R.string.au_msg2)
                        + "<br/><br/>"
                        + getString(R.string.au_msg3)));
    }

    private void setInitEvent() {
        llBack.setOnClickListener(new View.OnClickListener() {
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


