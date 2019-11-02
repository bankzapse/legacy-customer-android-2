package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.chaos.view.PinView;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.dao.UserCollectionDao;
import app.udrinkidrive.feed2us.com.customer.service.HttpManagerVerifyOTP;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityOTP extends FragmentActivity {

    SmsVerifyCatcher smsVerifyCatcher;
    PinView firstPinView;
    ProgressDialog dia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        firstPinView = findViewById(R.id.firstPinView);

        firstPinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 4){
                    VerifyOTP(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
//                String code = parseCode(message);//Parse verification code
//                etCode.setText(code);//set code in edit text
                //then you can send verification code to server
                firstPinView.setText(message);
            }
        });
    }

    public void VerifyOTP(String otp) {

        dia = new ProgressDialog(this);
        dia.setMessage("ระบบกำลังตรวจสอบ..");
        dia.setCancelable(false);
        dia.show();

        String mobile = SPUtils.getString(this, Value.MOBILENO);
        String api_key = SPUtils.getString(this, Value.API_KEY);
        Log.d("Tag","mobile : "+mobile);
        Log.d("Tag","api_key : "+api_key);
        Log.d("Tag","otp : "+otp);

        Call<UserCollectionDao> call = HttpManagerVerifyOTP.getInstance().getService().VerifyOTP(mobile, api_key, otp);

        call.enqueue(new Callback<UserCollectionDao>() {
            @Override
            public void onResponse(Call<UserCollectionDao> call, Response<UserCollectionDao> response) {
                if(response.isSuccessful()) {
                    dia.cancel();
                    Log.d("Tag","response : "+response.body().getResult());
                    if(response.body().getResult() == 1){
                        SPUtils.set(ActivityOTP.this, Value.IS_LOGIN, true);
                        Intent in = new Intent(ActivityOTP.this, MainActivity.class);
                        startActivity(in);
                        finish();
                    }
                }
                else {
                    dia.cancel();
                    Log.d("Tag","response : "+response.code());
                }
            }

            @Override
            public void onFailure(Call<UserCollectionDao> call, Throwable t) {
                dia.cancel();
                Log.d("Tag","call : "+call.toString());
                Log.d("Tag","getMessage : "+t.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
