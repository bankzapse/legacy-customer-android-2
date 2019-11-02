package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TL3 on 6/15/2016 AD.
 */
public class HaveServiceDao implements Parcelable {

    @SerializedName("result") private int result;
    @SerializedName("message") private String message;
    @SerializedName("card") private List<CreditCardDao> card;
    @SerializedName("count_card") private int count_card;
    @SerializedName("card_active") private int card_active;


    protected HaveServiceDao(Parcel in) {
        result = in.readInt();
        message = in.readString();
        card = in.createTypedArrayList(CreditCardDao.CREATOR);
        count_card = in.readInt();
        card_active = in.readInt();
    }

    public static final Creator<HaveServiceDao> CREATOR = new Creator<HaveServiceDao>() {
        @Override
        public HaveServiceDao createFromParcel(Parcel in) {
            return new HaveServiceDao(in);
        }

        @Override
        public HaveServiceDao[] newArray(int size) {
            return new HaveServiceDao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeString(message);
        dest.writeTypedList(card);
        dest.writeInt(count_card);
        dest.writeInt(card_active);
    }

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public List<CreditCardDao> getCard() {
        return card;
    }

    public int getCount_card() {
        return count_card;
    }

    public int getCard_active() {
        return card_active;
    }
}
