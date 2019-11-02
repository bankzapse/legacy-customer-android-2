package app.udrinkidrive.feed2us.com.customer.util;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import app.udrinkidrive.feed2us.com.customer.model.ModelCustomer;
import app.udrinkidrive.feed2us.com.customer.model.ModelDriver;

/**
 * Created by E on 12/13/2014.
 */
public class JsonParser {

    public static List<ModelCustomer> parseLogin(Context c, JSONObject json) {
        List<ModelCustomer> list = new ArrayList<>();
        try {
            JSONArray jsonArray = json.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject j = jsonArray.getJSONObject(i);
                ModelCustomer model = new ModelCustomer();
                model.first_name = j.getString("first_name");
                model.last_name = j.getString("last_name");
                model.email = j.getString("email");
                model.gender = j.getString("sex_id");
                model.mobileno = j.getString("phone");
                model.api_key = j.getString("api_token");

                Log.d("Set User Default", " Complete");

                SPUtils.set(c, Value.FIRSTNAME, j.getString("first_name"));
                SPUtils.set(c, Value.LASTNAME, j.getString("last_name"));
                SPUtils.set(c, Value.EMAIL, j.getString("email"));
                SPUtils.set(c, Value.GENDER, j.getString("sex_id"));
                SPUtils.set(c, Value.MOBILENO, j.getString("phone"));
                SPUtils.set(c, Value.API_KEY, j.getString("api_token"));

                list.add(model);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static ModelDriver parseDriver(Context c, JSONObject json) {
        ModelDriver model = new ModelDriver();
        try {
            JSONArray jsonArray = json.getJSONArray("data");

            JSONObject j = jsonArray.getJSONObject(0);
            model.first_name = ((j.isNull("first_name")) ? "" : j.getString("first_name"));
            model.last_name = ((j.isNull("last_name")) ? "" : j.getString("last_name"));
            model.sex_id = ((j.isNull("sex_id")) ? "" : j.getString("sex_id"));
            model.phone_no1 = ((j.isNull("phone_no1")) ? "" : j.getString("phone_no1"));
            model.driver_lat = ((j.isNull("driver_lat")) ? "" : j.getString("driver_lat"));
            model.image_url = ((j.isNull("image_url")) ? "" : j.getString("image_url"));
            model.rating_avg = ((j.isNull("rating_avg")) ? "" : j.getString("rating_avg"));
            model.status = ((j.isNull("status")) ? "" : j.getString("status"));
            model.cost_wait = ((j.isNull("cost_wait")) ? "" : j.getString("cost_wait"));
            model.wait_time = ((j.isNull("wait_time")) ? "" : j.getString("wait_time"));
            model.duration = ((j.isNull("duration")) ? "" : j.getString("duration"));

            Log.d("Get Driver", " Complete");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return model;
    }
//
//    private String getStringNotNull(JSONObject j, String field){
//
//    }

}
