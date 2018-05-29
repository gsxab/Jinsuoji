package org.jinsuoji.jinsuoji.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jinsuoji.jinsuoji.net.AuthTask;
import org.jinsuoji.jinsuoji.net.ErrorBean;
import org.jinsuoji.jinsuoji.net.RestfulAsyncTask;

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

    void retrieveAccount() {
        // TODO 从本地存储获取account
    }

    void saveAccount() {
        // TODO 向本地存储写入account
    }

    private AccountManager() {
        // retrieveAccount();
    }

    public boolean hasLoginInfo() {
        return account != null;
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
    public void login(RestfulAsyncTask.SuccessOperation<String> onSuccess,
                      RestfulAsyncTask.MessageOperation onMessage) {
        if (account == null) {
            // 无信息不能登录
            onMessage.onFailure(new ErrorBean("NO_LOGIN_INFO", ""));
        } else {
            new AuthTask(this, onSuccess, onMessage);
        }
    }

    interface RegisterCallback {
        void onInfo(String string);
        void onRegisterSuccess(Account account);
    }

    /**
     * UI线程或子线程中使用.
     * @param username 用户名
     * @param password 密码
     */
    public void register(String username, String password, RegisterCallback callback) {
        String storedPassword = digest(username + password);
        // set account
        account = new Account(username, storedPassword);
        // 发送username和hexStoredPassword

        // 服务器回调成功/失败

    }

    public void logout() {
        account = null;
        saveAccount();
    }
}
