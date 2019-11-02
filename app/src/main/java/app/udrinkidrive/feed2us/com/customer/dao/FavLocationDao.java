package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 9/23/2016 AD.
 */
public class FavLocationDao implements Parcelable {

    @SerializedName("name") private String name;
    @SerializedName("formatted_address") private String address;
    @SerializedName("lat") private String latitude;
    @SerializedName("lng") private String longitude;

    protected FavLocationDao(Parcel in) {
        name = in.readString();
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<FavLocationDao> CREATOR = new Creator<FavLocationDao>() {
        @Override
        public FavLocationDao createFromParcel(Parcel in) {
            return new FavLocationDao(in);
        }

        @Override
        public FavLocationDao[] newArray(int size) {
            return new FavLocationDao[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
