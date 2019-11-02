package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
//import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import dao.TestDao;
import io.fabric.sdk.android.Fabric;
import manager.http.HTTPEngine;
import manager.http.HTTPEngineListener;
import app.udrinkidrive.feed2us.com.customer.model.ModelFacebook;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;

public class ActivityProfile extends FragmentActivity {

    private String TAG = ActivityProfile.class.getSimpleName();
    private Context mContext;

    private ProgressDialog dia;
    public static int REQ_CREDIT_CARD = 1;

    private int progressStatus = 0;
    private Handler handler = new Handler();

    public String level_name = "";
    public int star_number = 0;
    public int point_km = 0;
    public int exp = 0;
    private int connected_facebook = 1;
    private int reward_badge = 0;
    UDIDAlert alert;

    public ModelFacebook mFacebook;
    private CallbackManager callbackManager;

    @BindView(R.id.ivUp) ImageView ivUp;
    @BindView(R.id.ivEditAccount) ImageView ivEditAccount;
    @BindView(R.id.tvRank) TextView tvRank;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.userImage) ImageView userImage;
    @BindView(R.id.ivBorder) ImageView ivBorder;
    @BindView(R.id.ivStar1) ImageView ivStar1;
    @BindView(R.id.ivStar2) ImageView ivStar2;
    @BindView(R.id.ivStar3) ImageView ivStar3;
    @BindView(R.id.ivStar4) ImageView ivStar4;
    @BindView(R.id.ivStar5) ImageView ivStar5;
    @BindView(R.id.llNextLv) LinearLayout llNextLv;
    @BindView(R.id.tvReachPoint) TextView tvReachPoint;
    @BindView(R.id.userFullname) TextView userFullname;
    @BindView(R.id.llPayment) LinearLayout llPayment;
    @BindView(R.id.llHistory) LinearLayout llHistory;
    @BindView(R.id.rlReward) RelativeLayout rlReward;
    @BindView(R.id.llAbout) LinearLayout llAbout;
    @BindView(R.id.logout) LinearLayout logout;
    @BindView(R.id.login_button) LoginButton loginButton;
    @BindView(R.id.rlRewardBadge) RelativeLayout rlRewardBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setInitView();
        setInitEvent();
        getCustomerDetail();
    }

    private void setInitView() {
        mContext = this;

        String userName = SPUtils.getString(mContext, Value.FIRSTNAME) +" "+SPUtils.getString(mContext, Value.LASTNAME);
        userFullname.setText(userName);
    }

    private void setInitEvent() {

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                mFacebook = new ModelFacebook();
                mFacebook.facebookId = loginResult.getAccessToken().getUserId();
                mFacebook.facebookToken = loginResult.getAccessToken().getToken();

                facebookConnect(mFacebook.facebookId, mFacebook.facebookToken);
            }

            @Override
            public void onCancel() {
                Utils.showToast("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                Utils.showToast("Login attempt failed.");
            }
        });

        ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, ActivityEditProfile.class);
                startActivity(in);
                finish();
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, ActivityEditProfile.class);
                startActivity(in);
                finish();
            }
        });

        llPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(mContext, ActivityCreditCard.class);
