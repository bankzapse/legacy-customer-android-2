package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 10/5/2016 AD.
 */
public class CardDao implements Parcelable{

    @SerializedName("object") private String object;
    @SerializedName("id") private String id;
    @SerializedName("livemode") private Boolean livemode;
    @SerializedName("country") private String country;
    @SerializedName("city") private String city;
    @SerializedName("postal_code") private String postal_code;
    @SerializedName("financing") private String financing;
    @SerializedName("bank") private String bank;
    @SerializedName("last_digits") private String last_digits;
    @SerializedName("brand") private String brand;
    @SerializedName("expiration_month") private int expiration_month;
    @SerializedName("expiration_year") private int expiration_year;
    @SerializedName("fingerprint") private String fingerprint;
    @SerializedName("name") private String name;
    @SerializedName("security_code_check") private Boolean security_code_check;
    @SerializedName("created") private String created;

    protected CardDao(Parcel in) {
        object = in.readString();
        id = in.readString();
        country = in.readString();
        city = in.readString();
        postal_code = in.readString();
        financing = in.readString();
        bank = in.readString();
        last_digits = in.readString();
        brand = in.readString();
        expiration_month = in.readInt();
        expiration_year = in.readInt();
        fingerprint = in.readString();
        name = in.readString();
        created = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(object);
        dest.writeString(id);
        dest.writeString(country);
        dest.writeString(city);
        dest.writeString(postal_code);
        dest.writeString(financing);
        dest.writeString(bank);
        dest.writeString(last_digits);
        dest.writeString(brand);
        dest.writeInt(expiration_month);
        dest.writeInt(expiration_year);
        dest.writeString(fingerprint);
        dest.writeString(name);
        dest.writeString(created);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CardDao> CREATOR = new Creator<CardDao>() {
        @Override
        public CardDao createFromParcel(Parcel in) {
            return new CardDao(in);
        }

        @Override
        public CardDao[] newArray(int size) {
            return new CardDao[size];
        }
    };

    public String getObject() {
        return object;
    }

    public String getId() {
        return id;
    }

    public Boolean getLivemode() {
        return livemode;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public String getFinancing() {
        return financing;
    }

    public String getBank() {
        return bank;
    }

    public String getLast_digits() {
        return last_digits;
    }

    public String getBrand() {
        return brand;
    }

    public int getExpiration_month() {
        return expiration_month;
    }

    public int getExpiration_year() {
        return expiration_year;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getName() {
        return name;
    }

    public Boolean getSecurity_code_check() {
        return security_code_check;
    }

    public String getCreated() {
        return created;
    }
}
