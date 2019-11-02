package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 9/23/2016 AD.
 */
public class AddFavLocationDao implements Parcelable {

    @SerializedName("result") private int result;
    @SerializedName("message") private String message;
    @SerializedName("status") private String status;

    protected AddFavLocationDao(Parcel in) {
        result = in.readInt();
        message = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeString(message);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AddFavLocationDao> CREATOR = new Creator<AddFavLocationDao>() {
        @Override
        public AddFavLocationDao createFromParcel(Parcel in) {
            return new AddFavLocationDao(in);
        }

        @Override
        public AddFavLocationDao[] newArray(int size) {
            return new AddFavLocationDao[size];
        }
    };

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
