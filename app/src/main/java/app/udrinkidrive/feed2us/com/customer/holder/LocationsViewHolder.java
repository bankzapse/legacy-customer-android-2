package app.udrinkidrive.feed2us.com.customer.holder;

//import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.model.UdidLocation;


/**
 * Created by TL3 on 6/18/2016 AD.
 */
public class LocationsViewHolder extends RecyclerView.ViewHolder {

    public TextView locationColor;
    public TextView locationTitle;
    public TextView locationAddress;
    public ImageView locationImage;

    public LocationsViewHolder(View itemView) {
        super(itemView);

        locationColor = (TextView) itemView.findViewById(R.id.location_color);
        locationTitle = (TextView) itemView.findViewById(R.id.location_title);
        locationAddress = (TextView) itemView.findViewById(R.id.location_address);
        locationImage = (ImageView) itemView.findViewById(R.id.location_img);
    }

    public void updateUI(UdidLocation location, final int position) {
        locationAddress.setText(location.getName());
    }
}
