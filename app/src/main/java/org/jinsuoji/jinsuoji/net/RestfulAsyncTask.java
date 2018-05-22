package org.jinsuoji.jinsuoji.net;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RestfulAsyncTask<T> extends AsyncTask<Object, Integer, Object> {
    public interface SuccessOperation<T> {
        void onSuccess(T result);
    }
    public interface MessageOperation {
        void onFailure(ErrorBean errorBean);
        void onProgressUpdate(int phase);
    }

    private static final String BASE_URL = "http://60.205.186.150/";
    private static final String TAG = "o.h.h.a.AsyncTask";

    private ReqAttr reqAttr;
    private String api;
    private SuccessOperation<T> onSuccess;
    private MessageOperation onMessage;
    private boolean successFlag = false;

    RestfulAsyncTask(ReqAttr reqAttr, String api, SuccessOperation<T> onSuccess,
                     MessageOperation onMessage) {
        super();
        this.reqAttr = reqAttr;
        try {
            this.api = URLEncoder.encode(api, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            // never
        }
        this.onSuccess = onSuccess;
        this.onMessage = onMessage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object... objects) {
        Object result = null;
        try {
            publishProgress(0);
            HttpURLConnection conn = ((HttpURLConnection) new URL(BASE_URL + api).openConnection());
            conn.setRequestMethod(reqAttr.methodName);
            conn.setDoInput(true);
            decorate(conn);
            publishProgress(1);
            if (reqAttr.openOutputStream && objects.length > 0) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                OutputStream outputStream = conn.getOutputStream();
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(outputStream, objects[0]);
                } finally {
                    outputStream.close();
                }
            }
            publishProgress(2);
            conn.connect();
            publishProgress(3);
            successFlag = conn.getResponseCode() == reqAttr.successCode;
            if (successFlag) {
                if (reqAttr.openInputStream) {
                    InputStream inputStream = conn.getInputStream();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        result = mapper.readValue(inputStream, (Class) objects[1]);
                    } finally {
                        inputStream.close();
                    }
                }
                publishProgress(4);
                return result;
            } else {
                Log.d(TAG, "doInBackground: status code " + conn.getResponseCode());
                if (reqAttr.openErrorStream) {
                    InputStream inputStream = conn.getErrorStream();
                    try {
                        Reader reader = new InputStreamReader(inputStream);
                        ObjectMapper mapper = new ObjectMapper();
                        result = mapper.readValue(inputStream, ErrorBean.class);
                        reader.close();
                    } finally {
                        inputStream.close();
                    }
                }
                publishProgress(4);
                return result;
            }
        } catch (IOException ignored) {}
        publishProgress(5);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(Object result) {
        if (successFlag) {
            onSuccess.onSuccess((T) result);
        } else {
            onMessage.onFailure(((ErrorBean) result));
        }
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Integer... phase) {
        onMessage.onProgressUpdate(phase[0]);
        super.onProgressUpdate(phase);
    }

    @Override
    protected void onCancelled(Object value) {
        onMessage.onProgressUpdate((Integer) value);
        super.onCancelled(value);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    protected void decorate(HttpURLConnection conn) {}
}
