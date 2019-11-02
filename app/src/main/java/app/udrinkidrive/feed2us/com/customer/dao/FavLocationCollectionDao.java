package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TL3 on 9/23/2016 AD.
 */
public class FavLocationCollectionDao implements Parcelable{

    @SerializedName("result") private int result;
    @SerializedName("data") private List<FavLocationDao> data;
    @SerializedName("message") private String message;

    protected FavLocationCollectionDao(Parcel in) {
        result = in.readInt();
        data = in.createTypedArrayList(FavLocationDao.CREATOR);
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeTypedList(data);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FavLocationCollectionDao> CREATOR = new Creator<FavLocationCollectionDao>() {
        @Override
        public FavLocationCollectionDao createFromParcel(Parcel in) {
            return new FavLocationCollectionDao(in);
        }

        @Override
        public FavLocationCollectionDao[] newArray(int size) {
            return new FavLocationCollectionDao[size];
        }
    };

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<FavLocationDao> getData() {
        return data;
    }

    public void setData(List<FavLocationDao> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
