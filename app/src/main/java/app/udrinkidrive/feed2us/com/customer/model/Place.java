package app.udrinkidrive.feed2us.com.customer.model;

/**
 * Created by TL3 on 6/18/2016 AD.
 */
public class Place {

    final String DRAWABLE = "drawable/";

    public String getImageUrl() {
        return DRAWABLE + locationImgUrl;
    }


    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getLocationImgUrl() {
        return locationImgUrl;
    }

    private Double latitude;
    private Double longitude;
    private String locationTitle;
    private String locationAddress;
    private String locationImgUrl;

    public Place(Double latitude, Double longitude, String locationTitle, String locationAddress, String locationImgUrl) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationTitle = locationTitle;
        this.locationAddress = locationAddress;
        this.locationImgUrl = locationImgUrl;
    }
}
