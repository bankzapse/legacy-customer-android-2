package app.udrinkidrive.feed2us.com.customer.adapter;

//import android.support.v4.content.ContextCompat;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.holder.LocationsViewHolder;
import app.udrinkidrive.feed2us.com.customer.model.UdidLocation;
import app.udrinkidrive.feed2us.com.customer.service.Contextor;

/**
 * Created by TL3 on 6/18/2016 AD.
 */
public class LocationsAdapter extends RecyclerView.Adapter<LocationsViewHolder>{

    private ArrayList<UdidLocation> locations;

    ListenerFromAdapter callback;

    public interface ListenerFromAdapter{
        void onRowClick(int index);
        void onImgClick(int index);
    }

    public LocationsAdapter(ArrayList<UdidLocation> locations, ListenerFromAdapter callback) {
        this.locations = locations;
        this.callback = callback;
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    @Override
    public LocationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_location, parent, false);
        return new LocationsViewHolder(card);
    }

    @Override
    public void onBindViewHolder(LocationsViewHolder holder, final int position) {
        final UdidLocation location = locations.get(position);
        holder.updateUI(location, position);

        if(position == 0) {
            holder.locationTitle.setText("PICK UP");
            holder.locationColor.setBackgroundColor(ContextCompat.getColor(Contextor.getInstance().getContext(), R.color.blue_udid));
            holder.locationImage.setVisibility(View.INVISIBLE);
        }
        else if (position == locations.size() - 1) {
            holder.locationTitle.setText("DESTINATION");
            holder.locationAddress.setHint("Choose your destination");
            holder.locationColor.setBackgroundColor(ContextCompat.getColor(Contextor.getInstance().getContext(), R.color.black));
            holder.locationImage.setImageResource(R.drawable.add_des);
            if(location.getType() == 0) {
                holder.locationImage.setVisibility(View.INVISIBLE);
            }
            else {
                holder.locationImage.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.locationTitle.setText("DROP OFF" + " " + position);
            holder.locationAddress.setHint("Choose your dropoff");
            holder.locationColor.setBackgroundColor(ContextCompat.getColor(Contextor.getInstance().getContext(), R.color.grey500));
            holder.locationImage.setImageResource(R.drawable.remove_des);
        }

        holder.locationAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onRowClick(position);
            }
        });

        holder.locationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onImgClick(position);
            }
        });
    }

}
