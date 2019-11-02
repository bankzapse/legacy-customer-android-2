package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.adapter.HistoryListAdapter;
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

public class ActivityHistory extends AppCompatActivity {

    private String TAG = ActivityHistory.class.getSimpleName();
    private Context mContext;

    private ProgressDialog dia;

    private ArrayList<HashMap<String, String>> historyList = new ArrayList<HashMap<String,String>>();

    UDIDAlert alert;

    @BindView(R.id.llBack) ImageView llBack;
    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        setInitView();
        setInitEvent();
    }

    private void setInitView() {
        mContext = this;
        getHistoryData();
    }

    private void setInitEvent() {

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupCustomListView() {
        if(historyList != null) {
            listView.setAdapter(new HistoryListAdapter(historyList));
        }
    }

    private void getHistoryData() {
        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();
        historyList.clear();
        Map<String, String> param = new HashMap<>();
        param.put("api_token", SPUtils.getString(mContext, Value.API_KEY));
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {
                JSONObject response = new JSONObject(rawData);
                if (response != null) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response);
                    Log.d("TagD","jsonFromPojo history : "+jsonFromPojo);
                    try {
                        if (response.getString("result").equals("1")) {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject j = jsonArray.getJSONObject(i);
                                HashMap<String, String> hashMap = new HashMap<String, String>();
                                hashMap.put("pickup_time", j.getString("pickup_time"));
                                hashMap.put("source", j.getString("source"));
                                hashMap.put("destination", j.getString("destination"));
                                hashMap.put("price", j.getString("price"));
                                historyList.add(hashMap);
                            }
                            setupCustomListView();
                            dia.dismiss();
                        }
                        else {
                            dia.dismiss();
                            alert = new UDIDAlert(ActivityHistory.this, "Reservation History", response.get("message").toString(), 1);
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
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityHistory.this);
            }
        }, Value.URL_SERVICE_HISTORY, param);
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


