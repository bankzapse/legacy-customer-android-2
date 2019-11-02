package app.udrinkidrive.feed2us.com.customer.fragment;

import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.adapter.GuidePagerAdapter;


/**
 * Created by TL3 on 6/15/2016.
 */
public class GuideFragment extends Fragment {

    public interface FragmentListener {
        void onSkipClicked();
    }

    GuidePagerAdapter adapter;
    ViewPager pager;
    CirclePageIndicator indicator;

    //GUIDES Tab Swipe
    private final int[] GUIDES = new int[] {
            R.drawable.simple_gl_1,
            R.drawable.simple_gl_2,
            R.drawable.simple_gl_3,
            R.drawable.simple_gl_4,
            R.drawable.simple_gl_5,
            R.drawable.simple_gl_6,
            R.drawable.simple_gl_7,
            R.drawable.simple_gl_8
    };

    public GuideFragment() {
        super();
    }

    public static GuideFragment newInstance() {
        GuideFragment fragment = new GuideFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guide, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {

        Button btnSkip = (Button) rootView.findViewById(R.id.btnSkip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListener listener = (FragmentListener) getActivity();
                listener.onSkipClicked();
            }
        });

        adapter = new GuidePagerAdapter(GUIDES);
        pager = (ViewPager)rootView.findViewById(R.id.vpIntro);
        pager.setAdapter(adapter);

        indicator = (CirclePageIndicator) rootView.findViewById(R.id.cpIntro);
        indicator.setViewPager(pager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

}
