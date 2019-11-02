package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TL3 on 10/3/2016 AD.
 */
public class MainDataDao implements Parcelable {

    @SerializedName("result") private int result;
    @SerializedName("reward_badge") private int reward_badge;
    @SerializedName("message") private String message;

    protected MainDataDao(Parcel in) {
        result = in.readInt();
        reward_badge = in.readInt();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeInt(reward_badge);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MainDataDao> CREATOR = new Creator<MainDataDao>() {
        @Override
        public MainDataDao createFromParcel(Parcel in) {
            return new MainDataDao(in);
        }

        @Override
        public MainDataDao[] newArray(int size) {
            return new MainDataDao[size];
        }
    };

    public int getResult() {
        return result;
    }

    public int getReward_badge() {
        return reward_badge;
    }

    public String getMessage() {
        return message;
    }
}
