package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TL3 on 10/5/2016 AD.
 */
public class CreditCardCollectionDao implements Parcelable {

    @SerializedName("result") private int result;
    @SerializedName("data") private List<CreditCardDao> data;
    @SerializedName("count") private int count;
    @SerializedName("card_active") private String card_active;
    @SerializedName("message") private String message;

    protected CreditCardCollectionDao(Parcel in) {
        result = in.readInt();
        data = in.createTypedArrayList(CreditCardDao.CREATOR);
        count = in.readInt();
        card_active = in.readString();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeTypedList(data);
        dest.writeInt(count);
        dest.writeString(card_active);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CreditCardCollectionDao> CREATOR = new Creator<CreditCardCollectionDao>() {
        @Override
        public CreditCardCollectionDao createFromParcel(Parcel in) {
            return new CreditCardCollectionDao(in);
        }

        @Override
        public CreditCardCollectionDao[] newArray(int size) {
            return new CreditCardCollectionDao[size];
        }
    };

    public int getResult() {
        return result;
    }

    public List<CreditCardDao> getData() {
        return data;
    }

    public int getCount() {
        return count;
    }

    public String getCard_active() {
        return card_active;
    }

    public String getMessage() {
        return message;
    }
}
