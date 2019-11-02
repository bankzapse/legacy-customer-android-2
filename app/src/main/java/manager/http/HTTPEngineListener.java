package manager.http;

import org.json.JSONException;

import dao.BaseDao;


/**
 * Created by Kong on 10/14/2014.
 */
public interface HTTPEngineListener<T extends BaseDao> {

    public void onResponse(T data, String rawData) throws JSONException;

    public void onFailure(T data, String rawData);

}
