package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.dao.EvaluateDao;
import app.udrinkidrive.feed2us.com.customer.service.HttpManager;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import app.udrinkidrive.feed2us.com.customer.model.ModelDriver;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityEvaluate extends FragmentActivity {

    private String TAG = ActivityEvaluate.class.getSimpleName();
    private Context mContext;

    private String Remark = "";

    private ProgressDialog proDia;
    //Dialog
    private Dialog dia;
    private Button btnEvaluate;
    private EditText edtEvaluate;
    private ImageView remark1, remark2, remark3, remark4, remark5, remark6;
    private ArrayList<String> complaint = new ArrayList<String>();
    private float cus_rating = -1;

    private ModelDriver modelDriver;

    private SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy HH.mm");

    private int tip = 0;
    private boolean isClickDone = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    UDIDAlert alert;

    @BindView(R.id.tvPickupTime) TextView tvPickupTime;
    @BindView(R.id.tvPrice) TextView tvPrice;
    @BindView(R.id.tvPriceFinal) TextView tvPriceFinal;
    @BindView(R.id.driver_image) ImageView driver_image;
    @BindView(R.id.tvDriverName) TextView tvDriverName;
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @BindView(R.id.rlTip) RelativeLayout rlTip;
    @BindView(R.id.tvTip) TextView tvTip;
    @BindView(R.id.btnDecrease) ImageView btnDecrease;
    @BindView(R.id.btnIncrease) ImageView btnIncrease;
    @BindView(R.id.btDone) Button btDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        OneSignal.clearOneSignalNotifications();
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_evaluate);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setInitView();
        setInitEvent();
    }

    private void setInitView() {
        mContext = this;

        Intent in = getIntent();
        String pickup_time = in.getStringExtra("PICKUPTIME");
        String price = in.getStringExtra("PRICE");
        String price_final = in.getStringExtra("PRICEFINAL");
        modelDriver = (ModelDriver) in.getSerializableExtra("DRIVERINFO");
        String payment_type = in.getStringExtra("PAYMENT");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(pickup_time);
            tvPickupTime.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String formattedPrice = new DecimalFormat("##,### THB").format(Double.parseDouble(price));
        String formattedPriceFinal = new DecimalFormat("##,###.## THB").format(Double.parseDouble(price_final));

        tvPrice.setText(formattedPriceFinal);
        tvPriceFinal.setText("ESTIMATED PRICE : " + formattedPrice);
        if(!modelDriver.image_url.equals("")) {
            try {
                Picasso.with(mContext).load(modelDriver.image_url).noFade().into(driver_image);
            }catch (Exception e){

            }
        }
        tvDriverName.setText(modelDriver.first_name + " " + modelDriver.last_name);

        if(payment_type.equals("Cash")) {
            rlTip.setVisibility(View.GONE);
        }
        btnDecrease.setEnabled(false);
    }

    private void setInitEvent() {
        //if rating value is changed,
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                cus_rating = rating;
                complaint.clear();
                Remark = "";
                if(rating < 3.5) {
                    dialogEvaluate();
                }
                else {
                    //sendEvaluate();
                }
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClickDone) {
                    isClickDone = true;
                    if (cus_rating != -1) {
                        sendEvaluate();
                    } else {
                        isClickDone = false;
                        alert = new UDIDAlert(ActivityEvaluate.this, "Evaluate", "Please tap on stars to rate your driver", 1);
                        alert.UDIDAlert.show();
                    }
                }
            }
        });

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tip <= 500) {
                    tip -= 50;
                    if(tip == 0) {
                        btnDecrease.setEnabled(false);
                        btnDecrease.setImageResource(R.drawable.decrease);
                    }
                }
                else {
                    tip -= 100;
                }
                btnIncrease.setEnabled(true);
                btnIncrease.setImageResource(R.drawable.increase_active);
                tvTip.setText(String.valueOf(tip) + "   " + "THB");
            }
        });

        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tip >= 500) {
                    tip += 100;
                    if(tip == 1000) {
                        btnIncrease.setEnabled(false);
                        btnIncrease.setImageResource(R.drawable.increase);
                    }
                }
                else {
                    tip += 50;
                }
                btnDecrease.setEnabled(true);
                btnDecrease.setImageResource(R.drawable.decrease_active);
                tvTip.setText(String.valueOf(tip) + "   " + "THB");
            }
        });
    }

    private void dialogEvaluate() {

        dia = new Dialog(this);
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dia.setContentView(R.layout.dialog_evaluate);
        //hide keyboard when dialog show
        dia.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        btnEvaluate = (Button) dia.findViewById(R.id.btn_evaluate);
        updateDialogeEvaluate();
        btnEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEvaluate.getText().length() == 0 && complaint.size() == 0) {
                    Utils.showToast("คุณต้องเลือกอย่างน้อย 1 คำตอบ");
                }
                else {
                    Remark = complaint.toString().replace("[", "").replace("]", "").replace(" ","");
                    Remark = String.format(Remark + "," + edtEvaluate.getText().toString());                    
                    //sendEvaluate();
                    dia.dismiss();
                }
            }
        });
        dia.setCancelable(false);
        dia.show();
    }

    private void updateDialogeEvaluate() {

        remark1 = (ImageView) dia.findViewById(R.id.eva_1);
        remark2 = (ImageView) dia.findViewById(R.id.eva_2);
        remark3 = (ImageView) dia.findViewById(R.id.eva_3);
        remark4 = (ImageView) dia.findViewById(R.id.eva_4);
        remark5 = (ImageView) dia.findViewById(R.id.eva_5);
        remark6 = (ImageView) dia.findViewById(R.id.eva_6);
        edtEvaluate = (EditText) dia.findViewById(R.id.edt_evaluate);

        remark1.setOnClickListener(new View.OnClickListener() {
            private Boolean value1 = false;

            @Override
            public void onClick(View v) {
                if (value1 == false) {
                    remark1.setImageResource(R.drawable.eva_1_active);
                    complaint.add("มารยาท");
                    value1 = true;
                } else {
                    remark1.setImageResource(R.drawable.eva_1);
                    complaint.remove("มารยาท");
                    value1 = false;
                }

            }
        });

        remark2.setOnClickListener(new View.OnClickListener() {
            private Boolean value2 = false;

            @Override
            public void onClick(View v) {
                if (value2 == false) {
                    remark2.setImageResource(R.drawable.eva_2_active);
                    complaint.add("ความชำนาญทาง");
                    value2 = true;
                } else {
                    remark2.setImageResource(R.drawable.eva_2);
                    complaint.remove("ความชำนาญทาง");
                    value2 = false;
                }

            }
        });

        remark3.setOnClickListener(new View.OnClickListener() {
            private Boolean value3 = false;

            @Override
            public void onClick(View v) {
                if (value3 == false) {
                    remark3.setImageResource(R.drawable.eva_3_active);
                    complaint.add("ทักษะการขับขี่");
                    value3 = true;
                } else {
                    remark3.setImageResource(R.drawable.eva_3);
                    complaint.remove("ทักษะการขับขี่");
                    value3 = false;
                }

            }
        });

        remark4.setOnClickListener(new View.OnClickListener() {
            private Boolean value4 = false;

            @Override
            public void onClick(View v) {
                if (value4 == false) {
                    remark4.setImageResource(R.drawable.eva_4_active);
                    complaint.add("ความชำนาญปุ๋มเกี่ยร์");
                    value4 = true;
                } else {
                    remark4.setImageResource(R.drawable.eva_4);
                    complaint.remove("ความชำนาญปุ๋มเกี่ยร์");
                    value4 = false;
                }

            }
        });

        remark5.setOnClickListener(new View.OnClickListener() {
            private Boolean value5 = false;

            @Override
            public void onClick(View v) {
                if (value5 == false) {
                    remark5.setImageResource(R.drawable.eva_5_active);
                    complaint.add("การตรงต่อเวลา");
                    value5 = true;
                } else {
                    remark5.setImageResource(R.drawable.eva_5);
                    complaint.remove("การตรงต่อเวลา");
                    value5 = false;
                }

            }
        });

        remark6.setOnClickListener(new View.OnClickListener() {
            private Boolean value6 = false;

            @Override
            public void onClick(View v) {
                if (value6 == false) {
                    remark6.setImageResource(R.drawable.eva_6_active);
                    complaint.add("การแต่งกาย");
                    value6 = true;

                } else {
                    remark6.setImageResource(R.drawable.eva_6);
                    complaint.remove("การแต่งกาย");
                    value6 = false;
                }

            }
        });
    }

    private void sendEvaluate() {
        SPUtils.set(ActivityEvaluate.this, Value.SERVICE, false);
        proDia = new ProgressDialog(this);
        proDia.setMessage("Loading..");
        proDia.setCancelable(false);
        proDia.show();
        isClickDone = false;

        Bundle bundle = new Bundle();
        bundle.putString("rating", String.valueOf(ratingBar.getRating()));
        bundle.putString("tip", String.valueOf(tip));
        mFirebaseAnalytics.logEvent("evaluation", bundle);
        Call<EvaluateDao> call = HttpManager.getInstance().getService().evaluateDriver(SPUtils.getString(ActivityEvaluate.this, Value.TRIP_ID),SPUtils.getString(ActivityEvaluate.this, Value.API_KEY), (int)ratingBar.getRating(), Remark);
        call.enqueue(new Callback<EvaluateDao>() {
            @Override
            public void onResponse(Call<EvaluateDao> call, Response<EvaluateDao> response) {
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo rating driver : "+jsonFromPojo);
                    EvaluateDao dao = response.body();
                    if(dao.getResult() == 1) {
                        proDia.dismiss();
                        alert = new UDIDAlert(ActivityEvaluate.this, "UDrink IDrive", getString(R.string.alert_ev1), 1);
                        alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alert.UDIDAlert.dismiss();
                                startActivity(new Intent(mContext, MainActivity.class));
                                finish();
                            }
                        });
                        alert.UDIDAlert.show();
                    }
                    else {
                        proDia.dismiss();
                        if(dao.getStatus().equals("0")) {
                            alert = new UDIDAlert(ActivityEvaluate.this, "Evaluate", dao.getMessage(), 1);
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
            public void onFailure(Call<EvaluateDao> call, Throwable t) {
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                }
                else  {
                    Utils.showToast(t.toString());
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


}


