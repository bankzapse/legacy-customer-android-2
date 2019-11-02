package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
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
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;

public class ActivityForgot extends FragmentActivity {

    private String TAG = ActivityForgot.class.getSimpleName();
    private Context mContext;

    private ProgressDialog dia;

    UDIDAlert alert;

    @BindView(R.id.mainLayout) LinearLayout mainLayout;

    @BindView(R.id.edt_email) EditText edtEmail;
    @BindView(R.id.btConfirm) Button btConfirm;
    @BindView(R.id.ivClose) ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forgot);
        ButterKnife.bind(this);

        setInitView();
        setInitEvent();
    }

    private void setInitView() {
        mContext = this;
    }

    private void setInitEvent() {

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(ActivityForgot.this);
            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValidation()) {
                    getPass();
                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void getPass() {
        dia = new ProgressDialog(mContext);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();
        Map<String,String> param = new HashMap<>();
        param.put("email", edtEmail.getText().toString());
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {                
                JSONObject response = new JSONObject(rawData);
                Gson gsonBuilder = new GsonBuilder().create();
                String jsonFromPojo = gsonBuilder.toJson(response);
                Log.d("TagD","jsonFromPojo for got : "+jsonFromPojo);
                if (response != null) {
                    try {
                        if (response.getString("result").equals("1")) {
                            alert = new UDIDAlert(ActivityForgot.this, "Forgot Password", response.get("message").toString(), 1);
                            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.UDIDAlert.dismiss();
                                    finish();
                                }
                            });
                            alert.UDIDAlert.show();
                        } else {
                            dia.dismiss();
                            alert = new UDIDAlert(ActivityForgot.this, "Forgot Password", response.getString("message"), 1);
                            alert.UDIDAlert.show();
                            edtEmail.requestFocus();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    // Loaded but has some error
                }
            }

            @Override
            public void onFailure(TestDao data, String rawData) {
                // Cannot load                
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityForgot.this);
            }
        }, Value.URL_FORGOT, param);
    }

    private boolean formValidation() {

        // Check Empty
        if (edtEmail.getText().length() == 0) {
            alert = new UDIDAlert(ActivityForgot.this, "Forgot Password", getString(R.string.alert_fp1), 1);
            alert.UDIDAlert.show();
            edtEmail.requestFocus();

            return false;
        }
        // Check E-mail
        if (!Utils.validateEmailAddress(edtEmail.getText().toString())) {
            alert = new UDIDAlert(ActivityForgot.this, "Forgot Password", getString(R.string.alert_fp2), 1);
            alert.UDIDAlert.show();
            edtEmail.requestFocus();

            return false;
        }

        return true;
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
        if (dia != null) {
            dia.dismiss();
        }
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(ActivityForgot.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}


