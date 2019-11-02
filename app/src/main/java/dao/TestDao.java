package dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kong on 11/16/2014.
 */
public class TestDao extends BaseDao {

    @SerializedName("result") private int Result;
    @SerializedName("message") private String message;

    public int getResult() {return Result;}
    public void setResult(int result) {Result = result;}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
