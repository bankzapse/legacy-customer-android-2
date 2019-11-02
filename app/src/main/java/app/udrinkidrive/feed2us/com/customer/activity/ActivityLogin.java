package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jsoup.Jsoup;

import java.util.concurrent.ExecutionException;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.dao.CheckServerDao;
import app.udrinkidrive.feed2us.com.customer.service.HttpManager;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import io.fabric.sdk.android.Fabric;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLogin extends FragmentActivity {

    private Context mContext;

    public static int REQ_REGISTER = 1;
    private static final int INITIAL_REQUEST=1337;
    private static final int CAMERA_REQUEST=INITIAL_REQUEST+1;
    private static final int PHONE_REQUEST=INITIAL_REQUEST+2;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;

    private ProgressDialog progressDia;
    UDIDAlert alert;

    private FirebaseAnalytics mFirebaseAnalytics;

    String mLatestVersionName;

    @BindView(R.id.btRegister) Button btRegister;
    @BindView(R.id.btSignin) Button btSignin;
//    @BindView(R.id.splashImageView) ImageView splashImageView;
    @BindView(R.id.lv_box_button) LinearLayout lv_box_button;
    @BindView(R.id.lv_loading) LinearLayout lv_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("app_launch", bundle);

        runSplashPage();
        setInitView();
        setInitEvent();

        if (ActivityCompat.checkSelfPermission((Activity)mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission((Activity)mContext,
                        android.Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions((Activity)mContext, new String[]{
                    android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_REQUEST);
        }

        String major,major_stroe,minor_p1,minor_stroep1,minor_p2,minor_stroep2;
        String versionApplition = getAppVersion(getApplicationContext());
        String[] separated = versionApplition.split("\\.");
        major = separated[0];
        minor_p1 = separated[1];
        minor_p2 = separated[2];
        Log.d("Tag","versionApplition Name: "+versionApplition);

        GetVersionCode versionChecker = new GetVersionCode();
        try {
            mLatestVersionName = versionChecker.execute().get();
//            mLatestVersionName = "2.1.0";
            String[] separated_mLatestVersionName = mLatestVersionName.split("\\.");
            major_stroe = separated_mLatestVersionName[0];
            minor_stroep1 = separated_mLatestVersionName[1];
            minor_stroep2 = separated_mLatestVersionName[2];
            Log.d("Tag","mLatestVersionName : "+mLatestVersionName);
            final String appPackageName = "app.udrinkidive.feed2us.com.customer";
            //if(Integer.parseInt(major_stroe) > Integer.parseInt(major))
            //if(!major.equalsIgnoreCase(major_stroe))
            Log.d("Tag","minor_stroep2 : "+minor_stroep2);
            Log.d("Tag","minor_p2 : "+minor_p2);
            if(Integer.parseInt(major_stroe) > Integer.parseInt(major)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("คุณต้องทำการอัพเดทเวอร์ชั่น ?");
                builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            finish();
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            finish();
                        }
                    }
                });
                builder.setCancelable(false);
                builder.show();
                //if(Integer.parseInt(minor_stroep1) > Integer.parseInt(minor_p1) || Integer.parseInt(minor_stroep2) > Integer.parseInt(minor_p2))
                //if(!minor_p1.equalsIgnoreCase(minor_stroep1) || !minor_p2.equalsIgnoreCase(minor_stroep2))
            }else if(Integer.parseInt(minor_stroep1) > Integer.parseInt(minor_p1) || Integer.parseInt(minor_stroep2) > Integer.parseInt(minor_p2)){
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("คุณต้องทำการอัพเดทเวอร์ชั่น ?");
                builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            finish();
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            finish();
                        }
                    }
                });

                builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

     }

    public void CheckMinorCodition(){

    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "app.udrinkidive.feed2us.com.customer" + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }

        }
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static String getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void setInitView() {
        mContext = this;
    }

    private void setInitEvent() {

        btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                mFirebaseAnalytics.logEvent("sign_in", bundle);
                    if (Utils.isNetworkAvailable(getApplicationContext())) {
                        Intent i = new Intent(mContext, ActivitySignin.class);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    else {
                        Utils.snackbarAlertNetwork(mContext, ActivityLogin.this);
                    }
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                mFirebaseAnalytics.logEvent("register", bundle);
                    if (Utils.isNetworkAvailable(getApplicationContext())) {
                        Intent i = new Intent(mContext, ActivityRegist.class);
                        i.putExtra("requestCode", REQ_REGISTER);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        Utils.snackbarAlertNetwork(mContext, ActivityLogin.this);
                    }                
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void runSplashPage() {

//        lv_box_button.setVisibility(View.GONE);
        lv_loading.setVisibility(View.VISIBLE);
        checkServerStatus();
    }

    private void checkServerStatus() {

        progressDia = new ProgressDialog(ActivityLogin.this);
        progressDia.setMessage("Loading..");
        progressDia.setCancelable(false);
        progressDia.show();

        Call<CheckServerDao> call = HttpManager.getInstance().getService().checkServerStatus();
        call.enqueue(new Callback<CheckServerDao>() {
            @Override
            public void onResponse(Call<CheckServerDao> call, Response<CheckServerDao> response) {
                progressDia.dismiss();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //if (Utils.isNetworkAvailable(getApplicationContext())) {
                        // ไม่ได้loginเอาไว้
                        if(!SPUtils.getBoolean(mContext, "GUIDE")) {
                            startActivity(new Intent(mContext, GuideActivity.class));
                            finish();
                        }
                        else {
                            if (!SPUtils.getBoolean(mContext, Value.IS_LOGIN)) {
                                Animation fade_out = AnimationUtils.loadAnimation(getApplicationContext(),
                                        R.anim.fade_out);
                                fade_out.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        lv_loading.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                lv_loading.startAnimation(fade_out);
                            }
                            else {
                                if(!SPUtils.getBoolean(mContext, Value.SERVICE)) {
                                    //startActivity(new Intent(mContext, ActivityHome.class));
                                    startActivity(new Intent(mContext, MainActivity.class));
                                    finish();
                                }
                                else {
                                    startActivity(new Intent(mContext, ActivityDriver.class));
                                    finish();
                                }
                            }
                        }
                    }
                }, 1000);
            }

            @Override
            public void onFailure(Call<CheckServerDao> call, Throwable t) {
                progressDia.dismiss();
                alert = new UDIDAlert(ActivityLogin.this, "ANNOUNCEMENT", "ขออภัยในความไม่สะดวก ปิดปรับปรุงระบบชั่วคราว กรุณาลองใหม่อีกครั้งในภายหลัง", 1);
                alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.UDIDAlert.dismiss();
                        finish();
                    }
                });
                alert.UDIDAlert.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                    // params will be empty if no data found
                    // ... insert custom logic here ...
                } else {
                    Log.i("MyApp", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }
}


