package manager.http;

import com.squareup.okhttp.Response;

import java.io.IOException;

import base.BaseAsyncTask;


/**
 * Created by Kong on 10/20/2014.
 */
public class HTTPRequestTask extends BaseAsyncTask<HTTPRequestData, Void, HTTPRequestTask.ContentMessage> {

    HTTPRequestListener mListener;

    public HTTPRequestTask(HTTPRequestListener aListener) {
        mListener = aListener;
    }

    @Override
    protected ContentMessage doInBackground(HTTPRequestData... params) {
        HTTPRequestData data = params[0];
        ContentMessage message = new ContentMessage();


        try {
            Response response = data.call.execute();
            if (response.isSuccessful()) {
                message.success = true;
                message.statusCode = response.code();
            } else {
                message.success = false;
                message.statusCode = response.code();
            }
            message.body = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            message.success = false;
        }

        return message;
    }

    @Override
    protected void onPostExecute(ContentMessage s) {
        super.onPostExecute(s);
        if (mListener != null) {
            if (s.success) {
                mListener.onMessageReceived(s.body);
            } else {
                mListener.onMessageError(s.statusCode, s.body);
            }
        }
    }

    public class ContentMessage {
        boolean success;
        int statusCode;
        String body;
    }
}
