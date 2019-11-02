package app.udrinkidrive.feed2us.com.customer.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ModelLocation implements Serializable {

    public int seq;
    public String location_name;
    public double latitude;
    public double longitude;
    public boolean checked;


}
