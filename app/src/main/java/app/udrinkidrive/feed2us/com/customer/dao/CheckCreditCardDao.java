package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 10/11/2016 AD.
 */
public class CheckCreditCardDao implements Parcelable{

    @SerializedName("result") private int result;
    @SerializedName("message") private String message;

    protected CheckCreditCardDao(Parcel in) {
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

    public static final Creator<CheckCreditCardDao> CREATOR = new Creator<CheckCreditCardDao>() {
        @Override
        public CheckCreditCardDao createFromParcel(Parcel in) {
            return new CheckCreditCardDao(in);
        }

        @Override
        public CheckCreditCardDao[] newArray(int size) {
            return new CheckCreditCardDao[size];
        }
    };

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
