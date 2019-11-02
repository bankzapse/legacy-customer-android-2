package app.udrinkidrive.feed2us.com.customer.service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import app.udrinkidrive.feed2us.com.customer.service.http.ApiService;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpManagerVerifyOTP {

    private static HttpManagerVerifyOTP instance;

    public static HttpManagerVerifyOTP getInstance() {
        if (instance == null)
            instance = new HttpManagerVerifyOTP();
        return instance;
    }

    private Context mContext;
    private ApiService service;

    private HttpManagerVerifyOTP() {
        mContext = Contextor.getInstance().getContext();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(35, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();


        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Value.HOST_verify_otp)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = retrofit.create(ApiService.class);
    }

    public ApiService getService() {
        return service;
    }
}
