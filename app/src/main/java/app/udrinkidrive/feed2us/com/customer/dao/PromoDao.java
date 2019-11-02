package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 8/18/2016 AD.
 */
public class PromoDao implements Parcelable {

    @SerializedName("result") private int result;
    @SerializedName("message") private String message;
    @SerializedName("discount") private String discount;
    @SerializedName("discount_num") private int discount_num;
    @SerializedName("type") private String type;


    protected PromoDao(Parcel in) {
        result = in.readInt();
        message = in.readString();
        discount = in.readString();
        discount_num = in.readInt();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeString(message);
        dest.writeString(discount);
        dest.writeInt(discount_num);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PromoDao> CREATOR = new Creator<PromoDao>() {
        @Override
        public PromoDao createFromParcel(Parcel in) {
            return new PromoDao(in);
        }

        @Override
        public PromoDao[] newArray(int size) {
            return new PromoDao[size];
        }
    };

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getDiscount_num() {
        return discount_num;
    }

    public void setDiscount_num(int discount_num) {
        this.discount_num = discount_num;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
