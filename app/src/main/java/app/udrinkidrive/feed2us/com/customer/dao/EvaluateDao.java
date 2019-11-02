package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 8/18/2016 AD.
 */
public class EvaluateDao implements Parcelable {

    @SerializedName("result") private int result;
    @SerializedName("message") private String message;
    @SerializedName("status") private String status;

    protected EvaluateDao(Parcel in) {
        result = in.readInt();
        message = in.readString();
        status = in.readString();
    }

    public static final Creator<EvaluateDao> CREATOR = new Creator<EvaluateDao>() {
        @Override
        public EvaluateDao createFromParcel(Parcel in) {
            return new EvaluateDao(in);
        }

        @Override
        public EvaluateDao[] newArray(int size) {
            return new EvaluateDao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeString(message);
        dest.writeString(status);
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
