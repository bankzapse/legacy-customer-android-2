package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 2/10/2017 AD.
 */

public class FacebookStatusDao implements Parcelable{

    @SerializedName("result") private int result;
    @SerializedName("message") private String message;

    protected FacebookStatusDao(Parcel in) {
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

    public static final Creator<FacebookStatusDao> CREATOR = new Creator<FacebookStatusDao>() {
        @Override
        public FacebookStatusDao createFromParcel(Parcel in) {
            return new FacebookStatusDao(in);
        }

        @Override
        public FacebookStatusDao[] newArray(int size) {
            return new FacebookStatusDao[size];
        }
    };

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
