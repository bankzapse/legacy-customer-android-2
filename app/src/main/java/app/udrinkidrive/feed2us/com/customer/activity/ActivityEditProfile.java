package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
//import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.cocosw.bottomsheet.BottomSheet;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import dao.TestDao;
import io.fabric.sdk.android.Fabric;
import manager.http.HTTPEngine;
import manager.http.HTTPEngineListener;
import app.udrinkidrive.feed2us.com.customer.util.FilePickUtils;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;

public class ActivityEditProfile extends FragmentActivity {

    private String TAG = ActivityEditProfile.class.getSimpleName();
    private Context mContext;

    private ProgressDialog dia;

    private int PICK_IMAGE_REQUEST = 1;

    UDIDAlert alert;

    private int hasImage = 0;
    private File imgFromPicker;
    private String selectedImagePath = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.mainLayout) LinearLayout mainLayout;

    @BindView(R.id.ivBack) ImageView ivBack;
    @BindView(R.id.ivSave) ImageView ivSave;

    @BindView(R.id.userImage) ImageView userImage;
    @BindView(R.id.edtFirstname) EditText edtFirstname;
    @BindView(R.id.edtLastname) EditText edtLastname;
    @BindView(R.id.rlGender) RelativeLayout rlGender;
    @BindView(R.id.tvGender) TextView tvGender;
    @BindView(R.id.tvEmail) TextView tvEmail;
    @BindView(R.id.edtMobile) EditText edtMobile;
    @BindView(R.id.tvPassword) TextView tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_editprofile);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setInitView();
        setInitEvent();
    }

    private void setInitView() {
        mContext = this;

        edtFirstname.setText(SPUtils.getString(mContext, Value.FIRSTNAME));
        edtLastname.setText(SPUtils.getString(mContext, Value.LASTNAME));
        tvEmail.setText(SPUtils.getString(mContext, Value.EMAIL));
        edtMobile.setText(SPUtils.getString(mContext, Value.MOBILENO));
        if(SPUtils.getString(mContext, Value.GENDER).equals("2")) {
            tvGender.setText("Male");
        }
        else {
            tvGender.setText("Female");
        }

        loadUserImageFromStorage();

    }

    private void setInitEvent() {

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(ActivityEditProfile.this);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, ActivityProfile.class);
                startActivity(in);
                finish();
            }
        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChange();
            }
        });

        rlGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheet.Builder(ActivityEditProfile.this).title("Gender").sheet(R.menu.list).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.male:
                                tvGender.setText("Male");
                                break;
                            case R.id.female:
                                tvGender.setText("Female");
                                break;
                        }
                    }
                }).show();

            }
        });

        tvPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ActivityChangePass.class));
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT >= 19) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null && !uri.toString().isEmpty()) {
                if(Build.VERSION.SDK_INT >= 19){
                    final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    //noinspection ResourceType
                    ActivityEditProfile.this.getContentResolver()
                            .takePersistableUriPermission(uri, takeFlags);
                }
                String filePath = FilePickUtils.getSmartFilePath(ActivityEditProfile.this, uri);                
                imgFromPicker = new File(filePath);
                Picasso.with(ActivityEditProfile.this).load(imgFromPicker).noFade().into(userImage);
                hasImage = 1;
            }
        }
    }

    private void loadUserImageFromStorage() {
        File f = Utils.loadUserImageFromStorage(ActivityEditProfile.this);
        if(f != null) {
            Picasso.with(ActivityEditProfile.this).load(f).noFade().into(userImage);
        }
        else {
            userImage.setImageResource(R.drawable.usericon);
        }
    }

    private void saveChange() {

        //Check MobileNo
        if ((edtMobile.getText().length() < 10 || edtMobile.getText().length() > 11)
        ||
        (edtMobile.getText().length() == 10 && !edtMobile.getText().toString().substring(0, 1).equals("0"))
        ){
            alert = new UDIDAlert(ActivityEditProfile.this, "Edit Profile", getString(R.string.alert_ep1), 1);
            alert.UDIDAlert.show();

            edtMobile.requestFocus();
        }
        else {
            update();
        }
    }

    private void update() {
        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();
        Map<String, String> param = new HashMap<>();
        param.put("api_token", SPUtils.getString(mContext, Value.API_KEY));
        param.put("first_name", edtFirstname.getText().toString());
        param.put("last_name", edtLastname.getText().toString());
        param.put("phone", edtMobile.getText().toString());

        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("update_profile", bundle);

        Log.d("updateProfile",Value.URL_UPDATE);
        Log.d("updateProfile",param.toString());
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {                
                Log.d("update() :r",rawData);
                JSONObject response = new JSONObject(rawData);
                if (response != null) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response);
                    Log.d("TagD","jsonFromPojo edit profile : "+jsonFromPojo);
                    try {
                        if (response.getString("result").equals("1")) {

                            SPUtils.set(mContext, Value.FIRSTNAME, edtFirstname.getText().toString());
                            SPUtils.set(mContext, Value.LASTNAME, edtLastname.getText().toString());
                            if (tvGender.getText().toString().equals("Male")) {
                                SPUtils.set(mContext, Value.GENDER, "2");
                            } else {
                                SPUtils.set(mContext, Value.GENDER, "1");
                            }
                            SPUtils.set(mContext, Value.MOBILENO, edtMobile.getText().toString());

                            if (hasImage == 1) {
                                userImage.setTag(target);
                                Picasso.with(mContext).load(imgFromPicker).into(target);
                            } else {
                                dia.dismiss();
                                alert = new UDIDAlert(ActivityEditProfile.this, "Edit Profile", response.get("message").toString(), 1);
                                alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.UDIDAlert.dismiss();
                                        finish();
                                    }
                                });
                                alert.UDIDAlert.show();
                            }
                        } else {
                            dia.dismiss();
                            alert = new UDIDAlert(ActivityEditProfile.this, "Edit Profile", response.get("message").toString(), 1);
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

                    } catch (JSONException e) {
                        dia.dismiss();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(TestDao data, String rawData) {
                dia.dismiss();
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityEditProfile.this);
            }
        }, Value.URL_UPDATE, param);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent i = new Intent(ActivityEditProfile.this, ActivityProfile.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(ActivityEditProfile.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            try {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/udid_image");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                String name = new Date().toString() + ".jpg";
                myDir = new File(myDir, name);
                FileOutputStream out = new FileOutputStream(myDir);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                SPUtils.set(mContext, Value.IMAGEURI, name);
                selectedImagePath = root + "/udid_image/" + name;
                new UploadFileAsync().execute(selectedImagePath, Value.URL_UPLOAD);
            } catch (Exception e) {

            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public class UploadFileAsync extends AsyncTask<String, Void, Void> {

        String resServer;

        @SuppressWarnings("deprecation")
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            int resCode = 0;
            String resMessage = "";

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            String strSDPath = params[0];
            String strUrlServer = params[1];

            try {
                /** Check file on SD Card ***/
                File file = new File(strSDPath);

                if (!file.exists()) {                    
                    resServer = "{\"StatusID\":\"0\",\"Error\":\"Please check path on SD Card\"}";
                    return null;
                }

                FileInputStream fileInputStream = new FileInputStream(new File(strSDPath));

                URL url = new URL(strUrlServer);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"upload\";filename=\""
                        + strSDPath + "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                outputStream.writeBytes(lineEnd);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"api_token\"" + lineEnd + lineEnd);
                outputStream.writeBytes(SPUtils.getString(mContext, Value.API_KEY));
                outputStream.writeBytes(lineEnd);

                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Response Code and Message
                resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    int read = 0;
                    while ((read = is.read()) != -1) {
                        bos.write(read);
                    }
                    byte[] result = bos.toByteArray();
                    bos.close();

                    resMessage = new String(result);

                    resServer = resMessage.toString();

                    JSONObject response = new JSONObject(resServer);
                    
                    if (response.get("result").toString().equals("1")) {
                        if (selectedImagePath != null && selectedImagePath != "") {                            
                            dia.dismiss();
                        }

                    } else {
                        // Loaded but has some error
                        dia.dismiss();
                    }
                }

                Log.d("resCode=", Integer.toString(resCode));

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();


            } catch (Exception ex) {
                // Exception handling
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dia.dismiss();
            alert = new UDIDAlert(ActivityEditProfile.this, "Edit Profile", "Upload Successfully", 1);
            alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.UDIDAlert.dismiss();
                    finish();
                }
            });
            alert.UDIDAlert.show();
        }
    }

}


