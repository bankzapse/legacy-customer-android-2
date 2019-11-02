package app.udrinkidrive.feed2us.com.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import app.udrinkidrive.feed2us.com.customer.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class ActivityVoucher extends FragmentActivity {

    private String TAG = ActivityVoucher.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.llBack) ImageView llBack;
    @BindView(R.id.ivRule) ImageView ivRule;
    @BindView(R.id.ivTop) ImageView ivTop;
    @BindView(R.id.tv1) TextView tv1;

    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvDescription) TextView tvDescription;
    @BindView(R.id.tvPrice) TextView tvPrice;
    @BindView(R.id.ivReward) ImageView ivReward;
    @BindView(R.id.ivQrcode) ImageView ivQrcode;
    @BindView(R.id.tvType) TextView tvType;
    @BindView(R.id.tvPromotion) TextView tvPromotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_voucher);
        ButterKnife.bind(this);

        setInitView();
        setInitEvent();
    }

    private void setInitView() {
        mContext = this;

        Intent in = getIntent();
        String promotionCode = in.getStringExtra("PROMOTION");
        String name = in.getStringExtra("NAME");
        String description = in.getStringExtra("DESCRIPTION");
        String price = in.getStringExtra("PRICE");
        String image = in.getStringExtra("IMAGE");
        String voucher = in.getStringExtra("VOUCHER");
        String type = in.getStringExtra("TYPE");
        int req = in.getIntExtra("REQ",0);

        if(req == 1) {
            ivTop.setVisibility(View.VISIBLE);
            tv1.setVisibility(View.VISIBLE);
        }
        else {
            ivTop.setVisibility(View.GONE);
            tv1.setVisibility(View.INVISIBLE);
        }

        if(type.equals("voucher")) {
            tvType.setText("SCAN QR CODE HERE");
            Picasso.with(mContext).load(voucher).noFade().into(ivQrcode);
        }
        else {
            tvType.setText("PROMOTION CODE");
            tvPromotion.setText(promotionCode);
        }
        Picasso.with(mContext).load(image).noFade().into(ivReward);
        tvName.setText(name);
        tvDescription.setText(description);
        tvDescription.setMovementMethod(new ScrollingMovementMethod());
        String formattedPrice = new DecimalFormat("##,### baht").format(Double.parseDouble(price));
        tvPrice.setText(formattedPrice);
    }

    private void setInitEvent() {
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
                setResult(RESULT_OK, in);
                finish();
            }
        });

        ivRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, ActivityRewardRule.class);
                startActivity(in);
            }
        });
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


}


