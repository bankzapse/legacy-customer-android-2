package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 2/23/2017 AD.
 */

public class CheckServerDao implements Parcelable{

    @SerializedName("result") private int result;
    @SerializedName("message") private String message;

    protected CheckServerDao(Parcel in) {
        result = in.readInt();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CheckServerDao> CREATOR = new Creator<CheckServerDao>() {
        @Override
        public CheckServerDao createFromParcel(Parcel in) {
            return new CheckServerDao(in);
        }

        @Override
        public CheckServerDao[] newArray(int size) {
            return new CheckServerDao[size];
        }
    };

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
