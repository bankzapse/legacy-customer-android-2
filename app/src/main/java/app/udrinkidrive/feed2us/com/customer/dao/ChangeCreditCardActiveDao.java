package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 10/11/2016 AD.
 */
public class ChangeCreditCardActiveDao implements Parcelable{

    @SerializedName("result") private int result;
    @SerializedName("message") private String message;
    @SerializedName("card_id") private String card_id;

    protected ChangeCreditCardActiveDao(Parcel in) {
        result = in.readInt();
        message = in.readString();
        card_id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeString(message);
        dest.writeString(card_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChangeCreditCardActiveDao> CREATOR = new Creator<ChangeCreditCardActiveDao>() {
        @Override
        public ChangeCreditCardActiveDao createFromParcel(Parcel in) {
            return new ChangeCreditCardActiveDao(in);
        }

        @Override
        public ChangeCreditCardActiveDao[] newArray(int size) {
            return new ChangeCreditCardActiveDao[size];
        }
    };

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public String getCard_id() {
        return card_id;
    }
}
