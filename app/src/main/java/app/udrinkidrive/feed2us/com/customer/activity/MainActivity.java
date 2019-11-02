package app.udrinkidrive.feed2us.com.customer.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.fragment.MainFragment;
import app.udrinkidrive.feed2us.com.customer.service.GeocoderTask;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import butterknife.ButterKnife;

//import com.onesignal.OSSubscriptionObserver;
//import com.onesignal.OSSubscriptionStateChanges;
//import com.onesignal.OneSignal;

public class MainActivity extends FragmentActivity implements   GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener, ResultCallback<LocationSettingsResult> {

    private MainFragment mainFragment;

    private final int PERMISSION_LOCATION = 111;
    private static final int REQUEST_PHONE_CALL = 1;
    private final int REQ_GPS = 66;
    private final long UPDATE_INTERVAL = 15000;  //60วิ
    private final long FASTEST_INTERVAL = 15000; //59วิ

    private LocationSettingsRequest.Builder builder;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest req;

    private Boolean isFirstTime = true;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        OneSignal.clearOneSignalNotifications();
        mContext = this;
//        OneSignal.addSubscriptionObserver(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initInstances();

        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.contentContainer);

        if(mainFragment == null) {
            mainFragment = MainFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contentContainer, mainFragment)
                    .commit();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("url_popup")) {
                final String url_popup = extras.getString("url_popup");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mainFragment != null) {
                            mainFragment.onNotiPopup(url_popup);
                        }
                    }
                }, 5000);
            }
        }
        checkphone();

    }

    private void initInstances() {
        createLocationRequest();
    }

    private void createLocationRequest() {
        req = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        else {
            startLocationServices();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Utils.showToast("Connection is susppended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utils.showToast("Connection is failed!");
    }

    @Override
    public void onLocationChanged(Location location) {
        if(isFirstTime) {
            if (location != null) {
                GeocoderTask geocoderTask = new GeocoderTask() {
                    @Override
                    protected void onPostExecute(List<Address> addressList) {
                        String name = "";
                        String address = "";
                        if (addressList == null || addressList.size() == 0) {
                            Log.d("Geocoder: ", "No Location found");
                            isFirstTime = true;
                        }
                        else {
                            int maxAddressLines = addressList.get(0).getMaxAddressLineIndex();
                            for (int i = 0; i < maxAddressLines; i++) {
                                address = address + addressList.get(0).getAddressLine(i) + " ";
                            }
                            name = addressList.get(0).getAddressLine(0);
                            Log.d("Geocoder: ", address);
                            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            System.out.println("Emi::" + telephonyManager.getDeviceId());
                            mainFragment.setPickupMarker(name, address, new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude()), telephonyManager.getDeviceId());
                            isFirstTime = false;
                        }
                    }
                };
                geocoderTask.execute(location.getLatitude(), location.getLongitude());
                isFirstTime = false;
            }
        }

        mainFragment.onLocationChanged(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SPUtils.set(MainActivity.this, "show_badge", 0);

        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }
        mGoogleApiClient.connect();

        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(req)
                .setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED && requestCode == REQ_GPS) {
            finish();
        }
    }

    public void checkphone(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED  ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE }, REQUEST_PHONE_CALL);

        }

    }

    public void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
            Log.d("checkper: ", "Requesting permissions");
        }
        else {
            Log.d("checkper: ", "Starting Location Services from onConnected");
            startLocationServices();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {                    
                    startLocationServices();
                }
                else {
                    //show a dialog saying somthing like, "I can't run your location dummy - you defined permission!"                    
                }
            }
        }
    }

    public void startLocationServices() {
        Log.d("startLocationServices: ", "Starting Location Services Called");

        try {            
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, req, this);

        } catch (SecurityException exception) {
            //show dialog to user saying we can't get location unless they give app permission            
        }

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.d("GPS Status: ", "All location settings are satisfied.");
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.d("GPS Status: ", "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MainActivity.this, REQ_GPS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i("GPS Status: ", "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.d("GPS Status: ", "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

//    @Override
//    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
//        if (!stateChanges.getFrom().getSubscribed() && stateChanges.getTo().getSubscribed()) {
//            new AlertDialog.Builder(this)
//                    .setMessage("You've successfully subscribed to push notifications!")
//                    .show();
//            // get player ID
//            stateChanges.getTo().getUserId();
//        }
//
//        SPUtils.set(mContext, Value.PLAYERID, stateChanges.getTo().getUserId());
//
//        Log.i("Debug", "onOSPermissionChanged: " + stateChanges.getTo().getUserId());
//    }
}
