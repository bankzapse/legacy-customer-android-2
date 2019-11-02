package manager.http;

/**
 * Created by Kong on 10/20/2014.
 */
public interface HTTPRequestListener {

    public void onMessageReceived(final String message);
    public void onMessageError(int statusCode, final String message);

}
