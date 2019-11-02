package app.udrinkidrive.feed2us.com.customer.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TL3 on 8/4/2016 AD.
 */
public class DriverCollectionDao implements Parcelable {

    @SerializedName("result") private int result;
    @SerializedName("data") private List<DriverDao> data;
    @SerializedName("status") private String status;

    protected DriverCollectionDao(Parcel in) {
        result = in.readInt();
        data = in.createTypedArrayList(DriverDao.CREATOR);
        status = in.readString();
    }

    public static final Creator<DriverCollectionDao> CREATOR = new Creator<DriverCollectionDao>() {
        @Override
        public DriverCollectionDao createFromParcel(Parcel in) {
            return new DriverCollectionDao(in);
        }

        @Override
        public DriverCollectionDao[] newArray(int size) {
            return new DriverCollectionDao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeTypedList(data);
        dest.writeString(status);
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<DriverDao> getData() {
        return data;
    }

    public void setData(List<DriverDao> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
