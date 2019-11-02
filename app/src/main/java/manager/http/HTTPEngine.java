package manager.http;

import android.content.Context;
import android.net.ConnectivityManager;
//import android.support.multidex.MultiDex;
//import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dao.BaseDao;
import dao.RegistDao;
import dao.TestDao;


/**
 * Created by Kong on 11/16/2014.
 */
public class HTTPEngine extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private String TAG = HTTPEngine.class.getSimpleName();
    private static HTTPEngine instance;
    private ConnectivityManager connMgr;

    public static HTTPEngine getInstance() {
        if (instance == null)
            instance = new HTTPEngine();
        return instance;
    }

    private Context mContext;
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    public HTTPEngine() {
    }

    public void init(Context context) {
        mContext = context;
        connMgr = (ConnectivityManager)  mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
    }


    private Map<String, String> getBaseData() {
        Map<String, String> postParams = new HashMap<String, String>();
        //postParams.put("device_id", Utils.getInstance().getDeviceId());
        //postParams.put("version", Utils.getInstance().getVersionName());
        return postParams;
    }

    public <T extends BaseDao> Call loadPostUrl(String url, Map<String, String> postData, final HTTPEngineListener<T> listener, final Class<T> tClass) {
        Map<String, String> postParams = getBaseData();

        if (postData != null) {
            for (Map.Entry<String, String> entry : postData.entrySet()) {
                postParams.put(entry.getKey(), entry.getValue());
            }
        }

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, mapToPostString(postParams));

        Request  request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Basic cGtleV81MGg1Z2JiZ3pjZDB1bmZka3ptOg==")
                    .post(body)
                    .build();

        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);

        Call call = client.newCall(request);

        HTTPRequestData data = new HTTPRequestData();
        data.url = url;
        data.postData = postData;
        data.call = call;

            new HTTPRequestTask(new HTTPRequestListener() {
                @Override
                public void onMessageReceived(String message) {                    
                    if (listener != null) {
                        String resp = message;                        
                        try {
                            T data = gson.fromJson(resp, tClass);
                            listener.onResponse(data, resp);
                        } catch (Exception e) {
                            e.printStackTrace();
                            T data = null;
                            try {
                                data = tClass.newInstance();
                                data.setSuccess(false);
                                data.setReason("Cannot parse JSON");
                            } catch (Exception e2) {
                            }
                            try {
                                listener.onFailure(data, resp);
                            } catch (Exception e2) {
                                listener.onFailure(data, "");
                            }
                        }
                    }

                }

                @Override
                public void onMessageError(int statusCode, String message) {                    
                    if (listener != null) {
                        T data = null;
                        try {
                            //data = tClass.newInstance();
                            //data.setSuccess(false);
                            //data.setReason(statusCode + "");
                            listener.onResponse(data, message);
                        } catch (Exception e2) {

                        }
                    }
                }
            }).multithreadExecute(data);
        //} else{
            //MainActivity.loseConnection();
        //}

        return call;
    }


    private String mapToPostString(Map<String, String> data) {
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (content.length() > 0) {
                content.append('&');
            }
            try {
                content.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append('=')
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }
        }
        return content.toString();
    }

    public void loseConnection(){
        Toast.makeText(mContext, "คุณไม่ได้ต่อ อินเตอร์เนต", Toast.LENGTH_LONG);
        //Intent connection = new Intent(mContext, ConnectionActivity.class);
        //connection.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        //mContext.startActivity(connection);
    }

    //////////////////////////MEMBER/////////////////////////
    public Call registerMember(final HTTPEngineListener<RegistDao> listener, final String url, final Map<String, String> postData) {        
        return loadPostUrl(url, postData, listener, RegistDao.class);
    }

    public Call loadData(final HTTPEngineListener<TestDao> listener,String url,Map<String,String> postData) {
        //Map<String, String> postData = new HashMap<String, String>();
        return loadPostUrl(url, postData, listener, TestDao.class);
    }

}
