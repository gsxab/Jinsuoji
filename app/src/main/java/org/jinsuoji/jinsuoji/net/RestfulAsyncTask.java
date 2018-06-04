package org.jinsuoji.jinsuoji.net;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class RestfulAsyncTask<T> extends AsyncTask<Object, Integer, Object> {
    public interface SuccessOperation<T> {
        void onSuccess(T result);
    }
    public interface FailureOperation {
        void onFailure(ErrorBean errorBean);
    }
    public interface MessageOperation {
        void onProgressUpdate(int phase);
        MessageOperation ignore = new MessageOperation() {
            @Override
            public void onProgressUpdate(int phase) {
            }
        };
    }

    private static final String BASE_URL = "http://60.205.186.150/";
    private static final String TAG = "o.h.h.a.AsyncTask";

    private ReqAttr reqAttr;
    private String api;
    private SuccessOperation<T> onSuccess;
    private FailureOperation onFailure;
    private MessageOperation onMessage;
    private boolean successFlag = false;

    RestfulAsyncTask(ReqAttr reqAttr, String api, SuccessOperation<T> onSuccess,
                     FailureOperation onFailure, MessageOperation onMessage) {
        super();
        this.reqAttr = reqAttr;
        if (api.startsWith("/")) {
            api = api.substring(1);
        }
//        try {
//            this.api = URLEncoder.encode(api, "UTF-8");
//        } catch (UnsupportedEncodingException ignored) {
//            // never
//        }
        this.api = api;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
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
            Log.d(TAG, "doInBackground: response code " + conn.getResponseCode());
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
        } catch (ConnectException e) {
            result = new ErrorBean("CONNECTION_FAILED", "");
        } catch (IOException e) {
            Log.d(TAG, "doInBackground: error", e);
        }
        publishProgress(5);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(Object result) {
        if (result == null) {
            onFailure.onFailure(new ErrorBean("UNKNOWN", ""));
        }
        if (successFlag) {
            onSuccess.onSuccess((T) result);
        } else {
            onFailure.onFailure(((ErrorBean) result));
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

    public abstract void start();
}
