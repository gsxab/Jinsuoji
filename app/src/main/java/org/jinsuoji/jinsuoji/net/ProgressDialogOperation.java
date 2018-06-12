package org.jinsuoji.jinsuoji.net;

import android.app.ProgressDialog;
import android.content.Context;

import org.jinsuoji.jinsuoji.R;

public class ProgressDialogOperation implements RestfulAsyncTask.MessageOperation {
    public static final int SINGLE_TASK_REQUEST_NUMBER = 6;
    private Context context;
    private int progress, maxProgressCount;
    private ProgressDialog progressDialog;

    public ProgressDialogOperation(Context context, int taskNumber) {
        this.context = context;
        this.maxProgressCount = taskNumber * SINGLE_TASK_REQUEST_NUMBER;
        showProgressDialog();
    }

    @Override
    public void onTaskOver() {
        progressDialog.dismiss();
    }

    @Override
    public void onProgressUpdate(int phase) {
        progress++;
        progressDialog.setProgress(progress);
        if (progress == maxProgressCount) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgress(0);
        progressDialog.setTitle(context.getString(R.string.please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(maxProgressCount);
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
