package app.udrinkidrive.feed2us.com.customer.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.udrinkidrive.feed2us.com.customer.R;
import app.udrinkidrive.feed2us.com.customer.view.RewardAlert;
import app.udrinkidrive.feed2us.com.customer.view.UDIDAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import dao.TestDao;
import io.fabric.sdk.android.Fabric;
import manager.http.HTTPEngine;
import manager.http.HTTPEngineListener;
//import su.levenetc.android.badgeview.BadgeView;
import app.udrinkidrive.feed2us.com.customer.util.SPUtils;
import app.udrinkidrive.feed2us.com.customer.util.Utils;
import app.udrinkidrive.feed2us.com.customer.util.Value;

public class ActivityReward extends ListActivity {

    private String TAG = ActivityReward.class.getSimpleName();
    private Context mContext;

    private ProgressDialog dia;
    RewardAlert rewardAlert;
    UDIDAlert alert;

    private ArrayList<HashMap<String, String>> rewardList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> inboxList = new ArrayList<HashMap<String, String>>();
    public int point_km = 0;
    public String level = "";
    public int inboxNum = 0;

    public int selected_index;

    public static int REQ_REDEEM = 1;
    public static int REQ_INBOX = 2;
    public int tabNo = 1;
    private boolean isOpenPopup = false;

    @BindView(R.id.ivBgLevel)
    ImageView ivBgLevel;
    @BindView(R.id.rlLevel)
    RelativeLayout rlLevel;
    @BindView(R.id.tvLevel)
    TextView tvLevel;
    @BindView(R.id.separator)
    View separator;

    @BindView(R.id.llBack)
    ImageView llBack;
    @BindView(R.id.ivRule)
    ImageView ivRule;
    @BindView(R.id.tvKm)
    TextView tvKm;

    @BindView(R.id.btnTab1)
    TextView btnTab1;
    @BindView(R.id.btnTab2)
    TextView btnTab2;
//    @BindView(R.id.badgeView)
//    BadgeView badgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reward);
        ButterKnife.bind(this);

