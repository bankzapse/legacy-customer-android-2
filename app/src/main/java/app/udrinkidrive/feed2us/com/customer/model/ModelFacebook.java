package app.udrinkidrive.feed2us.com.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TL3 on 3/14/16 AD.
 */
public class ModelFacebook implements Parcelable{

    public String facebookId = "";
    public String facebookToken = "";
    public String firstName = "";
    public String lastName = "";
    public int genderId = 0;
    public String email = "";
    public String birthday = "";
    public String verified = "";

    // Empty Constructor
    public ModelFacebook() {

    }

    protected ModelFacebook(Parcel in) {
        facebookId = in.readString();
        facebookToken = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        genderId = in.readInt();
        email = in.readString();
        birthday = in.readString();
        verified = in.readString();
    }

    public static final Creator<ModelFacebook> CREATOR = new Creator<ModelFacebook>() {
        @Override
        public ModelFacebook createFromParcel(Parcel in) {
            return new ModelFacebook(in);
        }

        @Override
        public ModelFacebook[] newArray(int size) {
            return new ModelFacebook[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(facebookId);
        dest.writeString(facebookToken);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeInt(genderId);
        dest.writeString(email);
        dest.writeString(birthday);
        dest.writeString(verified);
    }
}
