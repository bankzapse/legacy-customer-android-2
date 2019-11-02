package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 8/4/2016 AD.
 */
public class DriverDao implements Parcelable {

    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;
    @SerializedName("sex_id") private String sexId;
    @SerializedName("driver_lat") private String driverLatLng;
    @SerializedName("image_url") private String imageUrl;
    @SerializedName("start_time") private String startTime;
    @SerializedName("end_time") private String endTime;
    @SerializedName("distance") private Double distance;
    @SerializedName("eta") private String eta;

    protected DriverDao(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        sexId = in.readString();
        driverLatLng = in.readString();
        imageUrl = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        eta = in.readString();
    }

    public static final Creator<DriverDao> CREATOR = new Creator<DriverDao>() {
        @Override
        public DriverDao createFromParcel(Parcel in) {
            return new DriverDao(in);
        }

        @Override
        public DriverDao[] newArray(int size) {
            return new DriverDao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(sexId);
        dest.writeString(driverLatLng);
        dest.writeString(imageUrl);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(eta);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSexId() {
        return sexId;
    }

    public void setSexId(String sexId) {
        this.sexId = sexId;
    }

    public String getDriverLatLng() {
        return driverLatLng;
    }

    public void setDriverLatLng(String driverLatLng) {
        this.driverLatLng = driverLatLng;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }
}
