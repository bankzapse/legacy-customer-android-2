package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 10/5/2016 AD.
 */
public class AddCreditCardDao implements Parcelable {

    @SerializedName("result") private int result;
    @SerializedName("message") private String message;
    @SerializedName("card_id") private String card_id;

    protected AddCreditCardDao(Parcel in) {
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

    public static final Creator<AddCreditCardDao> CREATOR = new Creator<AddCreditCardDao>() {
        @Override
        public AddCreditCardDao createFromParcel(Parcel in) {
            return new AddCreditCardDao(in);
        }

        @Override
        public AddCreditCardDao[] newArray(int size) {
            return new AddCreditCardDao[size];
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
