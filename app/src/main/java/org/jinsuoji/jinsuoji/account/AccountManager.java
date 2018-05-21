package org.jinsuoji.jinsuoji.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AccountManager {
    private static final String TAG = "o.j.j.a.AM";
    private static final Charset ASCII = Charset.forName("US-ASCII");

    private void retrieveAccount() {
        // TODO 从本地存储获取account
    }

    private void saveAccount() {
        // TODO 向本地存储写入account
    }

    public AccountManager() {
        retrieveAccount();
    }

    public boolean hasLogin() {
        return account != null;
    }

    private @Nullable Account account;

    private @NonNull MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // MD5被java标准要求存在，这个情况不会发生的
            Log.e(TAG, "digest: MD5 is not supported", e);
            //noinspection ConstantConditions
            return null;
        }
    }

    interface LoginCallback {
        void onInfo(String string);
        void onLoginSuccess(Account account);
    }

    /**
     * UI线程或子线程中使用.
     */
    public void login() {
        if (account == null) {
            // 未使用登录
        } else {
            // 向服务器请求salt(ASCII字符)
            byte[] message = ("" + account.getStoredPassword()).getBytes(ASCII);
            // MD5(salt + storedPassword)
            MessageDigest digest = getDigest();
            digest.digest(message);
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
        // MD5
        MessageDigest digest = getDigest();
        byte[] rawStoredPassword = digest.digest(password.getBytes(ASCII));
        // set account
        BigInteger decimal = new BigInteger(1, rawStoredPassword);
        String storedPassword = decimal.toString(16);
        // 发送username和hexStoredPassword

        // 服务器回调成功/失败

    }

    public void logout() {
        account = null;
        saveAccount();
    }
}