        setInitView();
        setInitEvent();
    }

    private void setInitView() {
        mContext = this;

        getRewardList();
    }

    private void setInitEvent() {
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        btnTab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabNo = 1;
                btnTab1.setBackgroundColor(getResources().getColor(R.color.blue_udid));
                btnTab2.setBackgroundColor(getResources().getColor(R.color.blue_udid_unclick));
                setupCustomListView();
            }
        });

        btnTab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabNo = 2;
                btnTab1.setBackgroundColor(getResources().getColor(R.color.blue_udid_unclick));
                btnTab2.setBackgroundColor(getResources().getColor(R.color.blue_udid));
                setupCustomListView();
            }
        });

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int selectedIndex, long id) {

                selected_index = selectedIndex;
                final int index = selectedIndex;

                if (tabNo == 1) {
                    if (!isOpenPopup) {
                        isOpenPopup = true;
                        rewardAlert = new RewardAlert(
                                ActivityReward.this,
                                rewardList.get(selectedIndex).get("name"),
                                rewardList.get(selectedIndex).get("image_thumbnail"),
                                rewardList.get(selectedIndex).get("price"),
                                rewardList.get(selectedIndex).get("description"));
                        rewardAlert.dBtnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rewardAlert.RewardAlert.dismiss();
                                isOpenPopup = false;
                            }
                        });

                        rewardAlert.dBtnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rewardAlert.RewardAlert.dismiss();
                                isOpenPopup = false;
                                if (point_km >= Integer.parseInt(rewardList.get(index).get("point_km"))) {
                                    redeemReward();
                                } else {
                                    alert = new UDIDAlert(ActivityReward.this, "Reward", "แต้มไม่เพียงพอ", 1);
                                    alert.dBtnOK.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alert.UDIDAlert.dismiss();
                                        }
                                    });
                                    alert.UDIDAlert.show();
                                }
                            }
                        });
                        rewardAlert.RewardAlert.show();
                    }
                } else {
                    if (Integer.parseInt(inboxList.get(selectedIndex).get("view")) == 0) {
                        inboxNum -= 1;
//                        badgeView.setValue(inboxNum);
                        inboxList.get(selectedIndex).put("view", "1");
                        updateInboxView();
                    }
                    Intent in = new Intent(mContext, ActivityVoucher.class);
                    in.putExtra("PROMOTION", inboxList.get(selected_index).get("promotion_code"));
                    in.putExtra("VOUCHER", inboxList.get(selected_index).get("voucher_url"));
                    in.putExtra("NAME", inboxList.get(selected_index).get("reward_name"));
                    in.putExtra("DESCRIPTION", inboxList.get(selected_index).get("description"));
                    in.putExtra("PRICE", inboxList.get(selected_index).get("price"));
                    in.putExtra("IMAGE", inboxList.get(selected_index).get("image_thumbnail"));
                    in.putExtra("TYPE", inboxList.get(selected_index).get("type"));
                    in.putExtra("REQ", 2);
                    startActivityForResult(in, REQ_INBOX);
                }
            }
        });
    }

    private void getRewardList() {
        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();
        Map<String, String> param = new HashMap<>();
        param.put("api_token", SPUtils.getString(mContext, Value.API_KEY));
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {
                JSONObject response = new JSONObject(rawData);
                if (response != null) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    String jsonFromPojo = gsonBuilder.toJson(response);
                    Log.d("TagD","jsonFromPojo reward : "+jsonFromPojo);
                    try {
                        if (response.getString("result").equals("1")) {
                            level = response.getString("level_name");
                            point_km = response.getInt("points");
                            tvKm.setText(point_km + "");
                            rewardList.clear();
                            inboxList.clear();
                            inboxNum = 0;

                            JSONArray jsonArray = response.getJSONArray("data");
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject j = jsonArray.getJSONObject(i);
                                    HashMap<String, String> hashMap = new HashMap<String, String>();
                                    hashMap.put("id", j.getString("id"));
                                    hashMap.put("name", j.getString("name"));
                                    hashMap.put("description", j.getString("description"));
                                    hashMap.put("price", j.getString("price"));
                                    hashMap.put("quantity", j.getString("quantity"));
                                    hashMap.put("point_km", j.getString("point_km"));
                                    hashMap.put("image_thumbnail", j.getString("image_thumbnail"));
                                    hashMap.put("expire_datetime", j.getString("expire_datetime"));
                                    hashMap.put("type", j.getString("type"));
                                    rewardList.add(hashMap);
                                }
                            }

                            jsonArray = response.getJSONArray("inbox");
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject j = jsonArray.getJSONObject(i);
                                    HashMap<String, String> hashMap = new HashMap<String, String>();
                                    hashMap.put("id", j.getString("id"));
                                    hashMap.put("reward_name", j.getString("reward_name"));
                                    hashMap.put("description", j.getString("description"));
                                    hashMap.put("price", j.getString("price"));
                                    hashMap.put("image_thumbnail", j.getString("image_thumbnail"));
                                    hashMap.put("created_tm", j.getString("created_tm"));
                                    hashMap.put("type", j.getString("type"));
                                    hashMap.put("view", j.getString("view"));
                                    hashMap.put("voucher_url", ((j.isNull("voucher_url")) ? "" : j.getString("voucher_url")));
                                    hashMap.put("promotion_code", ((j.isNull("promotion_code")) ? "" : j.getString("promotion_code")));
                                    inboxList.add(hashMap);

                                    if (j.getInt("view") == 0) {
                                        inboxNum += 1;
                                    }
                                }
                            }

                            String level_name = response.getString("level_name");
                            rlLevel.setVisibility(View.VISIBLE);
                            separator.setVisibility(View.VISIBLE);
                            tvLevel.setText(level_name);
                            setLV(level);

                            if (inboxNum > 0) {
//                                badgeView.setValue(inboxNum);
//                                badgeView.setVisibility(View.VISIBLE);
                            }

                            setupCustomListView();
                            dia.dismiss();
                        } else {
                            dia.dismiss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(TestDao data, String rawData) {
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityReward.this);
            }
        }, Value.URL_REWARD, param);
    }

    private void setupCustomListView() {
        setListAdapter(new EfficientAdapter(getApplicationContext()));
    }

    public class EfficientAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public EfficientAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (tabNo == 1) {
                return rewardList.size();
            } else {
                return inboxList.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.reward_list_item, null);
                holder = new ViewHolder();
                holder.llBackground = (LinearLayout) convertView.findViewById(R.id.llBackground);
                holder.ivReward = (ImageView) convertView.findViewById(R.id.ivReward);
                holder.ivLock = (ImageView) convertView.findViewById(R.id.ivLock);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvKm = (TextView) convertView.findViewById(R.id.tvKm);
                holder.tvDetail = (TextView) convertView.findViewById(R.id.tvDetail);
                holder.tvExclusive = (TextView) convertView.findViewById(R.id.tvExclusive);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.llBackground.setBackgroundColor(getResources().getColor(R.color.bg_color));
            holder.ivLock.setVisibility(View.VISIBLE);
            if (tabNo == 1) {
                if (point_km >= Integer.parseInt(rewardList.get(position).get("point_km"))) {
                    holder.ivLock.setVisibility(View.GONE);
                }

                holder.tvName.setText(rewardList.get(position).get("name") + " ");
                holder.tvKm.setText("ใช้" + rewardList.get(position).get("point_km") + "km.");
                holder.tvDetail.setText(rewardList.get(position).get("name") + " เหลือ " + rewardList.get(position).get("quantity") + " รางวัล");
                holder.tvExclusive.setText(checkExclusive(rewardList.get(position).get("level_name")));
                holder.tvDate.setText(rewardList.get(position).get("expire_datetime"));
                Picasso.with(mContext).load(rewardList.get(position).get("image_thumbnail")).noFade().into(holder.ivReward);
            } else {
                if (Integer.parseInt(inboxList.get(position).get("view")) == 0) {
                    holder.llBackground.setBackgroundColor(getResources().getColor(R.color.list_divider));
                } else {
                    holder.llBackground.setBackgroundColor(getResources().getColor(R.color.bg_color));
                }
                holder.ivLock.setVisibility(View.GONE);
                holder.tvName.setText(inboxList.get(position).get("reward_name"));
                holder.tvKm.setText("");
                holder.tvDetail.setText("");
                holder.tvExclusive.setText("");
                holder.tvDate.setText(inboxList.get(position).get("created_tm"));
                Picasso.with(mContext).load(inboxList.get(position).get("image_thumbnail")).noFade().into(holder.ivReward);
            }

            return convertView;
        }
    }

    final public class ViewHolder {
        LinearLayout llBackground;
        ImageView ivReward;
        ImageView ivLock;
        TextView tvName;
        TextView tvKm;
        TextView tvDetail;
        TextView tvExclusive;
        TextView tvDate;
    }

    private void redeemReward() {
        dia = new ProgressDialog(this);
        dia.setMessage("Loading..");
        dia.setCancelable(false);
        dia.show();
        Map<String, String> param = new HashMap<>();
        param.put("api_token", SPUtils.getString(mContext, Value.API_KEY));
        param.put("reward_id", rewardList.get(selected_index).get("id"));
        param.put("point_km", rewardList.get(selected_index).get("point_km"));
        param.put("type", rewardList.get(selected_index).get("type"));
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {
                JSONObject response = new JSONObject(rawData);
                Gson gsonBuilder = new GsonBuilder().create();
                String jsonFromPojo = gsonBuilder.toJson(response);
                Log.d("TagD","jsonFromPojo redeemReward : "+jsonFromPojo);
                if (response != null) {
                    try {
                        if (response.getString("result").equals("1")) {
                            dia.dismiss();
                            Intent in = new Intent(mContext, ActivityVoucher.class);
                            in.putExtra("PROMOTION", ((response.isNull("promotion_code")) ? "" : response.getString("promotion_code")));
                            in.putExtra("VOUCHER", ((response.isNull("voucher_url")) ? "" : response.getString("voucher_url")));
                            in.putExtra("NAME", rewardList.get(selected_index).get("name"));
                            in.putExtra("DESCRIPTION", rewardList.get(selected_index).get("description"));
                            in.putExtra("PRICE", rewardList.get(selected_index).get("price"));
                            in.putExtra("IMAGE", rewardList.get(selected_index).get("image_thumbnail"));
                            in.putExtra("TYPE", rewardList.get(selected_index).get("type"));
                            in.putExtra("REQ", 1);
                            startActivityForResult(in, REQ_REDEEM);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(TestDao data, String rawData) {
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityReward.this);
            }
        }, Value.URL_REWARD_REDEEM, param);
    }

    private void updateInboxView() {
        Map<String, String> param = new HashMap<>();
        param.put("redeem_id", inboxList.get(selected_index).get("id"));
        Log.d("updateInboxView", Value.URL_UPDATE_INBOX_VIEW);
        Log.d("updateInboxView", param.toString());
        HTTPEngine.getInstance().loadData(new HTTPEngineListener<TestDao>() {
            @Override
            public void onResponse(TestDao data, String rawData) throws JSONException {

            }

            @Override
            public void onFailure(TestDao data, String rawData) {
                Utils.snackbarAlertNetwork(getApplicationContext(), ActivityReward.this);
            }
        }, Value.URL_UPDATE_INBOX_VIEW, param);
    }

    private String checkExclusive(String level) {
        return "Exclusive for "+level+" Member";
    }

    private void setLV(String level) {
        if (level.equals("Standard")) {
            tvLevel.setTextColor(getResources().getColor(R.color.white));
            ivBgLevel.setImageResource(R.drawable.nolevel_banner);
        } else if (level.equals("Bronze")) {
            tvLevel.setTextColor(getResources().getColor(R.color.rank_bronze));
            ivBgLevel.setImageResource(R.drawable.bronze_banner);
        } else if (level.equals("Silver")) {
            tvLevel.setTextColor(getResources().getColor(R.color.rank_silver));
            ivBgLevel.setImageResource(R.drawable.silver_banner);
        } else if (level.equals("Gold")) {
            tvLevel.setTextColor(getResources().getColor(R.color.rank_gold));
            ivBgLevel.setImageResource(R.drawable.gold_banner);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == REQ_REDEEM) {
            getRewardList();
        } else if (resultCode == RESULT_OK && requestCode == REQ_INBOX) {
            if (inboxNum <= 0) {
//                badgeView.setVisibility(View.GONE);
            }
            setupCustomListView();
        }
    }


}


