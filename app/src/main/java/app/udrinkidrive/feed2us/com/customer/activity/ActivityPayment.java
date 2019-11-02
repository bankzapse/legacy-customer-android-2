package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.SocketTimeoutException;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.dao.AddCreditCardDao;
import app.udrinkidrive.feed2us.com.customer.service.HttpManager;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPayment extends FragmentActivity {

    private String TAG = ActivityPayment.class.getSimpleName();
    private Context mContext;

    private ProgressDialog dia;

    UDIDAlert alert;

    @BindView(R.id.mainLayout) RelativeLayout mainLayout;

    @BindView(R.id.btAdd) Button btAdd;
    @BindView(R.id.btCancel) Button btCancel;

    @BindView(R.id.firstCreditNum) EditText firstCreditNum;
    @BindView(R.id.secondCreditNum) EditText secondCreditNum;
    @BindView(R.id.thirdCreditNum) EditText thirdCreditNum;
    @BindView(R.id.fourthCreditNum) EditText fourthCreditNum;

    @BindView(R.id.creditStatus) TextView creditStatus;
    @BindView(R.id.holderName) EditText holderName;
    @BindView(R.id.mm) EditText mm;
    @BindView(R.id.yy) EditText yy;
    @BindView(R.id.cardCCV) EditText cardCCV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        setInitView();
        setInitEvent();
    }

    private void setInitView() {
        mContext = this;
    }

    private void setInitEvent() {

        setEdtEvent();

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(ActivityPayment.this);
            }
        });

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValidation()) {
                    addCard();
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private boolean formValidation() {

        // Check Empty And Valid
        if (mm.getText().length() == 0 || yy.getText().length() == 0
                || cardCCV.getText().length() == 0 || holderName.getText().length() == 0
                || creditStatus.getCurrentTextColor() == getResources().getColor(R.color.red)) {
            alert = new UDIDAlert(ActivityPayment.this, "Add Credit Card", getString(R.string.alert_pm1), 1);
            alert.UDIDAlert.show();

            return false;
        }
        else if(firstCreditNum.getText().length() != 4 || secondCreditNum.getText().length() != 4 || thirdCreditNum.getText().length() != 4 || fourthCreditNum.getText().length() != 4) {
            alert = new UDIDAlert(ActivityPayment.this, "Add Credit Card", "หมายเลขบัตรต้องมี 16 หลัก", 1);
            alert.UDIDAlert.show();

            return false;
        }
        else if(Integer.parseInt(mm.getText().toString()) > 12) {
            alert = new UDIDAlert(ActivityPayment.this, "Add Credit Card", getString(R.string.alert_pm2), 1);
            alert.UDIDAlert.show();

            return false;
        }
        else if(!(firstCreditNum.getText().toString().substring(0,1).equals("4")) && (Integer.parseInt(firstCreditNum.getText().toString().substring(0,2)) < 51 || Integer.parseInt(firstCreditNum.getText().toString().substring(0,2)) > 55)) {
            alert = new UDIDAlert(ActivityPayment.this, "Add Credit Card", getString(R.string.alert_pm3), 1);
            alert.UDIDAlert.show();

            return false;
        }

        return true;
    }


    private void setEdtEvent() {

        firstCreditNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(start == 3 && count == 1){
                    secondCreditNum.requestFocus();
                    if(firstCreditNum.getText().length() == 4 && secondCreditNum.getText().length() == 4
                            && thirdCreditNum.getText().length() == 4 && fourthCreditNum.getText().length() == 4) {
                        checkCreditCardNum();
                    }
                }
                if(start == 3 && count == 0){
                    creditStatus.setText("");
                    creditStatus.setTextColor(getResources().getColor(R.color.red));
                }
            }

        });

        secondCreditNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 3 && count == 1) {
                    thirdCreditNum.requestFocus();
                    if(firstCreditNum.getText().length() == 4 && secondCreditNum.getText().length() == 4
                            && thirdCreditNum.getText().length() == 4 && fourthCreditNum.getText().length() == 4) {
                        checkCreditCardNum();
                    }
                }
                if (start == 0 && count == 0) {
                    firstCreditNum.requestFocus();
                }
                if (start == 3 && count == 0) {
                    creditStatus.setText("");
                    creditStatus.setTextColor(getResources().getColor(R.color.red));
                }
            }

        });

        thirdCreditNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 3 && count == 1) {
                    fourthCreditNum.requestFocus();
                    if (firstCreditNum.getText().length() == 4 && secondCreditNum.getText().length() == 4
                            && thirdCreditNum.getText().length() == 4 && fourthCreditNum.getText().length() == 4) {
                        checkCreditCardNum();
                    }
                }
                if (s.length() == 0 && count == 0) {
                    secondCreditNum.requestFocus();
                }
                if (start == 3 && count == 0) {
                    creditStatus.setText("");
                    creditStatus.setTextColor(getResources().getColor(R.color.red));
                }
            }

        });

        fourthCreditNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 3 && count == 1) {
                    if (firstCreditNum.getText().length() == 4 && secondCreditNum.getText().length() == 4
                            && thirdCreditNum.getText().length() == 4 && fourthCreditNum.getText().length() == 4) {
                        checkCreditCardNum();
                    }
                }
                if (start == 0 && count == 0) {
                    thirdCreditNum.requestFocus();
                }
                if (start == 3 && count == 0) {
                    creditStatus.setText("");
                    creditStatus.setTextColor(getResources().getColor(R.color.red));
                }
            }

        });

        mm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 1 && count == 1) {
                    yy.requestFocus();
                }
            }

        });

        yy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0 && count == 0) {
                    mm.requestFocus();
                }
            }

        });
    }

    private void checkCreditCardNum() {

        String creditNum = String.format(firstCreditNum.getText().toString() + secondCreditNum.getText().toString()
                            + thirdCreditNum.getText().toString() + fourthCreditNum.getText().toString());
        int result = 0;
        for(int i = creditNum.length() - 1 ; i >= 0 ; i-- ) {
            int temp = Integer.parseInt(creditNum.substring(i,i+1));
            if(i % 2 == 0) {
                temp = temp * 2;
                if(temp > 9) {
                    temp = (temp/10)+(temp%10);
                }
            }
            result += temp;
        }
        if(result%10==0)
        {
            creditStatus.setText(R.string.pm_status);
            creditStatus.setTextColor(getResources().getColor(R.color.green));
            holderName.requestFocus();
        }
        else
        {
            creditStatus.setText(R.string.pm_status2);
            creditStatus.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void addCard() {
        final String creditNum = String.format(firstCreditNum.getText().toString() + secondCreditNum.getText().toString()
                + thirdCreditNum.getText().toString() + fourthCreditNum.getText().toString());
        Call<AddCreditCardDao> call = HttpManager.getInstance().getService().addCard(
          SPUtils.getString(mContext, Value.API_KEY),
          holderName.getText().toString(),
          creditNum,
          mm.getText().toString(),
          String.format("20" + yy.getText().toString()),
          cardCCV.getText().toString()
        );
        call.enqueue(new Callback<AddCreditCardDao>() {
            @Override
            public void onResponse(Call<AddCreditCardDao> call, Response<AddCreditCardDao> response) {
                if(response.isSuccessful()){
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo add card : "+jsonFromPojo);
                    AddCreditCardDao dao = response.body();
                    if(dao.getResult() == 1){

                        Intent in = new Intent(mContext, ActivityCreditCard.class);
                        startActivity(in);
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        alert = new UDIDAlert(ActivityPayment.this, "Add credit card", dao.getMessage(), 1);
                        alert.UDIDAlert.show();
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
            public void onFailure(Call<AddCreditCardDao> call, Throwable t) {
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
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(ActivityPayment.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}


