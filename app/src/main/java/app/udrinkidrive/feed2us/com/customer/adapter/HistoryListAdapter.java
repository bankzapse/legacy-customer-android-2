package app.udrinkidrive.feed2us.com.customer.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import app.udrinkidrive.feed2us.com.customer.R;

/**
 * Created by TL3 on 6/15/2016 AD.
 */
public class HistoryListAdapter extends BaseAdapter {

    private Date pickupDateTime;
    private SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    private String formattedPrice;

    private ArrayList<HashMap<String, String>> historyList = new ArrayList<HashMap<String,String>>();

    public HistoryListAdapter(ArrayList<HashMap<String, String>> list) {
        this.historyList = list;
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_list_item, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.tvDate = (TextView)convertView.findViewById(R.id.tvDate);
            viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tvTime);
            viewHolder.tvPickup = (TextView)convertView.findViewById(R.id.tvPickup);
            viewHolder.tvDropoff = (TextView)convertView.findViewById(R.id.tvDropoff);
            viewHolder.tvFee = (TextView)convertView.findViewById(R.id.tvFee);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolderItem)convertView.getTag();
        }

        try {
            pickupDateTime = sdfDateTime.parse(historyList.get(position).get("pickup_time"));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.tvDate.setText(sdfDate.format(pickupDateTime));
        viewHolder.tvTime.setText(sdfTime.format(pickupDateTime));

        viewHolder.tvPickup.setText(Html.fromHtml(parent.getContext().getString(R.string.his_pickup) + historyList.get(position).get("source")));
        viewHolder.tvDropoff.setText(Html.fromHtml(parent.getContext().getString(R.string.his_dropoff) + historyList.get(position).get("destination")));

        //formattedPrice = new DecimalFormat("##,###.##฿").format(Double.parseDouble(historyList.get(position).get("price")));
        viewHolder.tvFee.setText(historyList.get(position).get("price"));

        return convertView;
    }

    final public class ViewHolderItem{
        TextView tvDate;
        TextView tvTime;
        TextView tvPickup;
        TextView tvDropoff;
        TextView tvFee;
    }
}
