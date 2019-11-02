package app.udrinkidrive.feed2us.com.customer.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.activity.ActivityCreditCard;
import app.udrinkidrive.feed2us.com.customer.activity.ActivityDriver;
import app.udrinkidrive.feed2us.com.customer.activity.ActivityInviteFriend;
import app.udrinkidrive.feed2us.com.customer.activity.ActivityLogin;
import app.udrinkidrive.feed2us.com.customer.activity.ActivityOmise;
import app.udrinkidrive.feed2us.com.customer.activity.ActivityProfile;
import app.udrinkidrive.feed2us.com.customer.activity.PlaceSearchActivity;
import app.udrinkidrive.feed2us.com.customer.adapter.LocationsAdapter;
import app.udrinkidrive.feed2us.com.customer.dao.AddServiceDao;
import app.udrinkidrive.feed2us.com.customer.dao.CheckCreditCardDao;
import app.udrinkidrive.feed2us.com.customer.dao.CreditCardCollectionDao;
import app.udrinkidrive.feed2us.com.customer.dao.CreditCardDao;
import app.udrinkidrive.feed2us.com.customer.dao.DriverCollectionDao;
import app.udrinkidrive.feed2us.com.customer.dao.DriverDao;
import app.udrinkidrive.feed2us.com.customer.dao.MainDataDao;
import app.udrinkidrive.feed2us.com.customer.dao.PromoDao;
import app.udrinkidrive.feed2us.com.customer.event.PopupEvent;
import app.udrinkidrive.feed2us.com.customer.model.UdidLocation;
import app.udrinkidrive.feed2us.com.customer.service.HttpManager;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import app.udrinkidrive.feed2us.com.customer.view.UDIDInput;
import app.udrinkidrive.feed2us.com.customer.view.VerticalSpaceItemDecorator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by TL3 on 6/22/2016.
 */
