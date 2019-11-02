package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 10/11/2016 AD.
 */
public class DelCreditCardDao implements Parcelable{

    @SerializedName("result") private int result;
    @SerializedName("message") private String message;

    protected DelCreditCardDao(Parcel in) {
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

    public static final Creator<DelCreditCardDao> CREATOR = new Creator<DelCreditCardDao>() {
        @Override
        public DelCreditCardDao createFromParcel(Parcel in) {
            return new DelCreditCardDao(in);
        }

        @Override
        public DelCreditCardDao[] newArray(int size) {
            return new DelCreditCardDao[size];
        }
    };

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
