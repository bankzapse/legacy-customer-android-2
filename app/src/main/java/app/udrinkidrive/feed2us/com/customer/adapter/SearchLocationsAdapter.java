package app.udrinkidrive.feed2us.com.customer.adapter;

//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.holder.SearchLocationsViewHolder;
import app.udrinkidrive.feed2us.com.customer.model.UdidLocation;

/**
 * Created by TL3 on 9/6/2016 AD.
 */
public class SearchLocationsAdapter extends RecyclerView.Adapter<SearchLocationsViewHolder>{

    private ArrayList<UdidLocation> locations;

    ListenerFromAdapter callback;

    public interface ListenerFromAdapter{
        void onRowClick(int index);
        void onImgClick(int index);
    }

    public SearchLocationsAdapter(ArrayList<UdidLocation> locations, ListenerFromAdapter callback) {
        this.locations = locations;
        this.callback = callback;
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    @Override
    public SearchLocationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_search_location, parent, false);
        return new SearchLocationsViewHolder(card);
    }

    @Override
    public void onBindViewHolder(SearchLocationsViewHolder holder, final int position) {
        final UdidLocation location = locations.get(position);
        holder.updateUI(location, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onRowClick(position);
            }
        });

        holder.locationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location.getType() == 2) {
                    callback.onImgClick(position);
                }
            }
        });
    }
}
