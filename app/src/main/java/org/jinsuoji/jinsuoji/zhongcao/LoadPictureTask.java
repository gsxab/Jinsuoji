package org.jinsuoji.jinsuoji.zhongcao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class LoadPictureTask extends AsyncTask<String, Void, Bitmap> {
    interface OnLoadSuccess {
        void onSuccess(Bitmap bitmap);
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
    protected Bitmap doInBackground(String... strings) {
        return BitmapFactory.decodeFile(strings[0], null);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            onLoadSuccess.onSuccess(bitmap);
        } else {
            onLoadFailure.onFailure();
        }
    }

    void start() {
        execute(filename);
    }
}
