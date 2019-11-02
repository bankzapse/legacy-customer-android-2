package app.udrinkidrive.feed2us.com.customer.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.activity.ActivityLogin;
import app.udrinkidrive.feed2us.com.customer.activity.PlaceSearchActivity;
import app.udrinkidrive.feed2us.com.customer.adapter.PlaceAutocompleteAdapter;
import app.udrinkidrive.feed2us.com.customer.adapter.SearchLocationsAdapter;
import app.udrinkidrive.feed2us.com.customer.dao.AddFavLocationDao;
import app.udrinkidrive.feed2us.com.customer.dao.DelFavLocationDao;
import app.udrinkidrive.feed2us.com.customer.dao.FavLocationCollectionDao;
import app.udrinkidrive.feed2us.com.customer.dao.FavLocationDao;
import app.udrinkidrive.feed2us.com.customer.model.UdidLocation;
import app.udrinkidrive.feed2us.com.customer.service.GeocoderTask;
import app.udrinkidrive.feed2us.com.customer.service.HttpManager;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by TL3 on 8/1/2016.
 */
public class PlaceSearchFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraChangeListener,
        SearchLocationsAdapter.ListenerFromAdapter {

    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.markerDesc)
    TextView markerDesc;
    @BindView(R.id.btnSetLocation)
    Button btnSetLocation;
    @BindView(R.id.ivFav)
    ImageView ivFav;
    @BindView(R.id.rlBottom)
    RelativeLayout rlBottom;
    @BindView(R.id.mMarker)
    ImageView mMarker;

    private ArrayList<UdidLocation> lists;
    private ArrayList<UdidLocation> favLists;
    private ArrayList<UdidLocation> recentLists;
    private ArrayList<HashMap<String, String>> mapRecentList = new ArrayList<HashMap<String, String>>();
    RecyclerView recyclerView;
    SearchLocationsAdapter adapter;

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private static final LatLngBounds BOUNDS_GREATER_THAI = new LatLngBounds(
            new LatLng(12.545011, 99.4640083), new LatLng(17.448246, 104.6732303));

    private UdidLocation currentLocation;
    private UdidLocation mLocation;
    private Boolean ready = false;

    UDIDAlert alert;
    private ProgressDialog progressDia;

    public int req = -1;
    public int index = -1;

    public static final int MODE_LOADING = 1;
    public static final int MODE_FINISH = 2;
    public static final int MODE_START = 3;
    public static final int MODE_END = 4;
    private int mode = MODE_FINISH;

    public static final String RECENT_FILENAME = "RECENT.ser";

    public PlaceSearchFragment() {
        super();
    }

    public static PlaceSearchFragment newInstance() {
        PlaceSearchFragment fragment = new PlaceSearchFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_place_search, container, false);
        ButterKnife.bind(this, rootView);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Places.GEO_DATA_API)
                .build();

        Intent intent = getActivity().getIntent();
        currentLocation = intent.getParcelableExtra("CURRENT");
        currentLocation.setType(1);
        req = intent.getIntExtra("REQ", -1);
        if (req == 3) {
            index = intent.getIntExtra("INDEX", -1);
        }
        lists = new ArrayList<>();
        lists.add(currentLocation);

        favLists = new ArrayList<>();
        recentLists = new ArrayList<>();
        getFavList();

    }

    @SuppressLint("WrongConstant")
    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_locations);
        recyclerView.setHasFixedSize(true);

        adapter = new SearchLocationsAdapter(lists, PlaceSearchFragment.this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                rootView.findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("TH")
                .build();

        mAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, BOUNDS_GREATER_THAI, typeFilter);
        mAutocompleteView.setAdapter(mAdapter);

        mAutocompleteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setModeState(MODE_START);

                if (s.length() == 0 && count == 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                if (s.length() > 0) {
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAutocompleteView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocation != null) {
                    if (Utils.checkImageResource(getContext(), ivFav, R.drawable.big_star_active)) {
                        addFav();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("LOCATION", mLocation);
                        if (req == 3) {
                            intent.putExtra("INDEX", index);
                        }
                        getActivity().setResult(getActivity().RESULT_OK, intent);
                        getActivity().finish();
                    }
                }
            }
        });

        ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocation != null && Utils.checkImageResource(getContext(), ivFav, R.drawable.big_star)) {
                    //addFav();
                    ivFav.setImageResource(R.drawable.big_star_active);
                } else {
                    ivFav.setImageResource(R.drawable.big_star);
                }
            }
        });

        if (req == 3) {
            mMarker.setImageResource(R.drawable.dropoff_marker);
        } else if (req == 1) {
            mMarker.setImageResource(R.drawable.pickup_marker);
        } else if (req == 2) {
            mMarker.setImageResource(R.drawable.destination_marker);
        }

        setModeState(MODE_START);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("PlaceSearch", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
        Utils.showToast("Could not connect to Google API Client: Error " + connectionResult.getErrorCode());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.7563, 100.5018), 17.0f));
        if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i("PlaceSearch", "Autocomplete item selected: " + primaryText);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i("PlaceSearch", "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.d("PlaceSearch", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            Log.i("PlaceSearch", "Place details received: " + place.getName());

            mAutocompleteView.setText(place.getName().toString());
            mAutocompleteView.clearFocus();
            hideKeyboard(getActivity());
            pantoPosition(place.getLatLng());
            ready = true;

            UdidLocation selectedLocation = new UdidLocation(place.getName().toString(), place.getAddress().toString(), place.getLatLng().latitude, place.getLatLng().longitude, 3);
            saveRecent(selectedLocation);
            lists.add(favLists.size() + 1, selectedLocation);
            adapter.notifyDataSetChanged();

            setModeState(MODE_END);

            places.release();
        }
    };

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if(ready && mode == MODE_FINISH) {
            setModeState(MODE_LOADING);
            runGeoCodingSearch(cameraPosition.target.latitude, cameraPosition.target.longitude);
            ivFav.setImageResource(R.drawable.big_star);
        }
    }

    private void pantoPosition(LatLng location){
        if(mMap.getCameraPosition()!= null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom);
            //mMap.animateCamera(cameraUpdate);
            mMap.moveCamera(cameraUpdate);
        }
    }

    private void runGeoCodingSearch(final Double lat, final Double lng) {
        if(lat != null && lng != null) {
            GeocoderTask geocoderTask = new GeocoderTask() {
                @Override
                protected void onPostExecute(List<Address> addressList) {
                    String name = "";
                    String address = "";
                    if (addressList == null || addressList.size() == 0) {
                        Log.d("Geocoder: ", "No Location found");
                        runGeoCodingSearch(lat, lng);
                    }
                    else {
                        Address rawAddress = addressList.get(0);
                        for (int i = 0; i < rawAddress.getMaxAddressLineIndex(); i++) {
                            address = address + addressList.get(0).getAddressLine(i) + " ";
                        }
                        name = addressList.get(0).getAddressLine(0);


                        if(address==""){
                            address = name;
                        }

                        markerDesc.setVisibility(View.VISIBLE);
                        markerDesc.setText(address);

                        mLocation = new UdidLocation(address, address,
                                addressList.get(0).getLatitude(), addressList.get(0).getLongitude(), 1);
                        setModeState(MODE_FINISH);
                    }
                }
            };
            geocoderTask.execute(lat, lng);
        }
    }

    private void setModeState(int mode) {
        if(mode == MODE_LOADING) {
            this.mode = MODE_LOADING;
            markerDesc.setText("Loading ...");
            btnSetLocation.setEnabled(false);
            btnSetLocation.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey500));
        }
        else if(mode == MODE_FINISH) {
            this.mode = MODE_FINISH;
            btnSetLocation.setEnabled(true);
            btnSetLocation.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_udid));
        }
        else if(mode == MODE_START) {
            rlBottom.setVisibility(View.GONE);
        }
        else if(mode == MODE_END) {
            rlBottom.setVisibility(View.VISIBLE);
        }
    }

    private void getRecent() {
        try {
            FileInputStream fileInputStream = getActivity().openFileInput(RECENT_FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            mapRecentList = (ArrayList) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        convertToParcel();
        for (UdidLocation recent : recentLists) {
            lists.add(recent);
        }

    }

    private void saveRecent(UdidLocation selectedLocation) {
        try {
            FileInputStream fileInputStream = getActivity().openFileInput(RECENT_FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            mapRecentList = (ArrayList) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        convertToParcel();
        if(recentLists.size() == 10) {
            recentLists.remove(9);
        }

        //สถานที่ใหม่อยู่บน
        recentLists.add(0, selectedLocation);

        convertFromParcel();
        try {
            FileOutputStream fileOutputStream = getActivity().openFileOutput(RECENT_FILENAME, getActivity().MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(mapRecentList);
            objectOutputStream.flush();
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void convertFromParcel() {
        mapRecentList.clear();
        for(UdidLocation recent : recentLists) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("name", recent.getName());
            hashMap.put("address", recent.getAddress());
            hashMap.put("latitude", String.valueOf(recent.getLatitude()));
            hashMap.put("longitude", String.valueOf(recent.getLongitude()));
            mapRecentList.add(hashMap);
        }
    }

    private void convertToParcel() {
        recentLists.clear();
        for(HashMap<String, String> hashMap : mapRecentList) {
            UdidLocation recentLocation = new UdidLocation(hashMap.get("name").toString(), hashMap.get("address").toString(), Double.valueOf(hashMap.get("latitude").toString()), Double.valueOf(hashMap.get("longitude").toString()), 3);
            recentLists.add(recentLocation);
        }
    }

    @Override
    public void onRowClick(int index) {
        recyclerView.setVisibility(View.GONE);
        hideKeyboard(getActivity());
        pantoPosition(new LatLng(lists.get(index).getLatitude(), lists.get(index).getLongitude()));
        ready = true;
        setModeState(MODE_END);
    }

    @Override
    public void onImgClick(int index) {
        delFav(index);
        lists.remove(index);
        adapter.notifyDataSetChanged();
    }

    private void getFavList() {
        progressDia = new ProgressDialog(getContext());
        progressDia.setMessage("Loading..");
        progressDia.setCancelable(false);
        progressDia.show();

        favLists.clear();
        Call<FavLocationCollectionDao> call = HttpManager.getInstance().getService().getFavLocation(SPUtils.getString(getContext(), Value.API_KEY));
        call.enqueue(new Callback<FavLocationCollectionDao>() {
            @Override
            public void onResponse(Call<FavLocationCollectionDao> call, Response<FavLocationCollectionDao> response) {
                progressDia.dismiss();
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo favorite location : "+jsonFromPojo);
                    FavLocationCollectionDao dao = response.body();
                    if(dao.getResult() == 1) {
                        List<FavLocationDao> favLocations = dao.getData();
                        if(favLocations != null) {
                            for(FavLocationDao favLocation : favLocations) {
                                UdidLocation fav = new UdidLocation(favLocation.getName(), favLocation.getAddress(), Double.valueOf(favLocation.getLatitude()), Double.valueOf(favLocation.getLongitude()), 2);
                                favLists.add(fav);
                                lists.add(fav);
                            }
                        }
                        getRecent();
                    }
                    else {
                        alert = new UDIDAlert(getActivity(), "Evaluate", dao.getMessage(), 1);
                        alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alert.UDIDAlert.dismiss();
                                Utils.logout(getContext());
                                Intent in = new Intent(getContext(), ActivityLogin.class);
                                startActivity(in);
                                getActivity().finish();
                            }
                        });
                        alert.UDIDAlert.show();
                    }
                }
                else {
                    try {
                        Utils.showToast(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getRecent();
                }
            }

            @Override
            public void onFailure(Call<FavLocationCollectionDao> call, Throwable t) {
                progressDia.dismiss();
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                }
                else  {
                    Utils.showToast(t.toString());
                }
            }
        });
    }

    private void addFav() {
        progressDia = new ProgressDialog(getContext());
        progressDia.setMessage("Loading..");
        progressDia.setCancelable(false);
        progressDia.show();

        Call<AddFavLocationDao> call = HttpManager.getInstance().getService().addFavLocation(mLocation.getName(), mLocation.getAddress(), String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()), SPUtils.getString(getContext(), Value.API_KEY));
        call.enqueue(new Callback<AddFavLocationDao>() {
            @Override
            public void onResponse(Call<AddFavLocationDao> call, Response<AddFavLocationDao> response) {
                progressDia.dismiss();
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo add favorite location : "+jsonFromPojo);
                    AddFavLocationDao dao = response.body();
                    if(dao.getResult() == 1) {
                        //ivFav.setImageResource(R.drawable.big_star_active);
                        Intent intent = new Intent();
                        intent.putExtra("LOCATION", mLocation);
                        if(req == 3) {
                            intent.putExtra("INDEX", index);
                        }
                        getActivity().setResult(getActivity().RESULT_OK, intent);
                        getActivity().finish();
                    }
                    else {
                        if(dao.getStatus().equals("1")) {
                            /*alert = new UDIDAlert(getActivity(), "Add Favorite Place", dao.getMessage(), 1);
                            alert.UDIDAlert.show();
                            ivFav.setImageResource(R.drawable.big_star_active);*/
                            Intent intent = new Intent();
                            intent.putExtra("LOCATION", mLocation);
                            if(req == 3) {
                                intent.putExtra("INDEX", index);
                            }
                            getActivity().setResult(getActivity().RESULT_OK, intent);
                            getActivity().finish();
                        }
                        else {
                            alert = new UDIDAlert(getActivity(), "Add Favorite Place", dao.getMessage(), 1);
                            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.UDIDAlert.dismiss();
                                    Utils.logout(getContext());
                                    Intent in = new Intent(getContext(), ActivityLogin.class);
                                    startActivity(in);
                                    getActivity().finish();
                                }
                            });
                            alert.UDIDAlert.show();
                        }
                    }
                }
                else {
                    try {
                        Utils.showToast(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AddFavLocationDao> call, Throwable t) {
                progressDia.dismiss();
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                }
                else  {
                    Utils.showToast(t.toString());
                }
            }
        });
    }

    private void delFav(int row) {
        progressDia = new ProgressDialog(getContext());
        progressDia.setMessage("Loading..");
        progressDia.setCancelable(false);
        progressDia.show();

        Call<DelFavLocationDao> call = HttpManager.getInstance().getService().delFavLocation(String.valueOf(lists.get(row).getLatitude()), String.valueOf(lists.get(row).getLongitude()), SPUtils.getString(getContext(), Value.API_KEY));
        call.enqueue(new Callback<DelFavLocationDao>() {

            @Override
            public void onResponse(Call<DelFavLocationDao> call, Response<DelFavLocationDao> response) {
                progressDia.dismiss();
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo dele favorite location : "+jsonFromPojo);
                    DelFavLocationDao dao = response.body();
                    if(dao.getResult() == 1) {

                    }
                    else {
                        if(dao.getStatus().equals("0")) {
                            alert = new UDIDAlert(getActivity(), "delete Favorite Place", dao.getMessage(), 1);
                            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.UDIDAlert.dismiss();
                                    Utils.logout(getContext());
                                    Intent in = new Intent(getContext(), ActivityLogin.class);
                                    startActivity(in);
                                    getActivity().finish();
                                }
                            });
                            alert.UDIDAlert.show();
                        }
                    }
                }
            }

//            @Override
//            public void onResponse(Call<DelFavLocationDao> call, Response<DelFavLocationDao> response) {
//
//            }

            @Override
            public void onFailure(Call<DelFavLocationDao> call, Throwable t) {
                progressDia.dismiss();
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("=Connection Time out. Please try again.");
                }
                else  {
                    Utils.showToast(t.toString());
                }
            }
        });
    }

    public void hideKeyboard(Activity activity) {
//        InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
