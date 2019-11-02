package app.udrinkidrive.feed2us.com.customer.view;

import android.app.Activity;
import android.app.Dialog;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import app.udrinkidrive.feed2us.com.customer.R;

/**
 * Created by TL3 on 22/12/2015.
 */
public class RewardAlert {

    String name;
    String image_thumbnail;
    String price;
    String description;

    //Dialog
    public Dialog RewardAlert;
    public TextView dName;
    public ImageView dImage;
    public Button dBtnYes;
    public Button dBtnNo;
    public TextView dPrice;
    public TextView dDescription;

    public RewardAlert(){

    }

    public RewardAlert(Activity activity, String name, String image_thumbnail, String price, String description){
        this.name = name;
        this.image_thumbnail = image_thumbnail;
        this.price = price;
        this.description = description;

        bindWidget(activity);
        setUDIDAlertWithImage();
        setName();
        setImage(activity);
        setPrice();
        setDescription();
    }

    public void setName() {
        dName.setText(name);
    }

    public void setImage(Activity activity) {
        Picasso.with(activity.getApplicationContext()).load(image_thumbnail).noFade().into(dImage);
    }

    public void setPrice() {
        String formattedPrice = new DecimalFormat("##,### baht").format(Double.parseDouble(price));
        dPrice.setText(formattedPrice);
    }

    public void setDescription() {
        dDescription.setText(description);
    }

    public void setUDIDAlertWithImage() {

        dBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewardAlert.dismiss();
            }
        });

        dBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewardAlert.dismiss();
            }
        });
    }

    private void bindWidget(Activity activity) {

        RewardAlert = new Dialog(activity);
        RewardAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RewardAlert.setContentView(R.layout.reward_alertview);
        RewardAlert.setCancelable(false);
        RewardAlert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dName = (TextView) RewardAlert.findViewById(R.id.dName);
        dImage = (ImageView) RewardAlert.findViewById(R.id.dImage);
        dBtnYes = (Button) RewardAlert.findViewById(R.id.dBtnYes);
        dBtnNo = (Button) RewardAlert.findViewById(R.id.dBtnNo);
        dPrice = (TextView) RewardAlert.findViewById(R.id.dPrice);
        dDescription = (TextView) RewardAlert.findViewById(R.id.dDescription);

        dDescription.setMovementMethod(new ScrollingMovementMethod());
    }

}
