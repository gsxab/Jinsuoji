package org.jinsuoji.jinsuoji.zhongcao;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import org.jinsuoji.jinsuoji.R;

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

    LoadPictureTask(Context context, String uri, OnLoadSuccess onLoadSuccess, OnLoadFailure onLoadFailure) {
        this.filename = getPath(Uri.parse(uri), context);
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

    private static String getPath(Uri imageUri, Context context) {
        String path;
        if (!TextUtils.isEmpty(imageUri.getAuthority())) { //使用 getAuthority 做判断条件
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                    !imageUri.getAuthority().equals("media")) {
                // 4.4及以上，返回content://com.android.providers.media.documents/document/image%3A12345
                String wholeID = DocumentsContract.getDocumentId(imageUri);
                String id = wholeID.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);
                if (cursor == null) throw new AssertionError();
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    path = cursor.getString(columnIndex);
                } else {
                    path = null;
                }
                cursor.close();
            } else {
                // 4.4以下，返回content://media/external/images/media/188222
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(imageUri, projection, null, null, null);
                if (cursor == null) throw new AssertionError();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
                cursor.close();
            }
        } else {
            path = imageUri.getPath(); //小米选择照片返回 data="file:///..." uri.getAuthority()==""
        }
        if (path == null) {
            Toast.makeText(context, R.string.add_failed, Toast.LENGTH_SHORT).show();
            return null;
        }
        return path;
    }
}
