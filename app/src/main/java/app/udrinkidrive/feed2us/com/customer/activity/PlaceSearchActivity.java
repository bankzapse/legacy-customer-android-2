package app.udrinkidrive.feed2us.com.customer.activity;

//import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.fragment.PlaceSearchFragment;

public class PlaceSearchActivity extends FragmentActivity {

    private PlaceSearchFragment placeSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);

        placeSearchFragment = (PlaceSearchFragment) getSupportFragmentManager().findFragmentById(R.id.contentContainer);

        if(placeSearchFragment == null) {
            placeSearchFragment = PlaceSearchFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contentContainer, placeSearchFragment)
                    .commit();
        }
    }
}
