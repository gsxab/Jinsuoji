package org.jinsuoji.jinsuoji.account;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.jinsuoji.jinsuoji.Preference;
import org.jinsuoji.jinsuoji.data_access.Serializer;
import org.jinsuoji.jinsuoji.net.AuthTask;
import org.jinsuoji.jinsuoji.net.DownloadTask;
import org.jinsuoji.jinsuoji.net.ErrorBean;
import org.jinsuoji.jinsuoji.net.NetworkUtils;
import org.jinsuoji.jinsuoji.net.RegisterTask;
import org.jinsuoji.jinsuoji.net.RestfulAsyncTask;
import org.jinsuoji.jinsuoji.net.UploadTask;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class AccountManager {
    private static final String TAG = "o.j.j.a.AM";
    private static final Charset ASCII = Charset.forName("US-ASCII");

    private static AccountManager manager;
    public static AccountManager getInstance(Context context) {
        if (manager == null) manager = new AccountManager();
        manager.retrieveAccount(context);
        return manager;
    }

    private void retrieveAccount(Context context) {
        account = Preference.getAccountInfo(context);
    }

    private void saveAccount(Context context) {
        if (account != null) {
            Preference.setAccountInfo(context, account);
        }
    }

    private AccountManager() {
        // retrieveAccount();
    }

    public boolean checkNoLoginInfo() {
        // if (account == null) retrieveAccount(context);
        return account == null;
    }

    private @Nullable Account account;

    private @NonNull String digest(String asciiString) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // MD5被java标准要求存在，这个情况不会发生的
            Log.e(TAG, "digest: MD5 is not supported", e);
            //noinspection ConstantConditions
            return null;
        }
        byte[] raw = digest.digest(asciiString.getBytes(ASCII));
        return new BigInteger(1, raw).toString(16);
    }

    public String getUsername() {
        if (account != null) {
            return account.getUsername();
        } else {
            return null;
        }
    }

    public String addSalt(String salt) {
        if (account == null) {
            return null;
        }
        return digest(salt + account.getStoredPassword());
    }

    private boolean checkLoginInfo(RestfulAsyncTask.FailureOperation onFailure,
                                   RestfulAsyncTask.MessageOperation onMessage) {
        if (checkNoLoginInfo()) {
            onFailure.onFailure(new ErrorBean("NO_LOGIN_INFO", ""));
            onMessage.onTaskOver();
            return false;
        }
        return true;
    }

    /**
     * UI线程使用.进行一次登录的验证，异步操作，传入回调.
     * @param onSuccess 验证成功
     * @param onMessage 验证失败及信息
     */
    public void login(final Context context,
                      final RestfulAsyncTask.SuccessOperation<String> onSuccess,
                      RestfulAsyncTask.FailureOperation onFailure,
                      RestfulAsyncTask.MessageOperation onMessage) {
        if (checkLoginInfo(onFailure, onMessage)) {
            new AuthTask(this, new RestfulAsyncTask.SuccessOperation<String>() {
                @Override
                public void onSuccess(String result) {
                    saveAccount(context);
                    onSuccess.onSuccess(result);
                }
            }, onFailure, onMessage).start();
        }
    }

    /**
     * UI线程使用.注册.
     * @param username 用户名
     * @param password 密码
     * @param onSuccess 验证成功
     * @param onFailure 验证失败
     * @param onMessage 验证信息
     */
    public void register(final Context context,
                         String username, String password,
                         final RestfulAsyncTask.SuccessOperation<String> onSuccess,
                         RestfulAsyncTask.FailureOperation onFailure,
                         RestfulAsyncTask.MessageOperation onMessage) {
        setInfo(username, password);
        new RegisterTask(account, new RestfulAsyncTask.SuccessOperation<String>() {
            @Override
            public void onSuccess(String result) {
                saveAccount(context);
                onSuccess.onSuccess(result);
            }
        },  onFailure, onMessage).start();
    }

    /**
     * UI线程使用.注销.
     */
    public void logout(Context context) {
        account = null;
        saveAccount(context);
    }

    private boolean checkNetwork(Context context,
                                 RestfulAsyncTask.FailureOperation onFailure,
                                 RestfulAsyncTask.MessageOperation onMessage) {
        if (ContextCompat.checkSelfPermission(context, INTERNET) != PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, ACCESS_NETWORK_STATE) != PERMISSION_GRANTED
                || !NetworkUtils.checkNetwork(context)) {
            //申请权限 ActivityCompat.requestPermissions();
            onFailure.onFailure(new ErrorBean("NETWORK_ACCESS_FAILED", ""));
            onMessage.onTaskOver();
            return false;
        }
        return true;
    }

    /**
     * UI线程使用.上同步.
     * @param context 上下文，用于取得数据库
     * @param onSuccess 上传成功
     * @param onMessage 上传失败及信息
     */
    public void upload(Context context,
                       RestfulAsyncTask.SuccessOperation<Void> onSuccess,
                       RestfulAsyncTask.FailureOperation onFailure,
                       RestfulAsyncTask.MessageOperation onMessage) {
        if (checkLoginInfo(onFailure, onMessage) && checkNetwork(context, onFailure, onMessage)) {
            new UploadTask(this, context, onSuccess, onFailure, onMessage).start();
        }
    }

    /**
     * UI线程使用.上同步.
     * @param context 上下文，用于取得数据库及判断网络连接
     * @param onSuccess 上传成功
     * @param onMessage 上传失败及信息
     */
    public void download(Context context,
                         RestfulAsyncTask.SuccessOperation<Serializer.DBMirror> onSuccess,
                         RestfulAsyncTask.FailureOperation onFailure,
                         RestfulAsyncTask.MessageOperation onMessage) {
        if (checkLoginInfo(onFailure, onMessage) && checkNetwork(context, onFailure, onMessage)) {
            new DownloadTask(this, context, onSuccess, onFailure, onMessage).start();
        }
    }

    public AccountManager setInfo(String username, String password) {
        String storedPassword = digest(username + password);
        account = new Account(username, storedPassword);
        return this;
    }
}
