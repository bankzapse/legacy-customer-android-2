package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.dao.ChangeCreditCardActiveDao;
import app.udrinkidrive.feed2us.com.customer.dao.CreditCardCollectionDao;
import app.udrinkidrive.feed2us.com.customer.dao.CreditCardDao;
import app.udrinkidrive.feed2us.com.customer.dao.DelCreditCardDao;
import app.udrinkidrive.feed2us.com.customer.service.HttpManager;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCreditCard extends ListActivity  {

    private String TAG = ActivityCreditCard.class.getSimpleName();
    private Context mContext;

    private ProgressDialog dia;

    private ArrayList<HashMap<String, String>> cardList = new ArrayList<HashMap<String,String>>();
    public static ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String, String>> arrayListTemp = new ArrayList<HashMap<String,String>>();

    public static int REQ_CREDIT_ADD = 1;
    private int deleting_row = -1;

    UDIDAlert alert;

    @BindView(R.id.llAdd) LinearLayout llAdd;
    @BindView(R.id.btDone) Button btDone;
    @BindView(R.id.llCash) LinearLayout llCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_creditcard);
        ButterKnife.bind(this);

        setInitView();
        setInitEvent();
    }

    private void setInitView() {
        mContext = this;

        getCardList();
        if(SPUtils.getString(mContext, "PAYMENT").toString().equals("") ||
                SPUtils.getString(mContext, "PAYMENT").toString().equals("Cash") ||
                SPUtils.getString(mContext, Value.CARD) == "") {
            SPUtils.set(mContext, "PAYMENT", "Cash");
            llCash.setBackgroundColor(ContextCompat.getColor(ActivityCreditCard.this, R.color.grey400));
        }
        else {
            llCash.setBackgroundColor(ContextCompat.getColor(ActivityCreditCard.this, R.color.greyAlpha));
        }        

        setupCustomListView();
    }

    private void setInitEvent() {

        llAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ActivityPayment.class);
                startActivityForResult(i, REQ_CREDIT_ADD);
                setResult(RESULT_OK);
                finish();
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int selectedIndex, long id) {
                changeCardActive(selectedIndex);
            }
        });

        llCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.set(mContext, "PAYMENT", "Cash");
                llCash.setBackgroundColor(ContextCompat.getColor(ActivityCreditCard.this, R.color.grey400));
                setupCustomListView();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK && requestCode == REQ_CREDIT_ADD){
            setupCustomListView();
        }
    }

    private void setupCustomListView() {
        setListAdapter(new EfficientAdapter(getApplicationContext()));
    }

    public class EfficientAdapter extends BaseAdapter {

        public Context lContext;
        public LayoutInflater mInflater;

        //constructor
        public  EfficientAdapter(Context ctx){
            lContext = ctx;
            // obj to load layout
            mInflater = LayoutInflater.from(lContext);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null){
                // created convertedView and bind Widgets in convertedview
                convertView = mInflater.inflate(R.layout.creditcard_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mainlayout = (RelativeLayout)convertView.findViewById(R.id.mainlayout);
                viewHolder.creditNum = (TextView)convertView.findViewById(R.id.creditNum);
                viewHolder.brandImage = (ImageView)convertView.findViewById(R.id.brandImage);
                viewHolder.delete = (ImageView)convertView.findViewById(R.id.delete);
                convertView.setTag(viewHolder);

                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int row = (Integer)view.getTag();
                        alert = new UDIDAlert(ActivityCreditCard.this, "REMOVE CREDIT CARD", getString(R.string.alert_cc2), 2);
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
                                deleting_row = row;
                                delCard();
                            }
                        });
                        alert.UDIDAlert.show();
                    }

                });
            }
            else{
                // re-bind widgets in convertedview
                viewHolder = (ViewHolder)convertView.getTag();
            }

            //update content in convertedview
            viewHolder.creditNum.setText(String.format("PERSONAL **** " + arrayList.get(position).get("card_num")));

            if(arrayList.get(position).get("brand").equals("Visa")) {
                viewHolder.brandImage.setImageResource(R.drawable.visa);
            }
            else {
                viewHolder.brandImage.setImageResource(R.drawable.mastercard);
            }
            Log.d("sputils",Boolean.toString(SPUtils.getString(mContext, Value.CARD).equals(arrayList.get(position).get("id"))));
            if(SPUtils.getString(mContext, Value.CARD).equals(arrayList.get(position).get("id"))) {
                if(!SPUtils.getString(mContext, "PAYMENT").toString().equals("Cash")) {
                    if(arrayList.get(position).get("card_num") != null) {
                        SPUtils.set(mContext, "PAYMENT", "**** " + arrayList.get(position).get("card_num"));
                    }
                    viewHolder.creditNum.setText(String.format("PERSONAL **** " + arrayList.get(position).get("card_num") + " ACTIVE"));
                    viewHolder.mainlayout.setBackgroundColor(ContextCompat.getColor(ActivityCreditCard.this, R.color.grey400));
                }
            }
            else{
                viewHolder.mainlayout.setBackgroundColor(getResources().getColor(R.color.greyAlpha));
            }
            viewHolder.delete.setTag(position);

            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    public class ViewHolder{
        public RelativeLayout mainlayout;
        public TextView creditNum;
        public ImageView brandImage;
        public ImageView delete;
    }

    private void getCardList() {
        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();

        cardList.clear();
        arrayList.clear();

        Call<CreditCardCollectionDao> call = HttpManager.getInstance()
                                                        .getService()
                                                        .getCardList(SPUtils.getString(mContext, Value.API_KEY));
        call.enqueue(new Callback<CreditCardCollectionDao>() {
            @Override
            public void onResponse(Call<CreditCardCollectionDao> call, Response<CreditCardCollectionDao> response) {
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo get card list : "+jsonFromPojo);
                    dia.dismiss();
                    CreditCardCollectionDao dao = response.body();
                    if(dao.getResult() == 1) {
                        SPUtils.set(mContext, Value.CARD, dao.getCard_active());

                        if(dao.getCount() != 0) {
                            List<CreditCardDao> cardDao = dao.getData();
                            for (int i = 0; i < cardDao.size(); i++) {
                                HashMap<String, String> hashMap = new HashMap<String, String>();
                                hashMap.put("id", cardDao.get(i).getId());
                                hashMap.put("card_num", cardDao.get(i).getCard_num());
                                hashMap.put("brand", cardDao.get(i).getBrand());
                                cardList.add(hashMap);
                                arrayList.add(hashMap);
                                Boolean lociCard = (cardDao.get(i).getId().equals(dao.getCard_active()));
                                Log.d("Tag","lociCard : "+lociCard);

                                if(cardDao.get(i).getId().equals(dao.getCard_active())){
                                    SPUtils.set(mContext, "PAYMENT", "**** " + arrayList.get(i).get("card_num"));
                                    llCash.setBackgroundColor(ContextCompat.getColor(ActivityCreditCard.this, R.color.greyAlpha));
                                }
                            }

                        }
                        else {
                            SPUtils.set(mContext, Value.CARD, "");
                        }

                        setupCustomListView();
                    }
                    else {
                        alert = new UDIDAlert(ActivityCreditCard.this, "UDID", dao.getMessage(), 1);
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
                else {
                    try {
                        Utils.showToast(response.errorBody().string());
                        dia.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                        dia.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<CreditCardCollectionDao> call, Throwable t) {
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                    dia.dismiss();
                }
                else  {
                    Utils.showToast(t.toString());
                    dia.dismiss();
                }
            }
        });
    }


    private void changeCardActive(final int index) {
        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();

        Log.d("Tag","API_KEY : "+SPUtils.getString(mContext, Value.API_KEY));
        Log.d("Tag","arrayList id : "+arrayList.get(index).get("id"));

        Call<ChangeCreditCardActiveDao> call = HttpManager.getInstance().getService().changeCardActive(SPUtils.getString(mContext, Value.API_KEY), arrayList.get(index).get("id"));
        call.enqueue(new Callback<ChangeCreditCardActiveDao>() {
            @Override
            public void onResponse(Call<ChangeCreditCardActiveDao> call, Response<ChangeCreditCardActiveDao> response) {
                dia.dismiss();
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo change card  : "+jsonFromPojo);
                    Log.d("Tag","call request : "+call.request());
                    Log.d("Tag","response : "+response.body());
                    ChangeCreditCardActiveDao dao = response.body();
                    if (dao.getResult() == 1) {
                        llCash.setBackgroundColor(ContextCompat.getColor(ActivityCreditCard.this, R.color.greyAlpha));
                        SPUtils.set(mContext, Value.CARD, arrayList.get(index).get("id"));
                        SPUtils.set(mContext, "PAYMENT", "**** " + arrayList.get(index).get("card_num"));
                        setupCustomListView();
                    }
                    else {
                        alert = new UDIDAlert(ActivityCreditCard.this, "Credit Card", dao.getMessage(), 1);
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
                else {
                    try {
                        Utils.showToast(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChangeCreditCardActiveDao> call, Throwable t) {
                dia.dismiss();
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                }
                else  {
                    Utils.showToast(t.toString());
                }
            }
        });
    }

    private void delCard() {
        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();

        arrayListTemp.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("id", arrayList.get(i).get("id"));
            hashMap.put("card_num", arrayList.get(i).get("card_num"));
            hashMap.put("brand", arrayList.get(i).get("brand"));
            arrayListTemp.add(hashMap);
        }
        arrayListTemp.remove(deleting_row);


        Call<DelCreditCardDao> call = HttpManager.getInstance().getService().
                deleteCard(
                SPUtils.getString(mContext, Value.API_KEY),
                arrayList.get(deleting_row).get("id"));
        call.enqueue(new Callback<DelCreditCardDao>() {
            @Override
            public void onResponse(Call<DelCreditCardDao> call, Response<DelCreditCardDao> response) {
                dia.dismiss();
                if(response.isSuccessful()) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response.body());
                    Log.d("TagD","jsonFromPojo Dele card : "+jsonFromPojo);
                    DelCreditCardDao dao = response.body();
                    if(dao.getResult() == 1) {
                        arrayList.remove(deleting_row);
                        
                        if(arrayList.size() == 0) {
                            SPUtils.set(mContext, "PAYMENT", "Cash");
                            llCash.setBackgroundColor(ContextCompat.getColor(ActivityCreditCard.this, R.color.grey400));
                        }

                        dia.cancel();                        
                        deleting_row = -1;
                        setupCustomListView();
                    }
                    else {
                        alert = new UDIDAlert(ActivityCreditCard.this, "REMOVE CREDIT CARD", dao.getMessage(), 1);
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
            public void onFailure(Call<DelCreditCardDao> call, Throwable t) {
                if(t instanceof SocketTimeoutException) {
                    Utils.showToast("Connection Time out. Please try again.");
                }
                else  {
                    dia.dismiss();
                    Utils.showToast(t.toString());
                }
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


