package manager.http;

/**
 * Created by Kong on 10/20/2014.
 */

import com.squareup.okhttp.Call;

import java.util.Map;

public class HTTPRequestData {

    public String url;
    public Map<String, String> postData;
    public Call call;

}
