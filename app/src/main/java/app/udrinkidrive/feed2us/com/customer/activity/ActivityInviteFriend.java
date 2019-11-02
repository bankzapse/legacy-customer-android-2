package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

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
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;

public class ActivityInviteFriend extends FragmentActivity {

    private String TAG = ActivityInviteFriend.class.getSimpleName();
    private Context mContext;

    private Boolean inEditMode = false;
    private String _referralCode = "";
    private String _discount = "";

    private ProgressDialog dia;
    private FirebaseAnalytics mFirebaseAnalytics;
    UDIDAlert alert;
    ShareDialog shareDialog;

    @BindView(R.id.edtReferralCode)
    EditText edtReferralCode;
    @BindView(R.id.ibEditReferralCode)
    ImageButton ibEditReferralCode;
    @BindView(R.id.tvDiscount)
    TextView tvDiscount;
    @BindView(R.id.tvCredit)
    TextView tvCredit;

    @BindView(R.id.mainlayout)
    LinearLayout mainlayout;
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.btnShareFacebook)
    Button btnShareFacebook;
    @BindView(R.id.btnShareOther)
    Button btnShareOther;
    @BindView(R.id.btnCopy)
    Button btnCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_invite_friend);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setInitView();
        setInitEvent();
        loadReferralData();
    }

    private void setInitView() {
        mContext = this;

        shareDialog = new ShareDialog(this);
        edtReferralCode.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        edtReferralCode.setEnabled(false);
    }

    private void setInitEvent() {

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityInviteFriend.this != null) {
                    hideKeyboard(ActivityInviteFriend.this);
                }
            }
        });

        ibEditReferralCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inEditMode) {
                    inEditMode = true;
                    edtReferralCode.setEnabled(true);
                    edtReferralCode.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(ActivityInviteFriend.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(edtReferralCode, 1);
                    ibEditReferralCode.setImageResource(R.drawable.referral_check);
                } else {
                    inEditMode = false;
                    edtReferralCode.setEnabled(false);

                    if (!edtReferralCode.getText().toString().equals(_referralCode)) {
                        editMyReferralCode();
                    }
                    ibEditReferralCode.setImageResource(R.drawable.referral_edit);
                }
            }
        });

        edtReferralCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    inEditMode = false;
                    edtReferralCode.setEnabled(false);

                    if (!edtReferralCode.getText().toString().equals(_referralCode)) {
                        editMyReferralCode();
                    }
                    ibEditReferralCode.setImageResource(R.drawable.referral_edit);

                    return true;
                }
                return false;
            }
        });

        edtReferralCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    edtReferralCode.setText(s);
                    int textLength = edtReferralCode.getText().length();
                    edtReferralCode.setSelection(textLength, textLength);
                }
            }

        });

        btnShareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("referral_code", Value.URL_REFERRAL_DATA);
                mFirebaseAnalytics.logEvent("share_to_facebook", bundle);
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("มาร่วมกันใช้ยูดริ้งค์ไอไดรฟ์เพื่อสังคมที่ปลอดภัย")
                            .setContentDescription(
                                    "ใช้โค้ด " + edtReferralCode.getText().toString() + " เพื่อรับส่วนลด " + _discount + " บาท")
                            .setContentUrl(Uri.parse("https://bnc.lt/download-udrink"))
                            .setImageUrl(Uri.parse("http://www.mx7.com/i/5a2/gbpqp5.png"))
                            .build();

                    shareDialog.show(linkContent);
                }
            }
        });

        btnShareOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean installed = appInstalledOrNot("jp.naver.line.android");
                Bundle bundle = new Bundle();
                bundle.putString("referral_code", Value.URL_REFERRAL_DATA);
                mFirebaseAnalytics.logEvent("share_message", bundle);
                if (installed) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "มาร่วมลดอุบัติเหตุให้สังคม ด้วยการใช้บริการยูดริ้งค์ไอไดรฟ์ http://www.udrinkidrive.co.th/download ใช้โค้ด " + edtReferralCode.getText().toString() + " เพื่อรับส่วนลด " + _discount + " บาท");
                    intent.setType("text/plain");
                    intent.setPackage("jp.naver.line.android");
                    startActivity(intent);
                } else {
                    Utils.showToast("Line is not installed.");
                }
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("referral_code", Value.URL_REFERRAL_DATA);
                mFirebaseAnalytics.logEvent("copy_code", bundle);

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Referral Code", edtReferralCode.getText().toString());
                clipboard.setPrimaryClip(clip);

                Utils.showToast("Copied to clipboard.");
            }
        });
    }

    private void loadReferralData() {
        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();
        Map<String, String> param = new HashMap<>();
        param.put("api_token", SPUtils.getString(mContext, Value.API_KEY));
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {
                JSONObject response = new JSONObject(rawData);
                if (response != null) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response);
                    Log.d("TagD","jsonFromPojo invite friend : "+jsonFromPojo);
                    try {
                        if (response.getString("result").equals("1")) {
                            dia.dismiss();

                            _referralCode = response.getString("code");
                            _discount = response.getString("discount");

                            edtReferralCode.setText(_referralCode);
                            tvDiscount.setText("คุณและเพื่อนจะได้รับส่วนลด " + _discount + " บาท");
                            tvCredit.setText("คุณมียอดสะสม " + response.getString("credit")+ " บาท");
                        } else {
                            dia.dismiss();
                            alert = new UDIDAlert(ActivityInviteFriend.this, "Referral Code", response.get("message").toString(), 1);
                            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.UDIDAlert.dismiss();
                                    edtReferralCode.setText("");
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
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityInviteFriend.this);
            }
        }, Value.URL_REFERRAL_DATA, param);
    }

    private void editMyReferralCode() {
        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();
        Map<String, String> param = new HashMap<>();
        param.put("api_token", SPUtils.getString(mContext, Value.API_KEY));
        param.put("code", edtReferralCode.getText().toString());
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {
                JSONObject response = new JSONObject(rawData);
                if (response != null) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response);
                    Log.d("TagD","jsonFromPojo edit invite friend : "+jsonFromPojo);
                    try {
                        if (response.getString("result").equals("1")) {
                            dia.dismiss();

                            _referralCode = edtReferralCode.getText().toString();
                            alert = new UDIDAlert(ActivityInviteFriend.this, "Referral Code", response.get("message").toString(), 1);
                            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.UDIDAlert.dismiss();
                                }
                            });
                            alert.UDIDAlert.show();
                        } else {
                            if (response.getString("result").equals("1")) {
                                dia.dismiss();

                                edtReferralCode.setText(_referralCode);
                                alert = new UDIDAlert(ActivityInviteFriend.this, "Referral Code", response.get("message").toString(), 1);
                                alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.UDIDAlert.dismiss();
                                    }
                                });
                                alert.UDIDAlert.show();
                            } else {
                                dia.dismiss();
                                alert = new UDIDAlert(ActivityInviteFriend.this, "Referral Code", response.get("message").toString(), 1);
                                alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.UDIDAlert.dismiss();
                                        edtReferralCode.setText(_referralCode);
                                    }
                                });
                                alert.UDIDAlert.show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(TestDao data, String rawData) {
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityInviteFriend.this);
            }
        }, Value.URL_REFERRAL_EDIT, param);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = ActivityInviteFriend.this.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
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

    public void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(ActivityInviteFriend.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

}


