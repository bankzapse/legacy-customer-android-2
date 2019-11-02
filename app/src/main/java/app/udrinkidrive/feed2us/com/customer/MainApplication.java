package app.udrinkidrive.feed2us.com.customer;

//import android.support.multidex.MultiDexApplication;

import androidx.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;

import io.branch.referral.Branch;
import manager.http.HTTPEngine;
import app.udrinkidrive.feed2us.com.customer.service.Contextor;

//import com.onesignal.OneSignal;

/**
 * Created by TL3 on 10/10/15 AD.
 */
public class MainApplication extends MultiDexApplication {
    //Logging TAG
    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize thing(s) here
        Contextor.getInstance().init(getApplicationContext());
        HTTPEngine.getInstance().init(getApplicationContext());
//        FacebookSdk.sdkInitialize(getApplicationContext());
        // Initialize the Branch object
        Branch.getAutoInstance(this);
        // OneSignal Initialization
//        OneSignal
//        .startInit(this)
//        .unsubscribeWhenNotificationsAreDisabled(true)
//        .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//        .setNotificationReceivedHandler(new NotificationReceivedHandler())
//        .init();
    }

    public MainApplication() {
        super();
    }

}
