package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dao.TestDao;
import io.fabric.sdk.android.Fabric;
import manager.http.HTTPEngine;
import manager.http.HTTPEngineListener;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;

public class ActivityChangePass extends FragmentActivity {

    private String TAG = ActivityChangePass.class.getSimpleName();
    private Context mContext;

    private ProgressDialog dia;

    UDIDAlert alert;

    @BindView(R.id.mainLayout) LinearLayout mainLayout;

    @BindView(R.id.llBack) ImageView llBack;
    @BindView(R.id.btConfirm) Button btConfirm;
    @BindView(R.id.btCancel) Button btCancel;

    @BindView(R.id.edtCurrent) EditText edtCurrent;
    @BindView(R.id.edtNew) EditText edtNew;
    @BindView(R.id.edtReNew) EditText edtReNew;

    @OnClick({ R.id.llBack, R.id.btCancel})
    public void close() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_changepass);
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
                hideKeyboard(ActivityChangePass.this);
            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check password and re-password
                if (!(edtNew.getText().toString().equals(edtReNew.getText().toString()))) {
                    alert = new UDIDAlert(ActivityChangePass.this, "Change Password", getString(R.string.alert_cp1), 1);
                    alert.UDIDAlert.show();

                    edtReNew.requestFocus();
                }
                //Check empty
                else if (edtCurrent.getText().length() == 0 || edtNew.getText().length() == 0
                        || edtReNew.getText().length() == 0) {
                    alert = new UDIDAlert(ActivityChangePass.this, "Change Password", getString(R.string.alert_cp2), 1);
                    alert.UDIDAlert.show();

                    edtCurrent.requestFocus();
                    edtNew.requestFocus();
                    edtReNew.requestFocus();
                }
                else {
                    alert = new UDIDAlert(ActivityChangePass.this, "Change Password", getString(R.string.alert_cp3), 2);
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
                            changePass();
                        }
                    });
                    alert.UDIDAlert.show();
                }
            }
        });
    }

    private void changePass() {
        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();
        Map<String, String> param = new HashMap<>();
        param.put("api_token", SPUtils.getString(mContext, Value.API_KEY));
        param.put("current", edtCurrent.getText().toString());
        param.put("new", edtNew.getText().toString());
        Log.d("changePass",Value.URL_CHANGEPASS);
        Log.d("changePass",param.toString());
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {                
                JSONObject response = new JSONObject(rawData);
                if (response != null) {
                    try {
                        if (response.getString("result").equals("1")) {
                            dia.dismiss();
                            alert = new UDIDAlert(ActivityChangePass.this, "Change Password", response.get("message").toString(), 1);
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
                            if (response.getString("result").equals("1")) {
                                alert = new UDIDAlert(ActivityChangePass.this, "Change Password", response.get("message").toString(), 1);
                                alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.UDIDAlert.dismiss();
                                    }
                                });
                                alert.UDIDAlert.show();
                            } else {
                                alert = new UDIDAlert(ActivityChangePass.this, "Change Password", response.get("message").toString(), 1);
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
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(TestDao data, String rawData) {                
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityChangePass.this);
            }
        }, Value.URL_CHANGEPASS, param);
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

    public void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(ActivityChangePass.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


}


