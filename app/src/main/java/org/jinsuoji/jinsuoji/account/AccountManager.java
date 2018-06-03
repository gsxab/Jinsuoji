package org.jinsuoji.jinsuoji.account;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jinsuoji.jinsuoji.data_access.Serializer;
import org.jinsuoji.jinsuoji.net.AuthTask;
import org.jinsuoji.jinsuoji.net.DownloadTask;
import org.jinsuoji.jinsuoji.net.ErrorBean;
import org.jinsuoji.jinsuoji.net.RegisterTask;
import org.jinsuoji.jinsuoji.net.RestfulAsyncTask;
import org.jinsuoji.jinsuoji.net.UploadTask;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AccountManager {
    private static final String TAG = "o.j.j.a.AM";
    private static final Charset ASCII = Charset.forName("US-ASCII");

    private static AccountManager manager;
    public static AccountManager getInstance() {
        if (manager == null) manager = new AccountManager();
        manager.retrieveAccount();
        return manager;
    }

    private void retrieveAccount() {
        // TODO 从本地存储获取account
    }

    private void saveAccount() {
        // TODO 向本地存储写入account
    }

    private AccountManager() {
        // retrieveAccount();
    }

    public boolean checkNoLoginInfo() {
        if (account == null) retrieveAccount();
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

    /**
     * UI线程使用.进行一次登录的验证，异步操作，传入回调.
     * @param onSuccess 验证成功
     * @param onMessage 验证失败及信息
     */
    public void login(final RestfulAsyncTask.SuccessOperation<String> onSuccess,
                      RestfulAsyncTask.FailureOperation onFailure,
                      RestfulAsyncTask.MessageOperation onMessage) {
        if (account == null) {
            // 无信息不能登录
            onFailure.onFailure(new ErrorBean("NO_LOGIN_INFO", ""));
        } else {
            new AuthTask(this, new RestfulAsyncTask.SuccessOperation<String>() {
                @Override
                public void onSuccess(String result) {
                    saveAccount();
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
    public void register(String username, String password,
                         final RestfulAsyncTask.SuccessOperation<String> onSuccess,
                         RestfulAsyncTask.FailureOperation onFailure,
                         RestfulAsyncTask.MessageOperation onMessage) {
        setInfo(username, password);
        new RegisterTask(account, new RestfulAsyncTask.SuccessOperation<String>() {
            @Override
            public void onSuccess(String result) {
                saveAccount();
                onSuccess.onSuccess(result);
            }
        },  onFailure, onMessage).start();
    }

    /**
     * UI线程使用.注销.
     */
    public void logout() {
        account = null;
        saveAccount();
    }

    /**
     * UI线程使用.上同步.
     * @param context 上下文，用于取得数据库
     * @param onSuccess 上传成功
     * @param onMessage 上传失败及信息
     */
    public void upload(Context context, RestfulAsyncTask.SuccessOperation<Void> onSuccess,
                       RestfulAsyncTask.FailureOperation onFailure,
                       RestfulAsyncTask.MessageOperation onMessage) {
        new UploadTask(this, context, onSuccess, onFailure, onMessage).start();
    }

    /**
     * UI线程使用.上同步.
     * @param context 上下文，用于取得数据库
     * @param onSuccess 上传成功
     * @param onMessage 上传失败及信息
     */
    public void download(Context context,
                       RestfulAsyncTask.SuccessOperation<Serializer.DBMirror> onSuccess,
                       RestfulAsyncTask.FailureOperation onFailure,
                       RestfulAsyncTask.MessageOperation onMessage) {
        new DownloadTask(this, context, onSuccess, onFailure, onMessage).start();
    }

    public AccountManager setInfo(String username, String password) {
        String storedPassword = digest(username + password);
        account = new Account(username, storedPassword);
        return this;
    }
}
