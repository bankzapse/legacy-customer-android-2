package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TL3 on 6/15/2016 AD.
 */
public class UserCollectionDao implements Parcelable {

    @SerializedName("result") private int result;
    @SerializedName("data") private List<UserDao> data;
    @SerializedName("message") private String message;
    @SerializedName("isOtpVerified") private Boolean isOtpVerified;

    protected UserCollectionDao(Parcel in) {
        result = in.readInt();
        data = in.createTypedArrayList(UserDao.CREATOR);
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeTypedList(data);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserCollectionDao> CREATOR = new Creator<UserCollectionDao>() {
        @Override
        public UserCollectionDao createFromParcel(Parcel in) {
            return new UserCollectionDao(in);
        }

        @Override
        public UserCollectionDao[] newArray(int size) {
            return new UserCollectionDao[size];
        }
    };

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<UserDao> getData() {
        return data;
    }

    public void setData(List<UserDao> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsOtpVerified() {
        return isOtpVerified;
    }

    public void setIsOtpVerified(Boolean isOtpVerified) {
        this.isOtpVerified = isOtpVerified;
    }
}
