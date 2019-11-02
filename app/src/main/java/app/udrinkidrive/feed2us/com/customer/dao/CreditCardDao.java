package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 10/5/2016 AD.
 */
public class CreditCardDao implements Parcelable{

    @SerializedName("id") private String id;
    @SerializedName("card_num") private String card_num;
    @SerializedName("brand") private String brand;

    protected CreditCardDao(Parcel in) {
        id = in.readString();
        card_num = in.readString();
        brand = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(card_num);
        dest.writeString(brand);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CreditCardDao> CREATOR = new Creator<CreditCardDao>() {
        @Override
        public CreditCardDao createFromParcel(Parcel in) {
            return new CreditCardDao(in);
        }

        @Override
        public CreditCardDao[] newArray(int size) {
            return new CreditCardDao[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getCard_num() {
        return card_num;
    }

    public String getBrand() {
        return brand;
    }
}
