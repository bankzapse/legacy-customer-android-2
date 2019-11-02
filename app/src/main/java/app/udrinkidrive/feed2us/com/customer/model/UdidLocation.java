package app.udrinkidrive.feed2us.com.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TL3 on 7/13/2016 AD.
 */
public class UdidLocation implements Parcelable {

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int type;
    // current: type = 1
    // favorite: type = 2
    // recent: type = 3

    public UdidLocation(String name, String address, double latitude, double longitude, int type) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    protected UdidLocation(Parcel in) {
        name = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        type = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(type);
    }

    public static final Creator<UdidLocation> CREATOR = new Creator<UdidLocation>() {
        @Override
        public UdidLocation createFromParcel(Parcel in) {
            return new UdidLocation(in);
        }

        @Override
        public UdidLocation[] newArray(int size) {
            return new UdidLocation[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setType(int type) {
        this.type = type;
    }
}
