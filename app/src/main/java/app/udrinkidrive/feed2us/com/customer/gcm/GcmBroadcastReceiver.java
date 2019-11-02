package app.udrinkidrive.feed2us.com.customer.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
//import android.support.v4.app.NotificationCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import app.udrinkidrive.feed2us.com.customer.activity.ActivityLogin;
import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.activity.MainActivity;
import app.udrinkidrive.feed2us.com.customer.event.PopupEvent;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import de.greenrobot.event.EventBus;

/**
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a
 * partial wake lock for your app. It passes off the work of processing the GCM
 * message to an {@code IntentService}, while ensuring that the device does not
 * go back to sleep in the transition. The {@code IntentService} calls
 * {@code GcmBroadcastReceiver.completeWakefulIntent()} when it is ready to
 * release the wake lock.
 */

public class GcmBroadcastReceiver extends BroadcastReceiver {

    private String TAG = GcmBroadcastReceiver.class.getSimpleName();
    private final static int MAX_VOLUME = 100;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Check notification from bank
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context.getApplicationContext());
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            Log.d("Tag", "Received : " + extras.toString());
            Log.d("Tag", "Received alert : " + extras.getString("custom"));
            try {
                JSONObject ob_state = new JSONObject(extras.getString("custom"));
                JSONObject ob_a_state = new JSONObject(ob_state.getString("a"));
                String state = ob_a_state.getString("state");
                Log.d("Tag", "state : " + state);
                if(state.equalsIgnoreCase("JOB_CANCELLED")){
                    context.sendBroadcast(new Intent("Cancel"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }// End code notification cancel

        try {
            String rawReferrerString = intent.getStringExtra("referrer");
            if(rawReferrerString != null) {
                Log.d("Tag", "Received the following intent " + rawReferrerString);
            }
            if(intent.getExtras().getString("params")!=null) {
                String jsonData = intent.getExtras().getString("params");
                //JSONArray params = new JSONArray(jsonData);
                JSONObject notification = new JSONObject(jsonData);
                Log.d("Tag", "context : "+notification.optString("alert"));
                if (notification.optString("action").equals("chat")) {                
                    GcmIntentService cmnService = new GcmIntentService();
                    Notification(context, notification.optString("alert"), notification.optString("action"));
                }
                else if (notification.optString("action").equals("actionPopup")){
                    EventBus.getDefault().post(new PopupEvent(notification.optString("url_popup")));
                    //SPUtils.set(context, "url_popup", notification.optString("url_popup"));
                    Notification(context,notification.optString("alert"), notification.optString("action"),notification.optString("url_popup"));
                }
                else if (notification.optString("action").equals("logout")){
                    Utils.logout(context);
                    Intent i = new Intent(context.getApplicationContext(), ActivityLogin.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    Notification(context,notification.optString("alert"), notification.optString("action"));
                }
                else {
                    Notification(context, notification.optString("alert"), notification.optString("action"));
                }


            }

        }
        catch(JSONException e){            
            //Toast.makeText(context, "Something went wrong with the notification", Toast.LENGTH_SHORT).show();
        }
    }

    public void Notification(Context context, String message, String action) {
        String strtitle = context.getString(R.string.app_name);
        Intent intent = new Intent(context, ActivityLogin.class);
        intent.putExtra("action", action);
        intent.putExtra("title", strtitle);
        intent.putExtra("text", message);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(message)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                //.addAction(R.drawable.ic_launcher, "Action Button", pIntent)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        MediaPlayer thePlayer = MediaPlayer.create(context, R.raw.noti);

        try {
            thePlayer.setVolume(MAX_VOLUME,MAX_VOLUME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        thePlayer.start();
        //builder.setSound(alarmSound);


        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify((int)((new Date().getTime() / 1000L) % Integer.MAX_VALUE), builder.build());

    }

    public void Notification(Context context, String message, String action, String url_popup) {
        String strtitle = context.getString(R.string.app_name);
        //Intent intent = new Intent(context, ActivityHome.class);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("url_popup",url_popup);
        intent.putExtra("action", action);
        intent.putExtra("title", strtitle);
        intent.putExtra("text", message);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(message)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                //.addAction(R.drawable.ic_launcher, "Action Button", pIntent)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        MediaPlayer thePlayer = MediaPlayer.create(context, R.raw.noti);

        try {
            thePlayer.setVolume(MAX_VOLUME,MAX_VOLUME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        thePlayer.start();
        //builder.setSound(alarmSound);


        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(0, builder.build());

    }

    // Check for network availability
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}