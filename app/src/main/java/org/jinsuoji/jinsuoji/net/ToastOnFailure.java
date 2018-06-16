package org.jinsuoji.jinsuoji.net;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.jinsuoji.jinsuoji.GuideActivity;
import org.jinsuoji.jinsuoji.R;

public class ToastOnFailure implements RestfulAsyncTask.FailureOperation {
    private static final String TAG = "o.j.j.n.ToastOnFailure";

    private Context context;
    private boolean showLoginOnFailure;

    public ToastOnFailure(Context context, boolean showLoginOnFailure) {
        this.context = context;
        this.showLoginOnFailure = showLoginOnFailure;
    }

    @Override
    public void onFailure(ErrorBean errorBean) {
        String toast = null;
        Log.d(TAG, "onFailure: " + "[error=" + errorBean.getError() + ",data=" + errorBean.getData() + "]");
        switch (errorBean.getError()) {
        case "NO_LOGIN_INFO":
            toast = context.getString(R.string.no_login_info);
            showLogin();
            break;
        case "MISSING_ARGS":
            toast = context.getString(R.string.missing_args2, errorBean.getData());
            break;
        case "USERNAME_TOO_LONG":
            toast = context.getString(R.string.username_too_long, errorBean.getData());
            break;
        case "USER_EXISTS":
            toast = context.getString(R.string.user_exists, errorBean.getData());
            break;
        case "USERNAME_INVALID":
            toast = context.getString(R.string.username_invalid, errorBean.getData());
            break;
        case "USER_NOT_FOUND":
            toast = context.getString(R.string.user_not_found, errorBean.getData());
            break;
        case "SALT_EXPIRED":
        case "TOKEN_EXPIRED":
            toast = context.getString(R.string.essential_timeout);
            break;
        case "AUTHENTICATION_FAILED":
            toast = context.getString(R.string.login_incorrect);
            if (showLoginOnFailure) {
                showLogin();
            }
            break;
        case "NETWORK_ACCESS_FAILED":
            toast = context.getString(R.string.network_access_failed);
            break;
        case "CONNECTION_FAILED":
            toast = context.getString(R.string.connection_failed);
            break;
        case "UNKNOWN":
            toast = context.getString(R.string.unknown_error);
            break;
        default:
        }
        if (toast != null) {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        }
    }

    private void showLogin() {
        Intent intent = new Intent(context, GuideActivity.class);
        intent.putExtra(GuideActivity.KEY_LOGIN, true);
        context.startActivity(intent);
    }
}
