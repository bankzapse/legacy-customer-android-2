package app.udrinkidrive.feed2us.com.customer.activity;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.fragment.GuideFragment;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Value;


public class GuideActivity extends AppCompatActivity implements GuideFragment.FragmentListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("guide_page", bundle);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, GuideFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onSkipClicked() {
        SPUtils.set(GuideActivity.this, "GUIDE", true);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("skip", bundle);
        if (!SPUtils.getBoolean(GuideActivity.this, Value.IS_LOGIN)) {
            Intent intent = new Intent(GuideActivity.this, ActivityLogin.class);
            startActivity(intent);
            finish();
        }
        else {
            if(!SPUtils.getBoolean(GuideActivity.this, Value.SERVICE)) {
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                finish();
            }
            else {
                startActivity(new Intent(GuideActivity.this, ActivityDriver.class));
                finish();
            }
        }

    }
}
