package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 8/18/2016 AD.
 */
public class AddServiceDao implements Parcelable {

    @SerializedName("result") private int result;
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("id") private String id;

    protected AddServiceDao(Parcel in) {
        result = in.readInt();
        status = in.readString();
        message = in.readString();
        id = in.readString();
    }

    public static final Creator<AddServiceDao> CREATOR = new Creator<AddServiceDao>() {
        @Override
        public AddServiceDao createFromParcel(Parcel in) {
            return new AddServiceDao(in);
        }

        @Override
        public AddServiceDao[] newArray(int size) {
            return new AddServiceDao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeString(status);
        dest.writeString(message);
        dest.writeString(id);
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