//                startActivityForResult(i, REQ_CREDIT_CARD);
                Intent i = new Intent(mContext, ActivityOmise.class);
                startActivity(i);
            }
        });

        llHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, ActivityHistory.class);
                startActivity(in);
            }
        });

        rlReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlRewardBadge.setVisibility(View.GONE);
                Intent in = new Intent(mContext, ActivityReward.class);
                startActivity(in);
            }
        });

        llAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, ActivityAboutUs.class);
                startActivity(in);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                
                if (SPUtils.getBoolean(mContext, Value.SERVICE)) {
                    alert = new UDIDAlert(ActivityProfile.this, "LOG OUT", getString(R.string.alert_p1), 1);
                    alert.UDIDAlert.show();
                } else {
                    alert = new UDIDAlert(ActivityProfile.this, "LOG OUT", getString(R.string.alert_p2), 2);
                    alert.dBtnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.UDIDAlert.dismiss();
                        }
                    });

                    alert.dBtnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.UDIDAlert.dismiss();
                            Utils.logout(mContext);
                            Intent in = new Intent(mContext, ActivityLogin.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(in);
                        }
                    });
                    alert.UDIDAlert.show();

                }
            }
        });
    }

    private void loadUserImageFromStorage() {
        File f = Utils.loadUserImageFromStorage(ActivityProfile.this);
        if(f != null) {
            Picasso.with(ActivityProfile.this).load(f).noFade().into(userImage);
        }
        else {
            userImage.setImageResource(R.drawable.usericon);
        }
    }

    private void runProgress() {
        final int percent = checkMaxLV();
        final int nextLV = checkNextLV();
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus <= 100) {
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            if(progressBar.getProgress() == percent) {
                                tvReachPoint.setText(nextLV + " km.");
                                llNextLv.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    try {
                        // Sleep for 50 milliseconds.
                        //Just to display the progress slowly
                        Thread.sleep(50);
                        if(progressBar.getProgress() == percent) {
                            break;
                        }
                        else {
                            progressStatus += 1;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private int checkMaxLV() {
        int maxLV = 0;
        int newMaxLV = 0;
        int percent = 0;
        int previousLV = 0;
        if(star_number == 0) {
            maxLV = 125;
            newMaxLV = 125;
        }
        else if(star_number == 1) {
            maxLV = 375;
            newMaxLV = 375 - 125;
            previousLV = 125;
        }
        else if(star_number == 3) {
            maxLV = 1200;
            newMaxLV = 1200 - 375 ;
            previousLV = 375;
        }
        else if(star_number == 5) {
            return 100;
        }

        percent = ((exp - previousLV) * 100) / newMaxLV;
        return percent;
    }

    private int checkNextLV() {
        int maxLV = 0;
        int nextLV = 0;
        if(star_number == 0) {
            maxLV = 125;
        }
        else if(star_number == 1) {
            maxLV = 375;
        }
        else if(star_number == 3) {
            maxLV = 1200;
        }
        else if(star_number == 5) {
            return 0;
        }

        nextLV = maxLV - exp;
        return nextLV;
    }

    private void setCustomerDetail() {

        if(connected_facebook == 0) {
            loginButton.setVisibility(View.VISIBLE);
        }
        else {
            loginButton.setVisibility(View.GONE);
        }

        if(reward_badge == 1) {
            rlRewardBadge.setVisibility(View.VISIBLE);
        }
        else {
            rlRewardBadge.setVisibility(View.GONE);
        }

        if (star_number == 0) {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar1));
            ivBorder.setAlpha(0);
        }
        else if (star_number == 1) {
            ivStar1.setVisibility(View.VISIBLE);
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar1));
            ivBorder.setImageResource(R.drawable.bronze_frame);
            tvRank.setTextColor(getResources().getColor(R.color.rank_bronze));
        }
        else if (star_number == 3) {
            ivStar1.setVisibility(View.VISIBLE);
            ivStar2.setVisibility(View.VISIBLE);
            ivStar3.setVisibility(View.VISIBLE);
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar2));
            ivBorder.setImageResource(R.drawable.silver_frame);
            tvRank.setTextColor(getResources().getColor(R.color.rank_silver));
        }
        else if (star_number == 5) {
            ivStar1.setVisibility(View.VISIBLE);
            ivStar2.setVisibility(View.VISIBLE);
            ivStar3.setVisibility(View.VISIBLE);
            ivStar4.setVisibility(View.VISIBLE);
            ivStar5.setVisibility(View.VISIBLE);
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progressbar3));
            ivBorder.setImageResource(R.drawable.gold_frame);
            tvRank.setTextColor(getResources().getColor(R.color.rank_gold));
        }
        tvRank.setText(level_name);
        tvRank.setVisibility(View.VISIBLE);
        ivBorder.setVisibility(View.VISIBLE);
        runProgress();
    }

    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            try {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/udid_image");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                String name = new Date().toString() + ".jpg";
                myDir = new File(myDir, name);
                FileOutputStream out = new FileOutputStream(myDir);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                SPUtils.set(mContext, Value.IMAGEURI, name);
                loadUserImageFromStorage();
            } catch (Exception e) {

            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private void getCustomerDetail() {
        Map<String, String> param = new HashMap<>();
        param.put("api_token", SPUtils.getString(mContext, Value.API_KEY));
        Log.d("getCustomerDetail",Value.URL_DETAIL);
        Log.d("getCustomerDetail",param.toString());
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {                
                Log.d("getCustomerDetail:r",rawData);
                JSONObject response = new JSONObject(rawData);
                if (response != null) {
                    try {
                        if (response.getString("result").equals("1")) {
                            JSONObject jsonObject = response.getJSONObject("data");
                            level_name = jsonObject.getString("level_name");
                            star_number = jsonObject.getInt("star_number");
                            point_km = jsonObject.getInt("point_km");
                            exp = jsonObject.getInt("exp");
                            connected_facebook = jsonObject.getInt("connected_facebook");
                            reward_badge = jsonObject.getInt("reward_badge");

                            String image_url = response.getString("image_url");
                            SPUtils.set(mContext, Value.IMAGEURL, image_url);
                            Log.d("image_url : ", image_url);
                            userImage.setTag(target);
                            if(!image_url.equals("")){
                                Picasso.with(mContext).load(image_url).noFade().placeholder(R.drawable.usericon).into(target);
                            }

                            setCustomerDetail();

                        } else {
                            alert = new UDIDAlert(ActivityProfile.this, "Profile", response.get("message").toString(), 1);
                            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.UDIDAlert.dismiss();
                                    Utils.logout(mContext);
                                    Intent in = new Intent(mContext, ActivityLogin.class);
                                    startActivity(in);
                                    finish();
                                }
                            });
                            alert.UDIDAlert.show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(TestDao data, String rawData) {                
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityProfile.this);
            }
        }, Value.URL_DETAIL, param);
    }

    public void facebookConnect(final String facebookId, final String facebookToken) {

        dia = new ProgressDialog(ActivityProfile.this);
        dia.setMessage("Connecting..");
        dia.setCancelable(false);
        dia.show();

        Map<String, String> param = new HashMap<String, String>();
        param.put("facebook_id", facebookId);
        param.put("facebook_token", facebookToken);
        param.put("api_token", SPUtils.getString(mContext, Value.API_KEY));
        Log.d("facebookConnect",Value.URL_FACEBOOK_CONNECT);
        Log.d("facebookConnect",param.toString());
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {                
                JSONObject response = new JSONObject(rawData);
                if (response != null) {
                    try {
                        if (response.getString("result").equals("1")) {
                            dia.cancel();
                            alert = new UDIDAlert(ActivityProfile.this, "Profile", response.get("message").toString(), 1);
                            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.UDIDAlert.dismiss();
                                    loginButton.setVisibility(View.GONE);
                                }
                            });
                            alert.UDIDAlert.show();
                        }
                        else {
                            dia.cancel();

                            alert = new UDIDAlert(ActivityProfile.this, "Profile", response.get("message").toString(), 1);
                            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.UDIDAlert.dismiss();
                                    try {
                                        if (AccessToken.getCurrentAccessToken() != null || com.facebook.Profile.getCurrentProfile() != null) {
                                            LoginManager.getInstance().logOut();
                                        }
                                    }catch (Exception e) {

                                    }
                                }
                            });
                            alert.UDIDAlert.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(TestDao data, String rawData) {                
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityProfile.this);
            }
        }, Value.URL_FACEBOOK_CONNECT, param);
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void perform_action(View v)
    {

    }

}


