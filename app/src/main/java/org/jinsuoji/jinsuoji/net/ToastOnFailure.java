package org.jinsuoji.jinsuoji.net;

import android.content.Context;
import android.widget.Toast;

import org.jinsuoji.jinsuoji.R;

public class ToastOnFailure implements RestfulAsyncTask.FailureOperation {
    private Context context;

    public ToastOnFailure(Context context) {
        this.context = context;
    }

    @Override
    public void onFailure(ErrorBean errorBean) {
        String toast = null;
        switch (errorBean.getError()) {
        case "NO_LOGIN_INFO":
            toast = context.getString(R.string.no_login_info);
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
}
