package dao;

/**
 * Created by Kong on 11/7/14 AD.
 */

import com.google.gson.annotations.SerializedName;


public class RegistDao extends BaseDao{

    @SerializedName("result") private int Result;
    @SerializedName("message") private String Message;
    @SerializedName("api_token") public String API_TOKEN;

    public int getResult() {return Result;}
    public void setResult(int result) {Result = result;}

    public String getMessage() {return Message;}
    public void setMessage(String message) {Message = message;}

    public String getAPI_TOKEN() {return API_TOKEN;}
    public void setAPI_TOKEN(String api_token) {API_TOKEN = api_token;}

}
