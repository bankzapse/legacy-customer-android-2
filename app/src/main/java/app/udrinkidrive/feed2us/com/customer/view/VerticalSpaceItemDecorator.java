package app.udrinkidrive.feed2us.com.customer.view;

import android.graphics.Rect;
//import android.support.v7.widget.RecyclerView;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by TL3 on 7/14/2016 AD.
 */
public class VerticalSpaceItemDecorator extends RecyclerView.ItemDecoration {

    private final int spacer;

    public VerticalSpaceItemDecorator(int spacer) {
        this.spacer = spacer;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = spacer;
    }

}
