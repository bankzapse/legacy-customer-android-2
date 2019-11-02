package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 9/26/2016 AD.
 */
public class DelFavLocationDao implements Parcelable {

    @SerializedName("result") private int result;
    @SerializedName("message") private String message;
    @SerializedName("status") private String status;

    protected DelFavLocationDao(Parcel in) {
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

    public static final Creator<DelFavLocationDao> CREATOR = new Creator<DelFavLocationDao>() {
        @Override
        public DelFavLocationDao createFromParcel(Parcel in) {
            return new DelFavLocationDao(in);
        }

        @Override
        public DelFavLocationDao[] newArray(int size) {
            return new DelFavLocationDao[size];
        }
    };

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
