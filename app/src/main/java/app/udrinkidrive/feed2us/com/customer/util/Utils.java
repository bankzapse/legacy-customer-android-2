package app.udrinkidrive.feed2us.com.customer.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.udrinkidrive.feed2us.com.customer.service.Contextor;


/**
 * Created by TL3 on 8/8/2016.
 */
public class Utils {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static Utils instance;
    public final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    public String[] namesOfDays = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};


    public static Utils getInstance() {
        if (instance == null)
            instance = new Utils();
        return instance;
    }

    private Utils() {

    }

    private Context mContext;

    public void init(Context context) {
        mContext = context;
    }

    public String getDeviceId() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getVersionName() {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }

    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }

    public String getDifferenceDay(Date sessionStart, Date sessionEnd) {
        if(sessionStart == null)
            return "[corrupted]";

        Calendar startDateTime = Calendar.getInstance();
        startDateTime.setTime(sessionStart);

        Calendar endDateTime = Calendar.getInstance();
        endDateTime.setTime(sessionEnd);

        long milliseconds1 = startDateTime.getTimeInMillis();
        long milliseconds2 = endDateTime.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;

        long hours = diff / (60 * 60 * 1000);
        long minutes = diff / (60 * 1000);
        minutes = minutes - 60 * hours;
        long seconds = diff / (1000);

        long day = hours/24;
        long month = day/24;

        /*if (day > 30 ) {
            return month+ " months "+(day%30)+" days";
        }else if (hours > 24 ) {
            return (hours/24)+ " days "+(hours%24)+" hours";
        }else if (hours > 0) {
            return hours + " hours " + minutes + " minutes";
        } else {
            if (minutes > 0)
                return minutes + " minutes";
            else {
                return seconds + " seconds";
            }
        }*/
        if (day > 30 ) {
            return month+ " months ";
        }else if (hours > 24 ) {
            return (hours/24)+ " days ";
        } else {
            return "1 days ";
        }
    }

    public byte[] uri2byte(Context context, Uri uri) throws IOException {
        // this dynamically extends to take the bytes you read
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public byte[] url2byte(String filePath) throws FileNotFoundException, IOException {

        File file = new File(filePath);
        System.out.println(file.exists() + "!!");

        FileInputStream fis = new FileInputStream(file);
        //create FileInputStream which obtains input bytes from a file in a file system
        //FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.

        //InputStream in = resource.openStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
                //no doubt here is 0
                /*Writes len bytes from the specified byte array starting at offset
                off to this byte array output stream.*/
                System.out.println("read " + readNum + " bytes,");
            }
        } catch (IOException ex) {
            Log.d("error", "error");
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static boolean validateEmailAddress(String emailAddress) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }
    /**
     * Generate a value suitable for use in .
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public void showGPSDisabledAlertToUser(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("กรุณาเปิด GPS ก่อนเข้าใช้งาน")
                .setCancelable(false)
                .setPositiveButton("ยอมรับ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static void snackbarAlertNetwork(Context context, Activity activity) {
        Toast.makeText(Contextor.getInstance().getContext(),
                "กรุณาเชื่อมต่ออินเตอร์เน็ต !",
                Toast.LENGTH_SHORT)
                .show();
    }

    public static void showToast(String text) {
        Toast.makeText(Contextor.getInstance().getContext(),
                text,
                Toast.LENGTH_SHORT)
                .show();
    }

    public String padding_str(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "UDrinkIDrive");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        File mediaFile;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static File loadUserImageFromStorage(Context context)
    {
        try {
            if(!SPUtils.getString(context, Value.IMAGEURI).equals("")) {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/udid_image");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                String name = SPUtils.getString(context, Value.IMAGEURI);
                myDir = new File(myDir, name);
                if (myDir.isFile()) {
                    return myDir;
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static boolean checkImageResource(Context ctx, ImageView imageView, int imageResource) {
        boolean result = false;

        if (ctx != null && imageView != null && imageView.getDrawable() != null) {
            Drawable.ConstantState constantState;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                constantState = ctx.getResources()
                        .getDrawable(imageResource, ctx.getTheme())
                        .getConstantState();
            }
            else {
                constantState = ctx.getResources().getDrawable(imageResource)
                        .getConstantState();
            }

            if (imageView.getDrawable().getConstantState() == constantState) {
                result = true;
            }
        }

        return result;
    }

    public static int convertDistanceToPrice(String distanceStr) {

        int distance = Math.round(Float.parseFloat(distanceStr));

        int price = 500;

        if(distance > 5 && distance <= 10) {
            price += 50;
        }
        else if(distance > 10 && distance <= 15) {
            price += 100;
        }
        else if(distance > 15 && distance <= 20) {
            price += 150;
        }
        else if(distance > 20 && distance <= 25) {
            price += 200;
        }
        else if(distance > 25 && distance <= 30) {
            price += 300;
        }
        else if(distance > 30 && distance <= 35) {
            price += 400;
        }
        else if(distance > 35 && distance <= 40) {
            price += 700;
        }
        else if(distance > 40 && distance <= 50) {
            price += 1000;
        }
        else if(distance > 50 && distance <= 60) {
            price += 1300;
        }
        else if(distance > 60) {
            price += 1300;
            price += (distance - 60) * 30;
        }
        /*if(price > 2500) {
            price = 2500;
        }*/

        return  price;
    }

    public static void logout(Context context) {
        SPUtils.delete(context, Value.IS_LOGIN);
        SPUtils.delete(context, Value.SERVICE);
        SPUtils.delete(context, Value.CARD);
        //SPUtils.delete(context, Value.IMAGEURI);

        File dir = context.getFilesDir();
        File file = new File(dir, "credit.ser");
        file.delete();

        file = new File(dir, "PICKUP.ser");
        file.delete();

        file = new File(dir, "DROPOFF.ser");
        file.delete();
        //boolean deleted = file.delete();

        try {
            if (AccessToken.getCurrentAccessToken() != null || com.facebook.Profile.getCurrentProfile() != null){
                LoginManager.getInstance().logOut();
            }
        }
        catch (Exception e) {

        }
    }
}
