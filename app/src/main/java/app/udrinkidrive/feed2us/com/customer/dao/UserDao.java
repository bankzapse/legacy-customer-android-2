package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 6/15/2016 AD.
 */
public class UserDao implements Parcelable {

    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;
    @SerializedName("email") private String email;
    @SerializedName("sex_id") private String sexId;
    @SerializedName("phone") private String phone;
    @SerializedName("api_token") private String api_token;

    protected UserDao(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        sexId = in.readString();
        phone = in.readString();
        api_token = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(sexId);
        dest.writeString(phone);
        dest.writeString(api_token);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserDao> CREATOR = new Creator<UserDao>() {
        @Override
        public UserDao createFromParcel(Parcel in) {
            return new UserDao(in);
        }

        @Override
        public UserDao[] newArray(int size) {
            return new UserDao[size];
        }
    };

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSexId() {
        return sexId;
    }

    public void setSexId(String sexId) {
        this.sexId = sexId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getApi_token() {
        return api_token;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }
}
