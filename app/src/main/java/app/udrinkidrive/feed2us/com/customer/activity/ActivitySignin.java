package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.dao.HaveServiceDao;
import app.udrinkidrive.feed2us.com.customer.dao.UserCollectionDao;
import app.udrinkidrive.feed2us.com.customer.dao.UserDao;
import app.udrinkidrive.feed2us.com.customer.service.HttpManager;
import app.udrinkidrive.feed2us.com.customer.service.HttpManagerVerifyOTP;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import app.udrinkidrive.feed2us.com.customer.model.ModelFacebook;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySignin extends FragmentActivity {
    private String TAG = ActivitySignin.class.getSimpleName();
    private Context mContext;

    private String gcmId;
    private ProgressDialog dia;

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private static TextView mDisplay;
    private static String SENDER_ID = "355505641123";
    private static GoogleCloudMessaging gcm;
    private static Boolean check_otp;

    UDIDAlert alert;

    // facebook login
    public static int REQ_FACEBOOK_REGISTER = 2;
    public ModelFacebook mFacebook;

    private CallbackManager callbackManager;

    @BindView(R.id.mainLayout) RelativeLayout mainLayout;

    @BindView(R.id.edt_username) EditText edtUsername;
    @BindView(R.id.edt_password) EditText edtPassword;
    @BindView(R.id.llDone) LinearLayout llDone;
    @BindView(R.id.ivClose) ImageView ivClose;

    @BindView(R.id.login_button) LoginButton loginButton;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("sign_in_page", bundle);

        setInitView();
        setInitEvent();
        getGCM_RegId();
    }

    private void setInitView() {
        mContext    = this;

        edtUsername.setText(SPUtils.getString(mContext, Value.USERNAME));
    }

    private void setInitEvent() {

        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                mFacebook = new ModelFacebook();
                mFacebook.facebookId = loginResult.getAccessToken().getUserId();
                mFacebook.facebookToken = loginResult.getAccessToken().getToken();

                dia = new ProgressDialog(ActivitySignin.this);
                dia.setMessage("กำลังเข้าสู่ระบบ..");
                dia.setCancelable(false);
                dia.show();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    mFacebook.firstName = object.getString("first_name");
                                    mFacebook.lastName = object.getString("last_name");
                                    if (object.has("gender")) {
                                        if (object.getString("gender").equals("male") || object.getString("gender").equals("ชาย")) {
                                            mFacebook.genderId = 2;
                                        } else {
                                            mFacebook.genderId = 1;
                                        }
                                    }
                                    if (object.has("email")) {
                                        mFacebook.email = object.getString("email");
                                    }
                                    if (object.has("birthday")) {
                                        String[] separated = object.getString("birthday").split("/");
                                        mFacebook.birthday = separated[1] + "/" + separated[0] + "/" + separated[2];
                                    }
                                    if (object.has("verified")) {
                                        if (object.getString("verified").equals("true")) {
                                            mFacebook.verified = "1";
                                        } else {
                                            mFacebook.verified = "0";
                                        }
                                    }

                                    facebookLogin(mFacebook.facebookId, mFacebook.facebookToken, mFacebook.email);

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,email,gender,birthday,picture,verified");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Utils.showToast("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("Tag","e : "+e.getMessage());
                Utils.showToast("Login attempt failed.");
            }
        });

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(ActivitySignin.this);
            }
        });

        llDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValidation()) {
                    login(edtUsername.getText().toString(), edtPassword.getText().toString());
                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ActivityLogin.class);
                startActivity(i);
                finish();
            }
        });


        if (SPUtils.getBoolean(mContext, Value.IS_LOGIN)) {
            startActivity(new Intent(ActivitySignin.this, MainActivity.class));
            finish();
        }
    }

    public void facebookLogin(final String facebookId, final String facebookToken, final String email) {

        String regId = SPUtils.getString(mContext, Value.GCMID);
        Log.d("logfacebook",regId);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("sign_facebook", bundle);

        Log.d("Tag","facebookId : "+facebookId);
        Log.d("Tag","facebookToken : "+facebookToken);
        Log.d("Tag","regId : "+regId);
        Log.d("Tag","email : "+email);

        Call<UserCollectionDao> call = HttpManager.getInstance().getService().facebookLogin(facebookId, facebookToken, regId, email);
//        Call<UserCollectionDao> call = HttpManagerVerifyOTP.getInstance().getService().facebookMockLogin(facebookId, facebookToken, regId, email);
        call.enqueue(new Callback<UserCollectionDao>() {
            @Override
            public void onResponse(Call<UserCollectionDao> call, Response<UserCollectionDao> response) {
                if(response.isSuccessful()) {
                    dia.cancel();

                    check_otp = response.body().getIsOtpVerified();
                    Log.d("Tag","check_otp : "+check_otp);
                    Log.d("Tag","code : "+response.code());

                    UserCollectionDao dao = response.body();
                    Log.d("Tag","getResult : "+dao.getResult());
                    if(dao.getResult() == 1) {
                        List<UserDao> user = dao.getData();

                        if(user != null) {
                            saveUserData(user);
                        }
                        checkHaveThis();
                    }
                    else {

                        Intent i = new Intent(mContext, ActivityRegist.class);
                        i.putExtra("requestCode", REQ_FACEBOOK_REGISTER);
                        i.putExtra("mFacebook", mFacebook);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
                else {
                    dia.cancel();
                    try {
                        Utils.showToast(response.errorBody().string());
                        Log.d("Tag","errorBody : "+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserCollectionDao> call, Throwable t) {
                dia.cancel();
                Utils.showToast(t.toString());
                Log.d("Tag","onFailure : "+t.toString());
            }
        });
    }

    public void login(String username, String password) {

        dia = new ProgressDialog(this);
        dia.setMessage("กำลังเข้าสู่ระบบ..");
        dia.setCancelable(false);
        dia.show();
        String regId = SPUtils.getString(mContext, Value.GCMID);

        Call<UserCollectionDao> call = HttpManager.getInstance().getService().login(username, password, regId);
//        Call<UserCollectionDao> call = HttpManagerVerifyOTP.getInstance().getService().loginMockLogin(username, password, regId);

        call.enqueue(new Callback<UserCollectionDao>() {
            @Override
            public void onResponse(Call<UserCollectionDao> call, Response<UserCollectionDao> response) {
                if(response.isSuccessful()) {
                    dia.cancel();

//                    check_otp = response.body().getIsOtpVerified();

                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo Login : "+jsonFromPojo);

                    UserCollectionDao dao = response.body();
                    if(dao.getResult() == 1) {
                        List<UserDao> user = dao.getData();
                        if(user != null) {
                            saveUserData(user);
                        }
                        checkHaveThis();

                    }
                    else {

                        alert = new UDIDAlert(ActivitySignin.this, "Login", dao.getMessage(), 1);
                        alert.UDIDAlert.show();

                        edtUsername.requestFocus();
                        edtPassword.requestFocus();
                    }

                    Log.d("Tag","Check otp verify : "+response.body().getIsOtpVerified());
                }
                else {
                    dia.cancel();
                    try {
                        Utils.showToast(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserCollectionDao> call, Throwable t) {
                dia.cancel();
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                }
                else  {
                    Utils.showToast(t.toString());
                }
            }
        });
    }

    public void checkHaveThis() {
        String api_key = SPUtils.getString(mContext,Value.API_KEY);

        Call<HaveServiceDao> call = HttpManager.getInstance().getService().checkHaveService(api_key);
        call.enqueue(new Callback<HaveServiceDao>() {
            @Override
            public void onResponse(Call<HaveServiceDao> call, Response<HaveServiceDao> response) {
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo have service : "+jsonFromPojo);
                    dia.cancel();
                    HaveServiceDao dao = response.body();
                    if(dao.getCount_card() > 0) {
                        if(dao.getCard_active() != -1) {
                            SPUtils.set(mContext, Value.CARD, dao.getCard_active());
                            SPUtils.set(mContext, "PAYMENT", "**** " + dao.getCard().get(dao.getCard_active()).getCard_num());
                        }
                        else {
                            SPUtils.set(ActivitySignin.this, "PAYMENT", "Cash");
                        }
                    }
                    else {
                        SPUtils.set(ActivitySignin.this, "PAYMENT", "Cash");
                    }

                    if(dao.getResult() == 1) {
                        SPUtils.set(ActivitySignin.this, Value.IS_LOGIN, true);
                        SPUtils.set(ActivitySignin.this, Value.SERVICE, true);
                        Intent in = new Intent(mContext, ActivityDriver.class);
                        startActivity(in);
                        finish();
                    }
                    else {
                        SPUtils.set(ActivitySignin.this, Value.IS_LOGIN, true);
                        Intent in = new Intent(mContext, MainActivity.class);
                        startActivity(in);
                        finish();
                        //Check OTP Service
//                        if(check_otp){
//                            SPUtils.set(ActivitySignin.this, Value.IS_LOGIN, true);
//                            Intent in = new Intent(mContext, MainActivity.class);
//                            startActivity(in);
//                            finish();
//
//                        }else{
//                            Intent in = new Intent(mContext, ActivityOTP.class);
//                            startActivity(in);
//                        }
                        hideKeyboard(ActivitySignin.this);

                    }
                }
                else {
                    dia.cancel();
                    try {
                        Utils.showToast(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<HaveServiceDao> call, Throwable t) {
                dia.cancel();
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                }
                else  {
                    Utils.showToast(t.toString());
                }
            }
        });
    }

    private void saveUserData(List<UserDao> user) {

        SPUtils.set(mContext, Value.FIRSTNAME, user.get(0).getFirstName());
        SPUtils.set(mContext, Value.LASTNAME, user.get(0).getLastName());
        SPUtils.set(mContext, Value.EMAIL, user.get(0).getEmail());
        SPUtils.set(mContext, Value.GENDER, user.get(0).getSexId());
        SPUtils.set(mContext, Value.MOBILENO, user.get(0).getPhone());
        SPUtils.set(mContext, Value.API_KEY, user.get(0).getApi_token());

        SPUtils.set(mContext, Value.USERNAME, edtUsername.getText().toString());
    }

    public String getGCM_RegId() {
        //GCM Registration
        if (SPUtils.getString(mContext, Value.GCMID) != null && !SPUtils.getString(mContext, Value.GCMID).equals("")) {
            gcmId = SPUtils.getString(mContext, Value.GCMID);

        } else if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(mContext);
            gcmId = getRegistrationId(mContext);            
            if (gcmId.isEmpty()) {
                registerInBackground();
            }

        } else {
            gcmId = "";
        }
        return gcmId;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        Log.i(TAG, "checkPlayServices# ConnectionResult : " + ConnectionResult.SUCCESS);
        Log.i(TAG, "checkPlayServices# resultCode : " + resultCode);
        if (resultCode != ConnectionResult.SUCCESS) {
            if(GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                showErrorDialog(resultCode);
            }
            else {
                Utils.showToast("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    void showErrorDialog(int code) {
        GoogleApiAvailability.getInstance().getErrorDialog(this, code,
                REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SPUtils.set(ActivitySignin.this, Value.GCMID, regId);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {

                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(mContext);
                    }
                    gcmId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + gcmId;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(mContext, gcmId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (mDisplay != null)
                    mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return mContext.getSharedPreferences(ActivitySignin.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private static void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    public String getGCM() {
        return this.gcmId;
    }

    public void setGCM(String gcmId) {
        this.gcmId = gcmId;
    }

    /**
     * @desc Check GCM Registeration ID
     */
    private void checkGCM() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            gcmId = getRegistrationId(mContext);            
            if (gcmId.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    private boolean formValidation() {

        // Check Empty
        if (edtUsername.getText().length() == 0 || edtPassword.getText().length() < 4 || edtPassword.getText().length() > 16) {
            alert = new UDIDAlert(ActivitySignin.this, "Login", getString(R.string.alert_r6), 1);
            alert.UDIDAlert.show();

            edtUsername.requestFocus();
            edtPassword.requestFocus();
            return false;
        }

        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent i = new Intent(ActivitySignin.this, ActivityLogin.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dia != null) {
            dia.dismiss();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if (resultCode == RESULT_CANCELED) {
                    Utils.showToast("Google Play Services must be installed.");
                    finish();
                }
                return;
            default:callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void perform_action(View v)
    {
        Intent i = new Intent(mContext, ActivityForgot.class);
        startActivity(i);
    }

    public void hideKeyboard(Activity activity) {
//        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(ActivitySignin.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
