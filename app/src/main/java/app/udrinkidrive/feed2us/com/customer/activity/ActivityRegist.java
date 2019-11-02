package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.dao.UserCollectionDao;
import app.udrinkidrive.feed2us.com.customer.dao.UserDao;
import app.udrinkidrive.feed2us.com.customer.service.HttpManager;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import dao.RegistDao;
import manager.http.HTTPEngine;
import manager.http.HTTPEngineListener;
import app.udrinkidrive.feed2us.com.customer.model.ModelFacebook;
import app.udrinkidrive.feed2us.com.customer.util.FilePickUtils;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityRegist extends FragmentActivity implements DatePickerDialog.OnDateSetListener {

    private String TAG = ActivityRegist.class.getSimpleName();
    private Context mContext;

    private static TextView mDisplay;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ProgressDialog dia;
    private String gcmId;
    private static GoogleCloudMessaging gcm;
    private static String SENDER_ID = "355505641123";
    private String regId;

    private String gender_id = "";
    private static final int FEMALE = 1;
    private static final int MALE = 2;

    private int hasImage = 0;
    private File imgFromPicker;
    private int PICK_IMAGE_REQUEST = 66;

    public ModelFacebook mFacebook;

    private Date selectDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");

    private String selectedImagePath = "";

    UDIDAlert alert;

    private CallbackManager callbackManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.mainLayout) LinearLayout mainLayout;
    @BindView(R.id.login_button) LoginButton loginButton;
    @BindView(R.id.ivProfile) ImageView ivProfile;
    @BindView(R.id.btnMale) Button btnMale;
    @BindView(R.id.btnFemale) Button btnFemale;
    @BindView(R.id.edtFirstname) EditText edtFirstname;
    @BindView(R.id.edtLastname) EditText edtLastname;
    @BindView(R.id.edtEmail) EditText edtEmail;
    @BindView(R.id.edtPassword) EditText edtPassword;
    @BindView(R.id.edtPhone) EditText edtPhone;
    @BindView(R.id.tvBirthday) TextView tvBirthday;
    @BindView(R.id.llDone) LinearLayout llDone;
    @BindView(R.id.ivClose) ImageView ivClose;

    @BindView(R.id.tvTerm) TextView tvTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setInitView();
        setInitEvent();

        Intent i = getIntent();
        int req = i.getIntExtra("requestCode", 0);

        if (req == 2) {
            mFacebook = i.getParcelableExtra("mFacebook");
            setFacebookInfo();
        }
    }

    private void setInitView() {
        mContext = this;
    }

    private void setFacebookInfo() {
        if (mFacebook != null) {
            edtFirstname.setText(mFacebook.firstName);
            edtLastname.setText(mFacebook.lastName);
            edtEmail.setText(mFacebook.email);
            tvBirthday.setText(mFacebook.birthday);

            setGenderId(mFacebook.genderId);
            setBirthday();

            Picasso.with(ActivityRegist.this).load("https://graph.facebook.com/" + mFacebook.facebookId + "/picture?type=large").into(ivProfile);
            hasImage = 1;
        }
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
                selectedImagePath = root + "/udid_image/" + name;
                new UploadFileAsync().execute(selectedImagePath, Value.URL_UPLOAD);
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

    private void saveImageFromUrlToStorage() {
        ivProfile.setTag(target);
        Picasso.with(mContext).load("https://graph.facebook.com/" + mFacebook.facebookId + "/picture?type=large").into(target);
    }

    private void setInitEvent() {

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityRegist.this != null) {
                    hideKeyboard(ActivityRegist.this);
                }
            }
        });

        llDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValidation()) {
                    alert = new UDIDAlert(ActivityRegist.this, getString(R.string.alert_r1), "", 2);
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
                            register();
                        }
                    });
                    alert.UDIDAlert.show();
                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() != null || com.facebook.Profile.getCurrentProfile() != null) {
                    LoginManager.getInstance().logOut();
                }
                Intent i = new Intent(mContext, ActivityLogin.class);
                startActivity(i);
                finish();
            }
        });

        tvTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, ActivityTermsConditions.class);
                startActivity(in);
            }
        });

        btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGenderId(2);
            }
        });

        btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGenderId(1);
            }
        });

        tvBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ActivityRegist.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.dismissOnPause(true);
                dpd.setAccentColor(Color.parseColor("#00AEFF"));
                dpd.setMaxDate(now);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT >= 19) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                mFacebook = new ModelFacebook();
                mFacebook.facebookId = loginResult.getAccessToken().getUserId();
                mFacebook.facebookToken = loginResult.getAccessToken().getToken();

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

                                    getFacebookStatus(mFacebook.facebookId, mFacebook.facebookToken);
                                    //setFacebookInfo();

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
            public void onError(FacebookException error) {
                Utils.showToast("Login attempt failed.");
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
    }

    private void getFacebookStatus(String facebook_id, String facebook_token) {
        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();

        regId = getGCM_RegId();        
        SPUtils.set(ActivityRegist.this, Value.GCMID, regId);

        Call<UserCollectionDao> call = HttpManager.getInstance().getService().facebookLogin(facebook_id, facebook_token, regId, "");
        call.enqueue(new Callback<UserCollectionDao>() {
            @Override
            public void onResponse(Call<UserCollectionDao> call, Response<UserCollectionDao> response) {
                dia.dismiss();
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo Register : "+jsonFromPojo);
                    UserCollectionDao dao = response.body();
                    if(dao.getResult() == 1) {
                        List<UserDao> user = dao.getData();
                        if(user != null) {
                            saveUserData(user);
                        }

                        SPUtils.set(ActivityRegist.this, Value.IS_LOGIN, true);
                        //Intent in = new Intent(mContext, ActivityHome.class);
                        Intent in = new Intent(mContext, MainActivity.class);
                        startActivity(in);
                        finish();
                    }
                    else {
                        setFacebookInfo();
                    }
                }
                else {
                    try {
                        Utils.showToast(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserCollectionDao> call, Throwable t) {
                dia.dismiss();
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                }
                else  {
                    Utils.showToast(t.toString());
                }
            }
        });
    }

    private void register() {
        dia = new ProgressDialog(this);
        dia.setMessage("Register..");
        dia.setCancelable(false);
        dia.show();

        regId = getGCM_RegId();        
        SPUtils.set(ActivityRegist.this, Value.GCMID, regId);

        Bundle bundle = new Bundle();
        Map<String, String> param = new HashMap<String, String>();
        param.put("firstname", edtFirstname.getText().toString());
        param.put("lastname", edtLastname.getText().toString());
        param.put("gender", gender_id);
        param.put("email", edtEmail.getText().toString());
        param.put("password", edtPassword.getText().toString());
        param.put("mobileno", edtPhone.getText().toString());
        param.put("gcm_id", regId);

        bundle.putString("firstname", edtFirstname.getText().toString());
        bundle.putString("lastname",edtLastname.getText().toString());
        bundle.putString("gender", gender_id);
        bundle.putString("email", edtEmail.getText().toString());
        bundle.putString("mobile_number",edtPhone.getText().toString());
        if(mFacebook != null) {
            param.put("facebook_id", mFacebook.facebookId);
            param.put("facebook_token", mFacebook.facebookToken);
            param.put("verify", mFacebook.verified);
        }
        if(selectDate != null) {
            param.put("birthday", sdfd.format(selectDate));
            bundle.putString("birthday", sdfd.format(selectDate));
        }

        mFirebaseAnalytics.logEvent("registeration", bundle);

        Log.d("register:R",Value.URL_REGISTER);
        Log.d("register:R", param.toString());
        HTTPEngine.getInstance().registerMember(new HTTPEngineListener<RegistDao>() {
            @Override
            public void onResponse(RegistDao data, String rawData) throws JSONException {                
//                Log.d("register:r",data.toString());
                Log.d("register:r",rawData);
                JSONObject response = new JSONObject(rawData);
                Gson gsonBuilder = new GsonBuilder().create();
                String jsonFromPojo = gsonBuilder.toJson(response);
                Log.d("TagD","jsonFromPojo register : "+jsonFromPojo);
                if (response != null) {
                    try {
                        if (response.getString("result").equals("1")) {
                            //dia.dismiss();                            
                            SPUtils.set(mContext, Value.FIRSTNAME, edtFirstname.getText().toString());
                            SPUtils.set(mContext, Value.LASTNAME, edtLastname.getText().toString());
                            SPUtils.set(mContext, Value.EMAIL, edtEmail.getText().toString());
                            SPUtils.set(mContext, Value.GENDER, gender_id);
                            SPUtils.set(mContext, Value.MOBILENO, edtPhone.getText().toString());
                            SPUtils.set(mContext, Value.API_KEY, response.getString("api_token"));

                            if(hasImage == 1) {
                                saveImageFromUrlToStorage();
                            }
                            else if(hasImage == 2){
                                ivProfile.setTag(target);
                                Picasso.with(mContext).load(imgFromPicker).into(target);
                            }
                            else {
                                alert = new UDIDAlert(ActivityRegist.this, "Register", response.get("message").toString(), 1);
                                alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.UDIDAlert.dismiss();
                                        SPUtils.set(ActivityRegist.this, Value.IS_LOGIN, true);
                                        Intent in = new Intent(mContext, MainActivity.class);
                                        startActivity(in);
                                        finish();
                                    }
                                });
                                alert.UDIDAlert.show();
                            }

                        } else {
                            dia.dismiss();
                            alert = new UDIDAlert(ActivityRegist.this, "Register", response.get("message").toString(), 1);
                            alert.UDIDAlert.show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(RegistDao data, String rawData) {                
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityRegist.this);
            }
        }, Value.URL_REGISTER, param);
    }

    private boolean formValidation() {

        // Check Empty
        if (edtEmail.getText().length() == 0 || edtFirstname.getText().length() == 0
                || edtLastname.getText().length() == 0 || edtPassword.getText().length() == 0
                || edtPhone.getText().length() == 0 ) {

            alert = new UDIDAlert(ActivityRegist.this, "Register", getString(R.string.alert_r3), 1);
            edtEmail.requestFocus();
            edtFirstname.requestFocus();
            edtLastname.requestFocus();
            edtPhone.requestFocus();
            alert.UDIDAlert.show();

            return false;
        }

        // Check E-mail
        if (!Utils.validateEmailAddress(edtEmail.getText().toString())) {
            alert = new UDIDAlert(ActivityRegist.this, "Register", getString(R.string.alert_r4), 1);
            edtEmail.requestFocus();
            alert.UDIDAlert.show();

            return false;
        }
        //Check password
        if (edtPassword.getText().length() < 4 || edtPassword.getText().length() > 16) {
            alert = new UDIDAlert(ActivityRegist.this, "Register", getString(R.string.alert_r6), 1);
            edtPassword.requestFocus();
            alert.UDIDAlert.show();

            return false;
        }
        //Check MobileNo
        if ((edtPhone.getText().length() < 10 || edtPhone.getText().length() > 11)
               ||
             (edtPhone.getText().length() == 10 && !edtPhone.getText().toString().substring(0, 1).equals("0"))
            ){
            alert = new UDIDAlert(ActivityRegist.this, "Register", getString(R.string.alert_r7), 1);
            edtPhone.requestFocus();
            alert.UDIDAlert.show();

            return false;
        }

        return true;
    }

    private void setGenderId(int gender) {
        if(gender == MALE) {
            gender_id = "2";
            btnMale.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey500));
            btnFemale.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey300));
        }
        else if(gender == FEMALE) {
            gender_id = "1";
            btnMale.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey300));
            btnFemale.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey500));
        }
        else {
            gender_id = "";
            btnMale.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey300));
            btnFemale.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey300));
        }
    }

    private void setBirthday() {

        if(!tvBirthday.getText().equals("") && tvBirthday.getText().length() != 0) {
            try {
                selectDate = sdf.parse(tvBirthday.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getGCM_RegId() {
        //GCM Registration
        if (SPUtils.getString(mContext, Value.GCMID) != null && SPUtils.getString(mContext, Value.GCMID) != "") {
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

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        Log.i(TAG, "checkPlayServices# ConnectionResult : " + ConnectionResult.SUCCESS);
        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (ActivityLogin) mContext,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

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

    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return mContext.getSharedPreferences(ActivityRegist.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

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

    private static void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (AccessToken.getCurrentAccessToken() != null || com.facebook.Profile.getCurrentProfile() != null){
                LoginManager.getInstance().logOut();
            }
            Intent i = new Intent(ActivityRegist.this, ActivityLogin.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        tvBirthday.setText(date);
        try {
            selectDate = sdf.parse(tvBirthday.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null && !uri.toString().isEmpty()) {
                if(Build.VERSION.SDK_INT >= 19){
                    final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    //noinspection ResourceType
                    ActivityRegist.this.getContentResolver()
                            .takePersistableUriPermission(uri, takeFlags);
                }
                String filePath = FilePickUtils.getSmartFilePath(ActivityRegist.this, uri);                
                imgFromPicker = new File(filePath);
                Picasso.with(ActivityRegist.this).load(imgFromPicker).noFade().into(ivProfile);
                hasImage = 2;
            }
        }
        else if(requestCode == PICK_IMAGE_REQUEST && resultCode != RESULT_OK) {

        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(ActivityRegist.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    public class UploadFileAsync extends AsyncTask<String, Void, Void> {

        String resServer;

        @SuppressWarnings("deprecation")
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            int resCode = 0;
            String resMessage = "";

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            String strSDPath = params[0];
            String strUrlServer = params[1];

            try {
                /** Check file on SD Card ***/
                File file = new File(strSDPath);

                if (!file.exists()) {                    
                    resServer = "{\"StatusID\":\"0\",\"Error\":\"Please check path on SD Card\"}";
                    return null;
                }

                FileInputStream fileInputStream = new FileInputStream(new File(strSDPath));

                URL url = new URL(strUrlServer);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"upload\";filename=\""
                        + strSDPath + "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                outputStream.writeBytes(lineEnd);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"api_token\"" + lineEnd + lineEnd);
                outputStream.writeBytes(SPUtils.getString(mContext, Value.API_KEY));
                outputStream.writeBytes(lineEnd);

                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Response Code and Message
                resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    int read = 0;
                    while ((read = is.read()) != -1) {
                        bos.write(read);
                    }
                    byte[] result = bos.toByteArray();
                    bos.close();

                    resMessage = new String(result);

                    resServer = resMessage.toString();

                    JSONObject response = new JSONObject(resServer);
                    
                    if (response.get("result").toString().equals("1")) {
                        if (selectedImagePath != null && selectedImagePath != "") {                            
                            dia.dismiss();
                        }

                    } else {
                        // Loaded but has some error
                        dia.dismiss();
                    }
                }

                Log.d("resCode=", Integer.toString(resCode));

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();


            } catch (Exception ex) {
                // Exception handling
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            alert = new UDIDAlert(ActivityRegist.this, "Register", "Register Successfully", 1);
            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.UDIDAlert.dismiss();
                    SPUtils.set(ActivityRegist.this, Value.IS_LOGIN, true);
                    //Intent in = new Intent(mContext, ActivityHome.class);
                    Intent in = new Intent(mContext, MainActivity.class);
                    startActivity(in);
                    finish();
                }
            });
            alert.UDIDAlert.show();
        }
    }
}