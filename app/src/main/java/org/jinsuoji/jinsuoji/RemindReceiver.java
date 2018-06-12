package org.jinsuoji.jinsuoji;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RemindReceiver extends BroadcastReceiver {
    private static final String TAG = "o.j.j.RemindReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Toast.makeText(context, R.string.reminder_toast, Toast.LENGTH_LONG).show();
        if (bundle == null) throw new AssertionError();
        Log.d(TAG, "onReceive: " + bundle.toString() + (bundle.getInt(RemindActivity.TODO_TO_BE_REMINDED)));
        Intent intent2 = new Intent(context, RemindActivity.class);
        intent2.putExtras(bundle);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
    }
}