public class MainFragment extends Fragment implements LocationsAdapter.ListenerFromAdapter,
        OnMapReadyCallback,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener,
        DialogInterface.OnCancelListener {

    @BindView(R.id.ivMenu)
    ImageView ivMenu;
    @BindView(R.id.ivDot)
    ImageView ivDot;
    @BindView(R.id.ivInviteFriend)
    ImageView ivInviteFriend;
    @BindView(R.id.ibMyLocation)
    ImageButton ibMyLocation;

    @BindView(R.id.rlDetail)
    RelativeLayout rlDetail;
    @BindView(R.id.rlPromo)
    RelativeLayout rlPromo;
    @BindView(R.id.tvPromo)
    TextView tvPromo;
    @BindView(R.id.rlPayment)
    RelativeLayout rlPayment;
    @BindView(R.id.tvPayment)
    TextView tvPayment;
    @BindView(R.id.rlTime)
    RelativeLayout rlTime;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.rlPhone)
    RelativeLayout rlPhone;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.rlDriver)
    RelativeLayout rlDriver;
    @BindView(R.id.tvDriver)
    TextView tvDriver;
    @BindView(R.id.rlGear)
    RelativeLayout rlGear;
    @BindView(R.id.tvGear)
    TextView tvGear;
    @BindView(R.id.llLine3)
    LinearLayout llLine3;
    @BindView(R.id.tvNote)
    TextView tvNote;

    @BindView(R.id.llEstimated)
    LinearLayout llEstimated;
    @BindView(R.id.tvEstimatedFareTitle)
    TextView tvEstimatedFareTitle;
    @BindView(R.id.tvEstimatedFare)
    TextView tvEstimatedFare;
    @BindView(R.id.tvEstimatedTimeTitle)
    TextView tvEstimatedTimeTitle;
    @BindView(R.id.tvEstimatedTime)
    TextView tvEstimatedTime;
    @BindView(R.id.btnBooking)
    Button btnBooking;

    @BindView(R.id.rlWebView)
    RelativeLayout rlWebView;
    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.ivCloseWeb)
    ImageView ivCloseWeb;

    UDIDAlert alert;

    //Dialog
    Dialog dia;
    Button btnOK;
    Button btnCancel;
    TextView tvdDate;
    TextView tvdTime;
    Button btnPickupNow;

    UDIDInput inputAlert;

    private ProgressDialog progressDia;

    static final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

    private SimpleDateFormat sdfc = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SimpleDateFormat sdfD = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdfT = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat sdfS = new SimpleDateFormat("E dd MMM HH:mm");
    private SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Date selectDate;

    private GoogleMap mMap;
    private LatLng currentLocation;
    private final Map<Integer, Marker> driverMarkers = new HashMap<Integer, Marker>();
    private final Map<Integer, Marker> mMarkers = new HashMap<Integer, Marker>();

    private ArrayList<UdidLocation> addressLists;
    RecyclerView recyclerView;
    LocationsAdapter adapter;

    HashMap<String, String> defaultValue;

    private String distanceStr = "";
    private int price = 0;

    private double distance1 = 0;
    private int estimateTime1 = 0;
    private double distance2 = 0;
    private int estimateTime2 = 0;
    private double distance = 0;
    private int estimateTime = 0;

    private String imei = "";

    private int promo_discount = 0;
    private String promo_type = "percent";
    private Boolean bookingPressed = false;

    public static final int REQ_SEARCH_PICKUP = 1;
    public static final int REQ_SEARCH_DESTINATION = 2;
    public static final int REQ_SEARCH_DROPOFF = 3;
    public static final int REQ_PAYMENT = 4;

    private FirebaseAnalytics mFirebaseAnalytics;

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        addressLists = new ArrayList<>();
        defaultValue = new HashMap<>();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        EventBus.getDefault().register(this);
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

        adapter = new LocationsAdapter(addressLists, MainFragment.this);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecorator(10));
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        btnBooking.setEnabled(false);

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivDot.setVisibility(View.GONE);
                Intent intent = new Intent(getContext(), ActivityProfile.class);
                startActivity(intent);
            }
        });

        ivInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityInviteFriend.class);
                startActivity(intent);
            }
        });

        ibMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLocation != null) {
                    pantoPosition(currentLocation);
                }
            }
        });

        rlPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputAlert = new UDIDInput(getActivity(), "Promo Code");
                showKeyboard(getActivity());
                inputAlert.dBtnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputAlert.UDIDInput.dismiss();
                        tvPromo.setText(inputAlert.edtInput.getText().toString());
                        if (tvPromo.getText().toString().equals("")) {
                            if (!tvEstimatedFare.getText().toString().equals("-")) {
                                tvEstimatedFare.setText(price + "THB");
                            }
                        } else {
                            bookingPressed = false;
                            checkPromo(inputAlert.edtInput.getText().toString());
                        }

                    }
                });
                inputAlert.UDIDInput.show();
            }
        });

        rlPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityOmise.class);
                startActivityForResult(intent, REQ_PAYMENT);
            }
        });

        rlTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaPickupTime();
            }
        });

        rlPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputAlert = new UDIDInput(getActivity(), "Mobile Number");
                showKeyboard(getActivity());
                inputAlert.dBtnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputAlert.UDIDInput.dismiss();
                        if ((inputAlert.edtInput.getText().toString().length() < 10 || inputAlert.edtInput.getText().toString().length() > 11)
                                ||
                                (inputAlert.edtInput.getText().toString().length() == 10 && !inputAlert.edtInput.getText().toString().substring(0, 1).equals("0"))
                        ) {
                            inputAlert.UDIDInput.dismiss();
                            UDIDAlert alert = new UDIDAlert(getActivity(), "Mobile Number", getString(R.string.alert_r7), 1);
                            alert.UDIDAlert.show();
                        } else {
                            inputAlert.UDIDInput.dismiss();
                            tvPhone.setText(inputAlert.edtInput.getText().toString());
                        }
                    }
                });
                inputAlert.UDIDInput.show();
            }
        });

        rlDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheet.Builder(getActivity()).title("Driver").sheet(R.menu.list_driver).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.any:
                                tvDriver.setText("Any Gender");
                                break;
                            case R.id.male:
                                tvDriver.setText("Male");
                                break;
                            case R.id.female:
                                tvDriver.setText("Female");
                                break;
                        }
                    }
                }).show();
            }
        });

        rlGear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheet.Builder(getActivity()).title("Gear").sheet(R.menu.list_gear).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.auto:
                                tvGear.setText("Automatic");
                                break;
                            case R.id.manual:
                                tvGear.setText("Manual");
                                break;
                            case R.id.supercar:
                                tvGear.setText("Supercar");
                                break;
                        }
                    }
                }).show();
            }
        });

        tvNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputAlert = new UDIDInput(getActivity(), "Note to Driver");
                showKeyboard(getActivity());
                inputAlert.dBtnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputAlert.UDIDInput.dismiss();
                        tvNote.setText(inputAlert.edtInput.getText().toString());
                    }
                });
                inputAlert.dBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputAlert.UDIDInput.dismiss();
                    }
                });
                inputAlert.UDIDInput.show();
            }
        });

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingPressed = true;

                if (!tvPromo.getText().toString().equals("") || tvPromo.getText().toString().length() > 0) {
                    checkPromo(tvPromo.getText().toString());
                } else {
                    addService();
                }

            }
        });

        if (SPUtils.getString(getContext(), "PAYMENT").toString().equals("") ||
                SPUtils.getString(getContext(), "PAYMENT").toString().equals("Cash") ||
                SPUtils.getString(getContext(), Value.CARD) == "") {
            SPUtils.set(getContext(), "PAYMENT", "Cash");
            tvPayment.setText("Cash");
        } else {
            tvPayment.setText(SPUtils.getString(getContext(), "PAYMENT").toString());
        }

        progressDia = new ProgressDialog(getContext());
        progressDia.setMessage("Loading ...");
        progressDia.setCancelable(false);
        progressDia.show();
    }

    private void getCardList() {

        Call<CreditCardCollectionDao> call = HttpManager.getInstance()
                .getService()
                .getCardList(SPUtils.getString(getContext(), Value.API_KEY));
        call.enqueue(new Callback<CreditCardCollectionDao>() {
            @Override
            public void onResponse(Call<CreditCardCollectionDao> call, Response<CreditCardCollectionDao> response) {
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
//                    Log.d("TagD","jsonFromPojo get card list : "+jsonFromPojo);
                    CreditCardCollectionDao dao = response.body();
                    if(dao.getResult() == 1) {
                        SPUtils.set(getContext(), Value.CARD, dao.getCard_active());

                        if(dao.getCount() != 0) {
                            List<CreditCardDao> cardDao = dao.getData();
                            for (int i = 0; i < cardDao.size(); i++) {
                                HashMap<String, String> hashMap = new HashMap<String, String>();
                                hashMap.put("id", cardDao.get(i).getId());
                                hashMap.put("card_num", cardDao.get(i).getCard_num());
                                hashMap.put("brand", cardDao.get(i).getBrand());
//                                Log.d("Tag","card_num : "+cardDao.get(i).getCard_num());
//                                Log.d("Tag","brand : "+cardDao.get(i).getBrand());
//                                cardList.add(hashMap);
//                                arrayList.add(hashMap);
                                Boolean lociCard = (cardDao.get(i).getId().equals(dao.getCard_active()));
//                                Log.d("Tag","lociCard : "+lociCard);
                                if(lociCard){
                                    tvPayment.setText("**** " + cardDao.get(i).getCard_num());
                                }
//                                if(cardDao.get(i).getId().equals(dao.getCard_active())){
//                                    SPUtils.set(getContext(), "PAYMENT", "**** " + arrayList.get(i).get("card_num"));
//                                }
                            }

                        }
                        else {
                            SPUtils.set(getContext(), Value.CARD, "");
                        }
//                        Log.d("Tag","getCard_active : "+dao.getCard_active());

                    }
                }

            }

            @Override
            public void onFailure(Call<CreditCardCollectionDao> call, Throwable t) {
            }
        });
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

    @OnClick(R.id.ivCloseWeb)
    public void clickWeb() {
        rlWebView.setVisibility(View.GONE);
    }

    @Override
    public void onRowClick(int index) {
        Intent intent = new Intent(getContext(), PlaceSearchActivity.class);
        intent.putExtra("CURRENT", addressLists.get(0));
        if (index == 0) {
            intent.putExtra("REQ", REQ_SEARCH_PICKUP);
            startActivityForResult(intent, REQ_SEARCH_PICKUP);
        } else if (index == addressLists.size() - 1) {
            intent.putExtra("REQ", REQ_SEARCH_DESTINATION);
            startActivityForResult(intent, REQ_SEARCH_DESTINATION);
        } else {
            intent.putExtra("REQ", REQ_SEARCH_DROPOFF);
            intent.putExtra("INDEX", index);
            startActivityForResult(intent, REQ_SEARCH_DROPOFF);
        }
    }

    @Override
    public void onImgClick(int index) {
        if (index < addressLists.size() - 1) {
            Boolean isLocation = false;
            if (addressLists.get(index).getLatitude() != 0.0 && addressLists.get(index).getLongitude() != 0.0) {
                isLocation = true;
            }
            addressLists.remove(index);
            pinAllMarker();
            if (isLocation) {
                //getOptimizedDistance();
                GetDistance();
//                getOptimizedDistanceWithAvoid();
            }
        } else {
            if (addressLists.size() < 7) {
                addressLists.add(addressLists.size() - 1, new UdidLocation("", "", 0.0, 0.0, 0));
            } else {
                alert = new UDIDAlert(getActivity(), "Add Drop Off", "Only 6 drop off points allow", 1);
                alert.UDIDAlert.show();
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void setPickupMarker(String name, String address, LatLng latLng, String imei) {
        //first time
        this.imei = imei;
        llEstimated.setVisibility(View.VISIBLE);
        setDefaultField();

        mMap.clear();
        MarkerOptions userMarker = new MarkerOptions().position(latLng).title("Current Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
        Marker marker = mMap.addMarker(userMarker);
        mMarkers.put(0, marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
        addressLists.add(new UdidLocation(name, address, latLng.latitude, latLng.longitude, 0));
        addressLists.add(new UdidLocation("", "", 0.0, 0.0, 0));
        adapter.notifyDataSetChanged();

        getMainData();
        getAvailDriver(latLng.latitude, latLng.longitude);

    }

    private void pinAllMarker() {
        int index = 0;
        for (Marker item : mMarkers.values()) {
            item.remove();
        }
        for (UdidLocation mLocation : addressLists) {
            if (addressLists.get(index).getLatitude() != 0.0 && addressLists.get(index).getLongitude() != 0.0) {
                int resource = getMarkerResource(index);
                LatLng point = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(point)
                        .icon(BitmapDescriptorFactory.fromResource(resource));
                Marker marker = mMap.addMarker(markerOptions);
                mMarkers.put(index, marker);
            }
            index++;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(addressLists.get(0).getLatitude() + 0.1, addressLists.get(0).getLongitude()), 11.0f));
    }

    private int getMarkerResource(int index) {
        if (index == 0) {
            return R.drawable.pickup_marker;
        } else if (index == addressLists.size() - 1) {
            return R.drawable.destination_marker;
        } else {
            return R.drawable.dropoff_marker;
        }
    }

    public void onLocationChanged(LatLng latLng) {
        currentLocation = latLng;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.7563, 100.5018), 14.0f));
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void pantoPosition(LatLng location) {
        if(mMap.getCameraPosition()!= null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom);
            mMap.animateCamera(cameraUpdate);
        }
    }

    private void setDefaultField() {

        tvGear.setText("Automatic");
        tvDriver.setText("Any Gender");
        if(SPUtils.getString(getContext(), Value.MOBILENO).length() == 11){
            StringBuilder sb = new StringBuilder(SPUtils.getString(getContext(), Value.MOBILENO));
            sb.delete(0, 2);
            String new_phone = sb.toString();
            tvPhone.setText("0"+new_phone);
        }else{
            tvPhone.setText(SPUtils.getString(getContext(), Value.MOBILENO));
        }
    }

    private void saveDefaultField() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("gear", tvGear.getText().toString());
        hashMap.put("driver", tvDriver.getText().toString());
        hashMap.put("phone", tvPhone.getText().toString());
    }

    private void setEtaAndPinDriverMarker(List<DriverDao> drivers) {
        int index = 0;
        for (Marker item : driverMarkers.values()) {
            item.remove();
        }
        for(DriverDao driver : drivers) {
            if(index == 0) {
                tvEstimatedTime.setText(driver.getEta());
            }
            if(index < 5) {
                String driverLatLng = driver.getDriverLatLng();
                String[] latLng = driverLatLng.split(",");
                Double lat = Double.parseDouble(latLng[0]);
                Double lng = Double.parseDouble(latLng[1]);
                LatLng point = new LatLng(lat, lng);
                MarkerOptions markerOptions = new MarkerOptions().position(point)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pickup));
                Marker driverMarker = mMap.addMarker(markerOptions);
                driverMarkers.put(index, driverMarker);
            }
            index++;
        }
    }

    private void diaPickupTime() {
        createAndBindDiaPickupTime();

        //init
        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date now = new Date(t + (30 * ONE_MINUTE_IN_MILLIS)); //เพิ่ม30นาที

        tvdDate.setText(sdfD.format(now));
        tvdTime.setText(sdfT.format(now) + " น.");

        //event
        btnPickupNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar date = Calendar.getInstance();
                long t = date.getTimeInMillis();
                Date now = new Date(t + (30 * ONE_MINUTE_IN_MILLIS)); //เพิ่ม30นาที

                tvdDate.setText(sdfD.format(now));
                tvdTime.setText(sdfT.format(now) + " น.");

                //next view
                try {
                    selectDate = sdfc.parse(tvdDate.getText().toString() + " " + tvdTime.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!tvdDate.getText().toString().equals("")) {
                    if (checkPickupTime()) {
                        //dia.dismiss();
                        tvTime.setText(sdfS.format(selectDate) + " น.");
                    } else {
                        alert = new UDIDAlert(getActivity(), "Notice", getString(R.string.alert_h1), 1);
                        alert.UDIDAlert.show();
                    }
                }
            }
        });

        tvdDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MainFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.dismissOnPause(true);
                dpd.setAccentColor(Color.parseColor("#00AEFF"));
                dpd.setMinDate(now);
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });

        tvdTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        MainFragment.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd.dismissOnPause(true);
                tpd.setAccentColor(Color.parseColor("#00AEFF"));
                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    selectDate = sdfc.parse(tvdDate.getText().toString() + " " + tvdTime.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!tvdDate.getText().toString().equals("")) {
                    if (checkPickupTime()) {
                        dia.dismiss();
                        tvTime.setText(sdfS.format(selectDate) + " น.");
                    }
                    else {
                        alert = new UDIDAlert(getActivity(), "Notice", getString(R.string.alert_h1), 1);
                        alert.UDIDAlert.show();
                    }
                }
            }
        });


        dia.setCancelable(true);
        dia.show();
    }

    private void createAndBindDiaPickupTime() {
        dia = new Dialog(getContext());
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dia.setContentView(R.layout.dialog_pickuptime);
        dia.setOnCancelListener(this);
        btnOK = (Button) dia.findViewById(R.id.btnOK);
        btnCancel = (Button) dia.findViewById(R.id.btnCancel);
        btnPickupNow = (Button) dia.findViewById(R.id.btnPickupNow);
        tvdDate = (TextView) dia.findViewById(R.id.tvPickupDate);
        tvdTime = (TextView) dia.findViewById(R.id.tvPickupTime);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        tvdDate.setText(date);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        //String secondString = second < 10 ? "0"+second : ""+second;
        String time = hourString+":"+minuteString;
        tvdTime.setText(time + " น.");
    }

    private Boolean checkPickupTime() {
        try{
            Calendar date = Calendar.getInstance();
            long t = date.getTimeInMillis();
            Date today = new Date(t + (29 * ONE_MINUTE_IN_MILLIS)); //เพิ่ม29นาที

            long difference = selectDate.getTime() - today.getTime();
            if (difference < 0)
            {
                return false;
            }
            else {
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK && requestCode == REQ_SEARCH_PICKUP) {
            UdidLocation mLocation = data.getParcelableExtra("LOCATION");
            addressLists.remove(0);
            addressLists.add(0, mLocation);
            adapter.notifyDataSetChanged();
            pinAllMarker();
            getAvailDriver(mLocation.getLatitude(), mLocation.getLongitude());
            if(addressLists.size() > 1) {
                GetDistance();
//                getOptimizedDistanceWithAvoid();
            }
        }
        else if(resultCode == getActivity().RESULT_OK && requestCode == REQ_SEARCH_DESTINATION) {
            UdidLocation mLocation = data.getParcelableExtra("LOCATION");
            addressLists.remove(addressLists.size() - 1);
            addressLists.add(mLocation);
            adapter.notifyDataSetChanged();

            onReadyBooking();
            //getOptimizedDistance();
            GetDistance();
//            getOptimizedDistanceWithAvoid();
            pinAllMarker();
        }
        else if(resultCode == getActivity().RESULT_OK && requestCode == REQ_SEARCH_DROPOFF) {
            UdidLocation mLocation = data.getParcelableExtra("LOCATION");
            int index = data.getIntExtra("INDEX", -1);
            if(index != -1) {
                addressLists.remove(index);
                addressLists.add(index, mLocation);
                adapter.notifyDataSetChanged();
            }
            //getOptimizedDistance();
            GetDistance();
//            getOptimizedDistanceWithAvoid();
            pinAllMarker();
        }
        else if(resultCode == getActivity().RESULT_OK && requestCode == REQ_PAYMENT) {
            /*if(SPUtils.getString(getContext(), "PAYMENT").toString().equals("") || SPUtils.getString(getContext(), "PAYMENT").toString().equals("Cash")) {
                SPUtils.set(getContext(), "PAYMENT", "Cash");
                tvPayment.setText("Cash");
            }
            else {
                tvPayment.setText(SPUtils.getString(getContext(), "PAYMENT").toString());
            }*/
        }
    }

    private void onReadyBooking() {
        btnBooking.setEnabled(true);
        btnBooking.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_udid));

        rlDetail.setAlpha(0.0f);
        rlDetail.setVisibility(View.VISIBLE);
        rlDetail.animate()
                .alpha(1.0f)
                .setDuration(2000);
        tvEstimatedTime.setVisibility(View.VISIBLE);

        tvEstimatedFareTitle.setText("FARE: ");
        tvEstimatedTimeTitle.setText("ETA: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SPUtils.getString(getContext(), "PAYMENT").toString().equals("") || SPUtils.getString(getContext(), "PAYMENT").toString().equals("Cash")) {
            SPUtils.set(getContext(), "PAYMENT", "Cash");
            tvPayment.setText("Cash");
        }
        else {
            tvPayment.setText(SPUtils.getString(getContext(), "PAYMENT").toString());
        }
        getCardList();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(PopupEvent event) {
        mWebView.setBackgroundColor(Color.DKGRAY);
        mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(event.getUrl_popup());
        rlWebView.setVisibility(View.VISIBLE);
    }

    private void discountPriceByPromo() {
        if(!tvEstimatedFare.getText().toString().equals("-")) {
            float price_promo = 0;
            if(promo_type.toString().equals("fix")) {
                price_promo = price - promo_discount;
            }
            else {
                price_promo = ((float)(100 - promo_discount) / 100) * (float)price;
            }
            long price_promo_int = Math.round(price_promo);
            tvEstimatedFare.setText(price_promo_int + "THB");
        }
    }

    private int getNet() {
        if(!tvEstimatedFare.getText().toString().equals("-")) {
            float price_promo = 0;
            if(promo_type.toString().equals("fix")) {
                price_promo = price - promo_discount;
            }
            else {
                price_promo = ((float)(100 - promo_discount) / 100) * (float)price;
            }
            long price_promo_int = Math.round(price_promo);
            return (int) price_promo_int;
        }
        else{
            return 0;
        }
    }

    private void GetDistance(){
        Log.d("TagG","GetDistance count");
        progressDia = new ProgressDialog(getContext());
        progressDia.setMessage("Loading ...");
        progressDia.setCancelable(false);
        progressDia.show();

        String origin = addressLists.get(0).getLatitude() + "," + addressLists.get(0).getLongitude();
        String destination = addressLists.get(addressLists.size() - 1).getLatitude() + "," + addressLists.get(addressLists.size() - 1).getLongitude();
        Log.d("Tag","tokenId : "+ SPUtils.getString(getContext(), Value.API_KEY));
        Log.d("Tag","origin : "+origin);
        Log.d("Tag","destination : "+destination);
        Log.d("Tag","registerSource : "+"2");
        Log.d("Tag","addressLists : "+addressLists.size());
//        String waypoint = "optimize:true|";
        ArrayList waypoint = new ArrayList();

        for (int i = 1; i < addressLists.size() - 1; i++) {
            //NOTE: %7C คือ |
            if (addressLists.get(i).getLatitude() != 0.0 && addressLists.get(i).getLongitude() != 0.0) {
//                waypoint = waypoint + "|" + addressLists.get(i).getLatitude() + "," + addressLists.get(i).getLongitude();
//                String add_point = addressLists.get(i).getLatitude() + "," + addressLists.get(i).getLongitude();
                waypoint.add( addressLists.get(i).getLatitude() + "," + addressLists.get(i).getLongitude());
            }
        }

        Log.d("Tag","waypoint : "+waypoint);
        Call<JsonElement> call = HttpManager.getInstance().getService().GetDistance(SPUtils.getString(getContext(), Value.API_KEY), destination, origin,waypoint,"2");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                progressDia.dismiss();
                Gson gsonBuilder = new GsonBuilder().create();
                String jsonFromPojo = gsonBuilder.toJson(response.body());
                Log.d("TagD","jsonFromPojo get distance : "+jsonFromPojo);

                JsonElement jsonElement = response.body();
                    Log.d("Tag","jsonElement : "+jsonElement);
                JsonObject jsonObject =  jsonElement.getAsJsonObject();
//                Log.d("Tag","jsonObject 1: "+jsonObject.getAsJsonObject("price"));
                String result = jsonObject.get("result").toString();
                if(!result.equalsIgnoreCase("0")) {
                    tvEstimatedFare.setText(jsonObject.get("price").toString());
                    distanceStr = jsonObject.get("distance").toString();
                    distance = Double.valueOf(distanceStr);
                    price = Integer.parseInt(jsonObject.get("price").toString());
                    estimateTime = 0;
                    discountPriceByPromo();

                    try {
                        if(Integer.parseInt(jsonObject.get("outzone").toString()) > 0){
                            ShowOutZone(jsonObject.get("outzone").toString());
                        }
                    }catch (Exception e){ }

                }

            }
            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                progressDia.dismiss();
                tvEstimatedFare.setText("-");
            }
        });

    }

    private void ShowOutZone(String pricezone){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_price_zone);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView dMessage = dialog.findViewById(R.id.dMessage);
        Button dBtnOK = dialog.findViewById(R.id.dBtnOK);

        dMessage.setText(pricezone + " บาท");

        dBtnOK.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getOptimizedDistanceWithAvoid() {
        progressDia = new ProgressDialog(getContext());
        progressDia.setMessage("Loading ...");
        progressDia.setCancelable(false);
        progressDia.show();

        String origin = addressLists.get(0).getLatitude() + "," + addressLists.get(0).getLongitude();
        String destination = addressLists.get(addressLists.size() - 1).getLatitude() + "," + addressLists.get(addressLists.size() - 1).getLongitude();
        //String waypoint = "%7C";
        String waypoint = "optimize:true|";
        for (int i = 1; i < addressLists.size() - 1; i++) {
            //NOTE: %7C คือ |
            if (addressLists.get(i).getLatitude() != 0.0 && addressLists.get(i).getLongitude() != 0.0) {
                waypoint = waypoint + "|" + addressLists.get(i).getLatitude() + "," + addressLists.get(i).getLongitude();
            }
        }

        Call<JsonElement> call = HttpManager.getInstance().getService().loadGoogleDirectionsWithAvoid(origin, destination, waypoint);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("opDisAvoid :r ", response.toString());
                if (response.isSuccessful()) {
                    progressDia.dismiss();
                    JsonElement jsonElement = response.body();
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.get("status").getAsString().equals("OK")) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("routes");
                        ArrayList<Double> distanceArray = new ArrayList<>();
                        ArrayList<Integer> durationArray = new ArrayList<>();

                        for(int i = 0; i < jsonArray.size(); i++) {
                            JsonArray legs = jsonArray.get(i).getAsJsonObject().getAsJsonArray("legs");
                            distance1 = 0;
                            estimateTime1 = 0;
                            for (int j = 0; j < legs.size(); j++) {
                                distance1 = distance1 + legs.get(j).
                                        getAsJsonObject().getAsJsonObject("distance").get("value").getAsDouble();
                                estimateTime1 = estimateTime1 + legs.get(j).
                                        getAsJsonObject().getAsJsonObject("duration").get("value").getAsInt();
                            }
                            distanceArray.add(distance1);
                            durationArray.add(estimateTime1);
                        }

                        distance1 = Collections.min(distanceArray);
                        estimateTime1 = Collections.min(durationArray);
                        getOptimizedDistance();
                    } else {
                        tvEstimatedFare.setText("-");
                    }
                } else {
                    try {
                        Utils.showToast(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                } else {
                    Utils.showToast(t.toString());
                }
            }
        });


    }

    private void getOptimizedDistance() {
        String origin = addressLists.get(0).getLatitude() + "," + addressLists.get(0).getLongitude();
        String destination = addressLists.get(addressLists.size() - 1).getLatitude() + "," + addressLists.get(addressLists.size() - 1).getLongitude();
        //String waypoint = "%7C";
        String waypoint = "optimize:true|";
        for (int i = 1; i < addressLists.size() - 1; i++) {
            //NOTE: %7C คือ |
            if(addressLists.get(i).getLatitude() != 0.0 && addressLists.get(i).getLongitude() != 0.0) {
                waypoint = waypoint + "|" + addressLists.get(i).getLatitude() + "," + addressLists.get(i).getLongitude();
            }
        }
        Log.d("Tag","tokenId 2 : "+ SPUtils.getString(getContext(), Value.API_KEY));
        Log.d("Tag","origin 2 : "+origin);
        Log.d("Tag","destination 2 : "+destination);
        Log.d("Tag","waypoint 2 : "+waypoint);
        Log.d("Tag","registerSource 2 : "+"2");
        Call<JsonElement> call = HttpManager.getInstance().getService().loadGoogleDirections(origin, destination, waypoint);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("opDis:r",response.toString());
                if(response.isSuccessful()) {
                    progressDia.dismiss();
                    JsonElement jsonElement = response.body();
                    JsonObject jsonObject =  jsonElement.getAsJsonObject();
                    if(jsonObject.get("status").getAsString().equals("OK")) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("routes");
                        ArrayList<Double> distanceArray = new ArrayList<>();
                        ArrayList<Integer> durationArray = new ArrayList<>();

                        for(int i = 0; i < jsonArray.size(); i++) {
                            JsonArray legs = jsonArray.get(i).getAsJsonObject().getAsJsonArray("legs");
                            distance2 = 0;
                            estimateTime2 = 0;
                            for (int j = 0; j < legs.size(); j++) {
                                distance2 = distance2 + legs.get(j).
                                        getAsJsonObject().getAsJsonObject("distance").get("value").getAsDouble();
                                estimateTime2 = estimateTime2 + legs.get(j).
                                        getAsJsonObject().getAsJsonObject("duration").get("value").getAsInt();
                            }
                            distanceArray.add(distance2);
                            durationArray.add(estimateTime2);
                        }

                        distance2 = Collections.min(distanceArray);
                        estimateTime2 = Collections.min(durationArray);
                        Log.d("distance2: ", distance2 + "");
                        Log.d("estimatetime2: ", estimateTime2 + "");

                        if(distance1 <= distance2) {
                            distance = distance1;
                            estimateTime = estimateTime1;
                        }
                        else {
                            distance = distance2;
                            estimateTime = estimateTime2;
                        }

                        distanceStr = String.format("%.02f", distance / 1000);
                        Log.d("distance: " , distanceStr);
                        price = Utils.convertDistanceToPrice(distanceStr);
                        discountPriceByPromo();
                    }
                    else {
                        tvEstimatedFare.setText("-");
                    }
                }
                else {
                    tvEstimatedFare.setText("-");
                    try {
                        Utils.showToast(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                tvEstimatedFare.setText("-");
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                }
                else  {
                    Utils.showToast(t.toString());
                }
            }
        });
    }

    private void getMainData() {
        Call<MainDataDao> call = HttpManager.getInstance().getService().getMainData(SPUtils.getString(getContext(), Value.API_KEY));
        call.enqueue(new Callback<MainDataDao>() {
            @Override
            public void onResponse(Call<MainDataDao> call, Response<MainDataDao> response) {
                Log.d("getMaindata",response.toString());
                progressDia.dismiss();
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo Card and reward : "+jsonFromPojo);
                    MainDataDao dao = response.body();
                    if(dao.getResult() == 1) {
                        if(dao.getReward_badge() == 1) {
                            ivDot.setVisibility(View.VISIBLE);
                    }
                        else {
                            ivDot.setVisibility(View.GONE);
                        }
                    }
                    else {
                        alert = new UDIDAlert(getActivity(), "UDID", dao.getMessage(), 1);
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
                }
            }

            @Override
            public void onFailure(Call<MainDataDao> call, Throwable t) {
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

    private void getAvailDriver(Double latitude, Double longtitude) {
        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longtitude);
        String latlng = lat + "," + lng;
        Call<DriverCollectionDao> call = HttpManager.getInstance().getService().getDriverAvailable(latlng);
        call.enqueue(new Callback<DriverCollectionDao>() {
            @Override
            public void onResponse(Call<DriverCollectionDao> call, Response<DriverCollectionDao> response) {
                Log.d("getAvailDriver",response.toString());
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo never driver : "+jsonFromPojo);
                    DriverCollectionDao dao = response.body();
                    if(dao.getResult() == 1) {
                        List<DriverDao> drivers = dao.getData();
                        if(drivers != null) {
                            setEtaAndPinDriverMarker(drivers);
                        }
                    }
                    else {
                        tvEstimatedTime.setText("-");
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
            public void onFailure(Call<DriverCollectionDao> call, Throwable t) {
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                }
                else  {
                    Utils.showToast(t.toString());
                }
            }
        });
    }

    private void checkCard() {
        progressDia = new ProgressDialog(getContext());
        progressDia.setMessage("Check Card..");
        progressDia.setCancelable(false);
        progressDia.show();

        Call<CheckCreditCardDao> call = HttpManager.getInstance()
                .getService()
                .checkCreditCard(
                        SPUtils.getString(getContext(), Value.CARD),
                        SPUtils.getString(getContext(), Value.API_KEY));
        call.enqueue(new Callback<CheckCreditCardDao>() {
            @Override
            public void onResponse(Call<CheckCreditCardDao> call, Response<CheckCreditCardDao> response) {
                Log.d("checkCard",response.toString());
                progressDia.dismiss();
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo Check card avaliable : "+jsonFromPojo);
                    CheckCreditCardDao dao = response.body();
                    if(dao.getResult() == 1) {
                        if(!tvPromo.getText().toString().equals("") || tvPromo.getText().toString().length() > 0) {
                            checkPromo(tvPromo.getText().toString());
                        }
                        else {
                            addService();
                        }
                    }
                    else {
                        alert = new UDIDAlert(getActivity(), "Credit Card", dao.getMessage(), 1);
                        alert.UDIDAlert.show();
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
            public void onFailure(Call<CheckCreditCardDao> call, Throwable t) {
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

    private void checkPromo(final String promo) {
        progressDia = new ProgressDialog(getContext());
        progressDia.setMessage("Check Promo..");
        progressDia.setCancelable(false);
        progressDia.show();

        String latlng = addressLists.get(0).getLatitude() + "," + addressLists.get(0).getLongitude();
        if(tvTime.getText().toString().equals("Now") || selectDate == null) {
            Calendar date = Calendar.getInstance();
            long t = date.getTimeInMillis();
            selectDate = new Date(t + (30 * ONE_MINUTE_IN_MILLIS));
        }
        Log.d("Tag","code : "+promo);
        Log.d("Tag","api_token : "+ SPUtils.getString(getContext(), Value.API_KEY));
        Log.d("Tag","pick : "+sdfd.format(selectDate));
        Log.d("Tag","latlng : "+latlng);
        Log.d("Tag","imei : "+imei);

        Call<PromoDao> call = HttpManager.getInstance().getService().checkPromo(promo, SPUtils.getString(getContext(), Value.API_KEY), sdfd.format(selectDate), latlng, imei);
        call.enqueue(new Callback<PromoDao>() {
            @Override
            public void onResponse(Call<PromoDao> call, Response<PromoDao> response) {
                Log.d("checkPromo",response.toString());
                progressDia.dismiss();
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo check pro mo : "+jsonFromPojo);
                    PromoDao dao = response.body();
                    if(dao.getResult() == 1) {
                        promo_discount = dao.getDiscount_num();
                        promo_type = dao.getType();
                        discountPriceByPromo();
                        alert = new UDIDAlert(getActivity(), "Promotion Code", dao.getMessage() + "\n" + dao.getDiscount(), 1);
                        alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alert.UDIDAlert.dismiss();
                                if(bookingPressed) {
                                    addService();
                                }
                            }
                        });
                        alert.UDIDAlert.show();
                    }
                    else {
                        tvPromo.setText("");
                        promo_discount = 0;
                        tvEstimatedFare.setText(price + "THB");
                        alert = new UDIDAlert(getActivity(), "Promotion Code", dao.getMessage(), 1);
                        alert.UDIDAlert.show();
                    }
                }
                else {
                    tvPromo.setText("");
                    promo_discount = 0;
                    try {
                        Utils.showToast(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PromoDao> call, Throwable t) {
                tvPromo.setText("");
                promo_discount = 0;
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

    private void addService() {
        progressDia = new ProgressDialog(getContext());
        progressDia.setMessage("Loading..");
        progressDia.setCancelable(false);
        progressDia.show();
        btnBooking.setEnabled(false);

        if(distance == 0) {
            progressDia.dismiss();
            alert = new UDIDAlert(getActivity(), "เกิดข้อผิดพลาด", "กรุณาเลือกสถานที่ใหม่", 1);
            alert.dBtnOK.setText("Done");
            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.UDIDAlert.dismiss();
                    return;
                }
            });
            alert.UDIDAlert.show();
            btnBooking.setEnabled(true);
        }
        else {
            btnBooking.setEnabled(false);
            String name = SPUtils.getString(getContext(), Value.FIRSTNAME) + " " + SPUtils.getString(getContext(), Value.LASTNAME);
//            String sex_id = SPUtils.getString(getContext(), Value.GENDER);
            String sex_id = (SPUtils.getString(getContext(), Value.GENDER)) != ""?SPUtils.getString(getContext(), Value.GENDER):"0";
            String source = addressLists.get(0).getName();
            String source_lat = addressLists.get(0).getLatitude() + "," + addressLists.get(0).getLongitude();

            String destination = addressLists.get(1).getName();
            String destination_lat = addressLists.get(addressLists.size() - 1).getLatitude() + "," + addressLists.get(addressLists.size() - 1).getLongitude();

            String latlng = "";
            String locationName = "";
            for (int i = 1; i < addressLists.size() - 1; i++) {
                if (addressLists.get(i).getLatitude() != 0.0 && addressLists.get(0).getLongitude() != 0.0) {
                    latlng = latlng + "|" + addressLists.get(i).getLatitude() + "," + addressLists.get(i).getLongitude();
                    locationName = locationName + "|" + addressLists.get(i).getAddress();
                }
            }

            String paymentType = "Cash";
            if (!tvPayment.getText().toString().equals("Cash")) {
                paymentType = "Credit";
            }

            String time = "Now";
            if (tvTime.getText().toString().equals("Now") || selectDate == null) {
                Calendar date = Calendar.getInstance();
                long t = date.getTimeInMillis();
                selectDate = new Date(t);
//                time = selectDate.toString();
            } else {
                time = selectDate.toString();
            }


            Bundle bundle = new Bundle();
            bundle.putString("distance", distanceStr);
            bundle.putString("price", String.valueOf(price));
            bundle.putString("transmission", String.valueOf(tvGear.getText()));
            bundle.putString("promotion",tvPromo.getText().toString());
            bundle.putString("payment_type",paymentType);
            bundle.putString("imei",imei);
            bundle.putString("lat_lng",latlng);
            bundle.putString("location_name",locationName);
            bundle.putString("mobile_number",Value.MOBILENO);
            mFirebaseAnalytics.logEvent("complete_booking", bundle);

            Call<AddServiceDao> call = HttpManager.getInstance().getService().addService(
                    SPUtils.getString(getContext(), Value.PLAYERID),
                    SPUtils.getString(getContext(), Value.API_KEY),
                    SPUtils.getString(getContext(), Value.CARD),
                    name,
                    Integer.parseInt(sex_id),
                    tvPhone.getText().toString(),
                    destination_lat,
                    addressLists.get(addressLists.size() - 1).getAddress(),
                    source,
                    source_lat,
                    destination,
                    destination_lat,
                    time,
                    estimateTime,
                    latlng,
                    locationName,
                    "",
                    Double.parseDouble(distanceStr),
                    price,
                    tvGear.getText().toString(),
                    tvDriver.getText().toString(),
                    3,
                    Utils.md5(tvPhone.getText().toString() + source_lat + sdfd.format(selectDate)),
                    tvNote.getText().toString(),
                    tvPromo.getText().toString(),
                    paymentType,
                    imei,
                    getNet());

            Log.d("TagD","player_id : "+SPUtils.getString(getContext(), Value.PLAYERID));
            Log.d("TagD","token_id : "+SPUtils.getString(getContext(), Value.API_KEY));
            Log.d("TagD","token_omise : "+SPUtils.getString(getContext(), Value.CARD));
            Log.d("TagD","codename : "+name);
            Log.d("TagD","sex_id : "+ Integer.parseInt(sex_id));
            Log.d("TagD","phone : "+ tvPhone.getText().toString());
            Log.d("TagD","address_lat : "+destination_lat);
            Log.d("TagD","address_auto : "+addressLists.get(addressLists.size() - 1).getAddress());
            Log.d("TagD","source : "+source);
            Log.d("TagD","source_lat : "+source_lat);
            Log.d("TagD","destination : "+destination);
            Log.d("TagD","destination_lat : "+destination_lat);
            Log.d("TagD","pickup_time : "+time);
            Log.d("TagD","estimate_finish_time : "+estimateTime);
            Log.d("TagD","latlng : "+latlng);
            Log.d("TagD","location_name : "+locationName);
            Log.d("TagD","waypoint_order : "+"");
            Log.d("TagD","distance : "+Double.parseDouble(distanceStr));
            Log.d("TagD","price : "+price);
            Log.d("TagD","gear : "+ tvGear.getText().toString());
            Log.d("TagD","remark : "+tvDriver.getText().toString());
            Log.d("TagD","data_source : "+3);
            Log.d("TagD","nonce : "+Utils.md5(tvPhone.getText().toString() + source_lat + sdfd.format(selectDate)));
            Log.d("TagD","customer_note : "+tvNote.getText().toString());
            Log.d("TagD","promotion_code : "+tvPromo.getText().toString());
            Log.d("TagD","paymentType : "+paymentType);
            Log.d("TagD","imei : "+imei);
            Log.d("TagD","net_fee : "+getNet()) ;

            call.enqueue(new Callback<AddServiceDao>() {
                @Override
                public void onResponse(Call<AddServiceDao> call, final Response<AddServiceDao> response) {
                    progressDia.dismiss();
                    if (response.isSuccessful()) {
                        Gson gsonBuilder = new GsonBuilder().create();
                        String jsonFromPojo = gsonBuilder.toJson(response.body());
                        Log.d("TagD","jsonFromPojo add service : "+jsonFromPojo);

                        final AddServiceDao dao = response.body();
                        Log.d("Tag","dao : "+dao);
                        if (dao.getResult() == 1) {
                            Bundle bundle = new Bundle();
                            mFirebaseAnalytics.logEvent("complete_booking", bundle);

                            alert = new UDIDAlert(getActivity(), "Reservation Complete", "", 1, R.drawable.reserve_com);
                            alert.dBtnOK.setText("Done");
                            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.UDIDAlert.dismiss();
                                    saveDefaultField();
                                    SPUtils.set(getContext(), Value.SERVICE, true);
                                    SPUtils.set(getContext(), Value.TRIP_ID, dao.getId());
                                    Intent intent = new Intent(getContext(), ActivityDriver.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });
                            alert.UDIDAlert.show();
                        } else {
                            alert = new UDIDAlert(getActivity(), "ALERT", dao.getMessage().toString(), 1);
                            alert.UDIDAlert.show();
                            btnBooking.setEnabled(true);
                        }
                    } else {
                        try {
                            Utils.showToast(response.errorBody().string());
                            btnBooking.setEnabled(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AddServiceDao> call, Throwable t) {
                    progressDia.dismiss();
                    if (t instanceof SocketTimeoutException) {
                        Utils.showToast("Connection Time out. Please try again.");
                    } else {
                        Utils.showToast(t.toString());
                    }
                }
            });
        }
    }

    public void showKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void onNotiPopup(String url_popup) {
        mWebView.setBackgroundColor(Color.DKGRAY);
        mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url_popup);
        rlWebView.setVisibility(View.VISIBLE);
    }



}
