package app.udrinkidrive.feed2us.com.customer.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.udrinkidrive.feed2us.com.customer.R;

/**
 * Created by TL3 on 24/11/2015.
 */
public class UDIDAlert {

    String title;
    String name;
    int resID;
    int numButton;

    //Dialog
    public Dialog UDIDAlert;
    public TextView dTitle;
    public TextView dMessage;
    public ImageView dImage;
    public LinearLayout llYesNo;
    public Button dBtnNo;
    public Button dBtnYes;
    public Button dBtnOK;

    private UDIDAlert(){

    }

    public UDIDAlert(Activity activity, String title , String name, int numButton){
        this.title = title;
        this.name = name;
        this.numButton = numButton;

        bindWidget(activity);
        setUDIDAlert();
        setTitle();
        setMessage();
    }

    public UDIDAlert(Activity activity, String title , String name, int numButton, int resID){
        this.title = title;
        this.name = name;
        this.resID = resID;
        this.numButton = numButton;

        bindWidget(activity);
        setUDIDAlertWithImage();
        setTitle();
        setMessage();
        setImage();
    }

    public void setTitle() {
        dTitle.setText(title);
    }

    public void setMessage() {
        dMessage.setText(name);
    }

    public void setImage() {
        dImage.setImageResource(resID);
    }

    public void setUDIDAlert() {

        dBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UDIDAlert.dismiss();
            }
        });

        if(numButton == 1) {
            llYesNo.setVisibility(View.GONE);
        }
        else {
            dBtnOK.setVisibility(View.GONE);
        }
    }

    public void setUDIDAlertWithImage() {

        dBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UDIDAlert.dismiss();
            }
        });

        dImage.setVisibility(View.VISIBLE);
        if(numButton == 1) {
            llYesNo.setVisibility(View.GONE);
        }
        else {
            dBtnOK.setVisibility(View.GONE);
        }

    }

    private void bindWidget(Activity activity) {

        UDIDAlert = new Dialog(activity);
        UDIDAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        UDIDAlert.setContentView(R.layout.udid_alertview);
        UDIDAlert.setCancelable(false);
        UDIDAlert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dTitle = (TextView) UDIDAlert.findViewById(R.id.dTitle);
        dMessage = (TextView) UDIDAlert.findViewById(R.id.dMessage);
        dImage = (ImageView) UDIDAlert.findViewById(R.id.dImage);
        llYesNo = (LinearLayout) UDIDAlert.findViewById(R.id.llYesNo);
        dBtnYes = (Button) UDIDAlert.findViewById(R.id.dBtnYes);
        dBtnNo = (Button) UDIDAlert.findViewById(R.id.dBtnNo);
        dBtnOK = (Button) UDIDAlert.findViewById(R.id.dBtnOK);

    }

}
