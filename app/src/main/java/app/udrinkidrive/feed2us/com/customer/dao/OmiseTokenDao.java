package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 10/5/2016 AD.
 */
public class OmiseTokenDao implements Parcelable{

    @SerializedName("object") private String object;
    @SerializedName("id") private String id;
    @SerializedName("livemode") private Boolean livemode;
    @SerializedName("location") private String location;
    @SerializedName("used") private Boolean used;
    @SerializedName("card") private CardDao card;
    @SerializedName("created") private String created;
    @SerializedName("code") private String code;
    @SerializedName("message") private String message;

    protected OmiseTokenDao(Parcel in) {
        object = in.readString();
        id = in.readString();
        location = in.readString();
        card = in.readParcelable(CardDao.class.getClassLoader());
        created = in.readString();
        code = in.readString();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(object);
        dest.writeString(id);
        dest.writeString(location);
        dest.writeParcelable(card, flags);
        dest.writeString(created);
        dest.writeString(code);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OmiseTokenDao> CREATOR = new Creator<OmiseTokenDao>() {
        @Override
        public OmiseTokenDao createFromParcel(Parcel in) {
            return new OmiseTokenDao(in);
        }

        @Override
        public OmiseTokenDao[] newArray(int size) {
            return new OmiseTokenDao[size];
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

    public String getLocation() {
        return location;
    }

    public Boolean getUsed() {
        return used;
    }

    public CardDao getCard() {
        return card;
    }

    public String getCreated() {
        return created;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
