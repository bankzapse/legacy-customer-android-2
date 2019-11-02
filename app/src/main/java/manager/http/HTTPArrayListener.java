package manager.http;

/**
 * Created by Kong on 10/14/2014.
 */
public interface HTTPArrayListener<T> {

    public void onResponse(T[] data, String rawData);

    public void onFailure(T[] data, String rawData);

}
