package app.udrinkidrive.feed2us.com.customer.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
//import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.gcm.GcmBroadcastReceiver;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dao.TestDao;
import io.fabric.sdk.android.Fabric;
import manager.http.HTTPEngine;
import manager.http.HTTPEngineListener;
import app.udrinkidrive.feed2us.com.customer.model.ModelDriver;
import app.udrinkidrive.feed2us.com.customer.util.JsonParser;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;

import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;


public class ActivityDriver extends FragmentActivity implements ResultCallback<LocationSettingsResult>,
        OnMapReadyCallback {

    private String TAG = ActivityDriver.class.getSimpleName();
    private Context mContext;

    private ProgressDialog dia;

    private GoogleMap mMapView;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL = 15000;  //15วิ
    private static long FASTEST_INTERVAL = 15000; //15วิ

    private LatLng current;

    private ModelDriver modelDriver;
    private Boolean hasDriver = false;

    private JSONArray badge;
    private String videoUrl = "";

    UDIDAlert alert;

    LocationSettingsRequest.Builder builder;
    private FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.ivMenu)
    ImageView ivMenu;

    @BindView(R.id.noDriverView)
    RelativeLayout noDriverView;
    @BindView(R.id.lblNoDriver)
    TextView lblNoDriver;

    @BindView(R.id.driverView)
    RelativeLayout driverView;
    @BindView(R.id.driver_image)
    ImageView driver_image;
    @BindView(R.id.lblDriverName)
    TextView lblDriverName;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.waitMessageView)
    RelativeLayout waitMessageView;
    @BindView(R.id.lblWaitTime)
    TextView lblWaitTime;
    @BindView(R.id.lblWaitCost)
    TextView lblWaitCost;
    @BindView(R.id.lblWaitDriver)
    TextView lblWaitDriver;

    @BindView(R.id.badge1)
    ImageView badge1;
    @BindView(R.id.badge2)
    ImageView badge2;
    @BindView(R.id.badge3)
    ImageView badge3;
    @BindView(R.id.badge4)
    ImageView badge4;
    @BindView(R.id.badge5)
    ImageView badge5;
    @BindView(R.id.badge6)
    ImageView badge6;

    @BindView(R.id.badgeView)
    RelativeLayout badgeView;
    @BindView(R.id.ivBadge)
    ImageView ivBadge;
    @BindView(R.id.badgeDesc)
    TextView badgeDesc;
    @BindView(R.id.btnVideo)
    Button btnVideo;
    @BindView(R.id.ivClose)
    ImageButton ivClose;

    @BindView(R.id.btContact)
    Button btContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        OneSignal.clearOneSignalNotifications();
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_driver);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setupMap();
        setInitView();
        setInitEvent();
        setupFirst();

        registerReceiver(broadcastReceiver, new IntentFilter("Cancel"));
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mMapView);
        mapFragment.getMapAsync(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapView = googleMap;
        mMapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mMapView.setMyLocationEnabled(true);
        mMapView.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.7563, 100.5018), 18.0f));
        mMapView.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.marker_info_content, null);
                TextView tvDuration = (TextView) v.findViewById(R.id.tvDuration);
                if (marker.getTitle() != null && !marker.getTitle().equals("")) {
                    tvDuration.setText(marker.getTitle());
                    tvDuration.setVisibility(View.VISIBLE);
                } else {
                    tvDuration.setVisibility(View.GONE);
                }

                return v;
            }
        });
    }

    private void setupFirst() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (current != null) {
                    pantoPosition(current);
                    getDriverInfo();
                } else {
                    setupFirst();
                }
            }
        }, 1000);
    }

    private void setInitView() {
        mContext = this;

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);

        badgeDesc.setMovementMethod(new ScrollingMovementMethod());

    }

    @OnClick(R.id.ivMenu)
    public void clickMenu() {
        Intent in = new Intent(mContext, ActivityProfile.class);
        startActivity(in);
    }

    @OnClick(R.id.btCancel)
    public void goCancelTrip() {
        alert = new UDIDAlert(ActivityDriver.this, "Change Reservation", getString(R.string.alert_d3), 2);
        alert.dBtnYes.setText(R.string.alert_d1);
        alert.dBtnYes.setVisibility(View.GONE);
        alert.dBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.UDIDAlert.dismiss();
                alert = new UDIDAlert(ActivityDriver.this, "Change Reservation", getString(R.string.alert_d4), 2);
                alert.dBtnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.UDIDAlert.dismiss();
                    }
                });

                alert.dBtnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.UDIDAlert.dismiss();
                        cancelTrip();
                    }
                });
                alert.UDIDAlert.show();
            }
        });

        alert.dBtnNo.setText(R.string.alert_d2);
        alert.dBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.UDIDAlert.dismiss();
                alert.UDIDAlert.dismiss();
                alert = new UDIDAlert(ActivityDriver.this, "Cancel trip", getString(R.string.alert_d5), 2, R.drawable.crying);
                alert.dBtnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.UDIDAlert.dismiss();
                    }
                });

                alert.dBtnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.UDIDAlert.dismiss();
                        cancelTrip();
                    }
                });
                alert.UDIDAlert.show();
            }
        });
        alert.UDIDAlert.setCancelable(true);
        alert.UDIDAlert.show();
    }

    @OnClick(R.id.btContact)
    public void goContactDriver() {
        if (hasDriver == true) {
            alert = new UDIDAlert(ActivityDriver.this, "Contact", getString(R.string.alert_d6), 2);
            alert.dBtnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.UDIDAlert.dismiss();
                }
            });

            alert.dBtnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.UDIDAlert.dismiss();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + modelDriver.phone_no1));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                }
            });
            alert.UDIDAlert.show();
        } else {
            /*alert = new UDIDAlert(ActivityDriver.this, "Contact Driver", getString(R.string.alert_d7), 1);
            alert.UDIDAlert.show();*/
            alert = new UDIDAlert(ActivityDriver.this, "Contact", "Contact Call Center?", 2);
            alert.dBtnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.UDIDAlert.dismiss();
                }
            });

            alert.dBtnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.UDIDAlert.dismiss();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + "0910809108"));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                }
            });
            alert.UDIDAlert.show();
        }
    }

    @OnClick(R.id.ivClose)
    public void closeBadgeView() {
        badgeView.setVisibility(View.GONE);
    }

    @OnClick(R.id.btnVideo)
    public void openYoutubeVideo() {
        Intent intent = new Intent(ActivityDriver.this, YoutubeActivity.class);
        intent.putExtra("YOUTUBE_VIDEO_ID", videoUrl);
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void setInitEvent() {

    }

    private void pantoPosition(LatLng location) {
        if (mMapView.getCameraPosition() != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 18);
            mMapView.animateCamera(cameraUpdate);
        }
    }

    GcmBroadcastReceiver broadcastReceiver = new GcmBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getDriverInfo();
        }
    };

    public void getDriverInfo() {
        Log.d("Tag", "getDriverInfo top");
        Map<String, String> param = new HashMap<>();
        param.put("api_token", SPUtils.getString(mContext, Value.API_KEY));
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {
//                Log.d("Tag","onResponse");
                JSONObject response = new JSONObject(rawData);
                Gson gsonBuilder = new GsonBuilder().create();
                String jsonFromPojo = gsonBuilder.toJson(response);
                Log.d("TagD", "jsonFromPojo getDriverInfo : " + jsonFromPojo);
                if (response != null) {
                    try {
                        if (response.getString("result").equals("1")) {
                            if (!response.has("data")) {
                                btContact.setText("ติดต่อคอลเซ็นเตอร์");
                                hasDriver = false;
                                driverView.setVisibility(View.GONE);
                                noDriverView.setVisibility(View.VISIBLE);
                                lblNoDriver.setText(response.get("message").toString());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getDriverInfo();
                                    }
                                }, FASTEST_INTERVAL);
                            } else {
                                btContact.setText("ติดต่อพนักงานขับรถ");
                                modelDriver = JsonParser.parseDriver(mContext, response);
                                if (response.has("badge")) {
                                    badge = response.getJSONArray("badge");
                                }
                                setDriverInfo();
                                hasDriver = true;


                                if (!TextUtils.isEmpty(modelDriver.driver_lat)) {
                                    pinDriverMarker(modelDriver.driver_lat, modelDriver.duration);
                                }
                                if (modelDriver.status.equals("D")) {
                                    setWaitMessageWithTime(response.get("message").toString(), modelDriver.wait_time, modelDriver.cost_wait);
                                }
                                if (modelDriver.status.equals("E")) {
                                    waitMessageView.setVisibility(View.GONE);
                                }
                                Log.d("Tag", "modelDriver status : " + modelDriver.status);
                                if (modelDriver.status.equals("I") || modelDriver.status.equals("G")) {
                                    alert = new UDIDAlert(ActivityDriver.this, "Sorry", getString(R.string.alert_d8), 1);
                                    alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alert.UDIDAlert.dismiss();
                                            SPUtils.set(ActivityDriver.this, Value.SERVICE, false);

                                            //startActivity(new Intent(mContext, ActivityHome.class));
                                            startActivity(new Intent(mContext, MainActivity.class));
                                            finish();
                                        }
                                    });
                                    alert.UDIDAlert.show();
                                } else if (modelDriver.status.equals("F") || modelDriver.status.equals("H")) {

                                    JSONArray jsonArray = response.getJSONArray("data");
                                    JSONObject j = jsonArray.getJSONObject(0);

                                    Intent i = new Intent(mContext, ActivityEvaluate.class);
                                    i.putExtra("PICKUPTIME", j.getString("pickup_time"));
                                    i.putExtra("PRICE", j.getString("price"));
                                    i.putExtra("PRICEFINAL", j.getString("price_final"));
                                    i.putExtra("DRIVERINFO", modelDriver);
                                    i.putExtra("PAYMENT", j.getString("payment_type"));
                                    startActivity(i);
                                    finish();
                                } else {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            getDriverInfo();
                                        }
                                    }, FASTEST_INTERVAL);
                                }
                            }
                        } else {
                            if (response.getString("result").equals("0")) {
                                alert = new UDIDAlert(ActivityDriver.this, "Tracking", response.get("message").toString(), 1);
                                alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.UDIDAlert.dismiss();
                                        Utils.logout(mContext);
                                        Intent in = new Intent(mContext, ActivityLogin.class);
                                        startActivity(in);
                                        finish();
                                    }
                                });
                                alert.UDIDAlert.show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Tag", "getDriverInfo bottom");
                }
            }

            @Override
            public void onFailure(TestDao data, String rawData) {

                // Cannot load                
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDriverInfo();
                    }
                }, FASTEST_INTERVAL);
            }
        }, Value.URL_DRIVER_VIEW, param);
    }

    private void setDriverInfo() {

        if (!modelDriver.image_url.equals("")) {
            Picasso.with(mContext).load(modelDriver.image_url).noFade().into(driver_image);
        }

        String sex = "";
        if (modelDriver.sex_id.equals("2")) {
            sex = "นาย ";
        } else {
            sex = "นางสาว ";
        }

        lblDriverName.setText(sex + modelDriver.first_name + " " + modelDriver.last_name);
//        ratingBar.setRating(Float.parseFloat(modelDriver.rating_avg));

//        for(int i = 0; i < badge.length(); i++) {
//            final int index = i;
//            if(index < 6) {
//                try {
//                    switch (i) {
//                        case 0:
//                            Picasso.with(mContext).load(badge.getJSONObject(i).getString("image_thumbnail")).noFade().into(badge1);
//                            badge1.setVisibility(View.VISIBLE);
//                            badge1.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    setBadgeView(index);
//                                }
//                            });
//                            break;
//                        case 1:
//                            Picasso.with(mContext).load(badge.getJSONObject(i).getString("image_thumbnail")).noFade().into(badge2);
//                            badge2.setVisibility(View.VISIBLE);
//                            badge2.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    setBadgeView(index);
//                                }
//                            });
//                            break;
//                        case 2:
//                            Picasso.with(mContext).load(badge.getJSONObject(i).getString("image_thumbnail")).noFade().into(badge3);
//                            badge3.setVisibility(View.VISIBLE);
//                            badge3.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    setBadgeView(index);
//                                }
//                            });
//                            break;
//                        case 3:
//                            Picasso.with(mContext).load(badge.getJSONObject(i).getString("image_thumbnail")).noFade().into(badge4);
//                            badge4.setVisibility(View.VISIBLE);
//                            badge4.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    setBadgeView(index);
//                                }
//                            });
//                            break;
//                        case 4:
//                            Picasso.with(mContext).load(badge.getJSONObject(i).getString("image_thumbnail")).noFade().into(badge5);
//                            badge5.setVisibility(View.VISIBLE);
//                            badge5.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    setBadgeView(index);
//                                }
//                            });
//                            break;
//                        case 5:
//                            Picasso.with(mContext).load(badge.getJSONObject(i).getString("image_thumbnail")).noFade().into(badge6);
//                            badge6.setVisibility(View.VISIBLE);
//                            badge6.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    setBadgeView(index);
//                                }
//                            });
//                            break;
//
//                    }
//                } catch (JSONException e) {
//
//                }
//            }
//        }

//        if(SPUtils.getInt(ActivityDriver.this, "show_badge") == 0) {
//            showFirstBadge();
//        }

        noDriverView.setVisibility(View.GONE);
        driverView.setVisibility(View.VISIBLE);
    }

    private void showFirstBadge() {

        if (badge.length() > 0) {
            try {
                String htmltext = badge.getJSONObject(0).getString("description");
                Spanned sp = Html.fromHtml(htmltext);
                badgeDesc.setText(sp);
                Picasso.with(mContext).load(badge.getJSONObject(0).getString("image_thumbnail")).noFade().into(ivBadge);

                if (badge.getJSONObject(0).isNull("video_url") || badge.getJSONObject(0).getString("video_url").equals("")) {
                    btnVideo.setVisibility(View.GONE);
                } else {
                    btnVideo.setVisibility(View.VISIBLE);
                    String[] urlList = badge.getJSONObject(0).getString("video_url").split("=");
                    videoUrl = urlList[1];
                }
            } catch (JSONException e) {

            }
            badgeView.setVisibility(View.VISIBLE);
        }

        SPUtils.set(mContext, "show_badge", 1);
    }

    private void setBadgeView(int index) {
        try {
            String htmltext = badge.getJSONObject(0).getString("description");
            Spanned sp = Html.fromHtml(htmltext);
            badgeDesc.setText(sp);
            Picasso.with(mContext).load(badge.getJSONObject(index).getString("image_thumbnail")).noFade().into(ivBadge);

            if (badge.getJSONObject(index).isNull("video_url") || badge.getJSONObject(index).getString("video_url").equals("")) {
                btnVideo.setVisibility(View.GONE);
            } else {
                btnVideo.setVisibility(View.VISIBLE);
                String[] urlList = badge.getJSONObject(index).getString("video_url").split("=");
                videoUrl = urlList[1];
            }
        } catch (JSONException e) {

        }
        badgeView.setVisibility(View.VISIBLE);
    }

    private void pinDriverMarker(String _latlng, String _duration) {
        mMapView.clear();
        String latlngStr[] = _latlng.split(",");
        double _lat = Double.parseDouble(latlngStr[0]);
        double _lng = Double.parseDouble(latlngStr[1]);
        LatLng latLng = new LatLng(_lat, _lng);
        mMapView.addMarker(new MarkerOptions().position(latLng).title("DURATION : " + _duration)
                .snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pickup)));//search android icon generate
        mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }

    private void setWaitMessageWithTime(String message, String time, String cost) {

        String formattedCost;

        if (Integer.parseInt(time) < 0) {
            time = "0";
        }

        formattedCost = new DecimalFormat("##,###").format(Double.parseDouble(cost));

        lblWaitTime.setText(getString(R.string.d_wait) + time + getString(R.string.d_min));
        lblWaitCost.setText(getString(R.string.d_charge) + formattedCost + " THB");
        lblWaitDriver.setText(message);

        if (waitMessageView.getVisibility() == View.GONE) {
            waitMessageView.setVisibility(View.VISIBLE);
        }
    }

    private void cancelTrip() {

        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("cancel_trip", bundle);

        Map<String, String> param = new HashMap<>();
        param.put("api_key", SPUtils.getString(mContext, Value.API_KEY));
        Log.d("cancelTrip", Value.URL_SERVICE_CANCEL);
        Log.d("cancelTrip", param.toString());
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {
                JSONObject response = new JSONObject(rawData);
                Gson gsonBuilder = new GsonBuilder().create();
                String jsonFromPojo = gsonBuilder.toJson(response);
                Log.d("TagD", "jsonFromPojo cancelTrip : " + jsonFromPojo);
                if (response != null) {
                    try {
                        if (response.getString("result").equals("1")) {
                            dia.dismiss();
                            SPUtils.set(ActivityDriver.this, Value.SERVICE, false);
                            SPUtils.set(ActivityDriver.this, "show_badge", 0);

                            startActivity(new Intent(mContext, MainActivity.class));
                            finish();
                        } else {
                            dia.dismiss();
                            alert = new UDIDAlert(ActivityDriver.this, "Cancel Trip", response.getString("message"), 1);
                            alert.UDIDAlert.show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(TestDao data, String rawData) {
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityDriver.this);
            }
        }, Value.URL_SERVICE_CANCEL, param);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onConnected(Bundle bundle) {
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    Activity#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for Activity#requestPermissions for more details.
                                return;
                            }
                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {

                                    if (location != null) {
                                        try {
                                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            current = latLng;
                                        } catch (Exception e) {

                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Utils.showToast("Connection is susppended!");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Utils.showToast("Connection is failed!");
                        }
                    }).build();
        }

        googleApiClient.connect();
        //setupMap();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dia != null) {
            dia.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_CANCELED && requestCode == 66) {
            finish();
        }
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(ActivityDriver.this, 66);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }
}




