package org.jinsuoji.jinsuoji.zhongcao;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class LoadPictureTask extends AsyncTask<String, Void, Drawable> {
    interface OnLoadSuccess {
        void onSuccess(Drawable drawable);
    }

    interface OnLoadFailure {
        void onFailure();
    }

    private String filename;
    private OnLoadSuccess onLoadSuccess;
    private OnLoadFailure onLoadFailure;

    LoadPictureTask(String filename, OnLoadSuccess onLoadSuccess, OnLoadFailure onLoadFailure) {
        this.filename = filename;
        this.onLoadSuccess = onLoadSuccess;
        this.onLoadFailure = onLoadFailure;
    }

    @Override
    protected Drawable doInBackground(String... strings) {
        try {
            return Drawable.createFromPath(strings[0]);
        } catch (SecurityException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        if (drawable != null) {
            onLoadSuccess.onSuccess(drawable);
        } else {
            onLoadFailure.onFailure();
        }
    }

    void start() {
        execute(filename);
    }
}
