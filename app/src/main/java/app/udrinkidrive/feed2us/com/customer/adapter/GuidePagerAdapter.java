package app.udrinkidrive.feed2us.com.customer.adapter;

import android.content.Context;
//import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import app.udrinkidrive.feed2us.com.customer.R;

/**
 * Created by TL3 on 6/16/2016 AD.
 */
public class GuidePagerAdapter extends PagerAdapter {

    private int[] guide;

    public GuidePagerAdapter(int[] guide) {
        this.guide = guide;
    }

    @Override
    public int getCount() {
        return guide.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.guide_pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.ivIntro);
        imageView.setImageResource(guide[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

}
