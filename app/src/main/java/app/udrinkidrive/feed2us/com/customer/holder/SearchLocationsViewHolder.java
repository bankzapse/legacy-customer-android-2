package app.udrinkidrive.feed2us.com.customer.holder;

//import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.model.UdidLocation;


/**
 * Created by TL3 on 9/6/2016 AD.
 */
public class SearchLocationsViewHolder extends RecyclerView.ViewHolder {

    public TextView locationTitle;
    public TextView locationAddress;
    public ImageView locationImage;

    public SearchLocationsViewHolder(View itemView) {
        super(itemView);

        locationTitle = (TextView) itemView.findViewById(R.id.location_title);
        locationAddress = (TextView) itemView.findViewById(R.id.location_address);
        locationImage = (ImageView) itemView.findViewById(R.id.location_img);
    }

    public void updateUI(UdidLocation location, final int position) {
        locationTitle.setText(location.getName());
        locationAddress.setText(location.getAddress());

        if (location.getType() == 1) {
            locationImage.setImageResource(R.drawable.current);
        }
        else if(location.getType() == 2) {
            locationImage.setImageResource(R.drawable.small_star);
        }
        else {
            locationImage.setImageResource(R.drawable.recent);
        }
    }
}
