package app.udrinkidrive.feed2us.com.customer.service;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by TL3 on 7/14/2016 AD.
 */
public class GeocoderTask extends AsyncTask<Double, Void, List<Address>> {

    @Override
    protected List<Address> doInBackground(Double... params) {
        Geocoder geoCoder = new Geocoder(Contextor.getInstance().getContext(), new Locale("TH"));
        List<Address> addressList = null;
        try {
            if(params[0] != null && params[1] != null) {
                addressList = geoCoder.getFromLocation(params[0], params[1], 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressList;
    }

    @Override
    protected void onPostExecute(List<Address> addressList) {
        super.onPostExecute(addressList);
    }
}
