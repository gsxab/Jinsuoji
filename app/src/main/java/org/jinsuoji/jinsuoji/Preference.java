package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jinsuoji.jinsuoji.account.Account;

import java.util.Calendar;

/**
 * 封装SharedPreference(偏好设置，储存少量数据，对应于数据文件夹的一个文件).
 */
public class Preference {
    private static final String TAG = "o.j.j.Preference";

    // 上次打开引导界面是在哪个版本
    private static final String KEY_LAST_GUIDED = "guide_activity";

    // 上次同步
    private static final String KEY_LAST_SYNC_TIME = "last_sync_time";
    // 账户
    private static final String KEY_USER_NAME = "user_name";
    // 密码
    private static final String KEY_STORED_PASSWORD = "stored_password";

    private static SharedPreferences getPreferences(Context context) {
        return getDefaultSharedPreferences(context);
    }

    private static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    /**
     * 获得包信息
     * @param context 应用上下文
     * @return 包信息
     */
    private static @Nullable PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getPackageInfo: ", e);
            return null;
        }
    }

    /**
     * 判断是否引导过.
     * 判断依据是是否记录了一个不小于当前版本号的版本号.
     * 版本号(VersionCode)在module级build.gradle中设置.
     * @param context 应用上下文
     * @return  是否已经引导过 true引导过了 false未引导
     */
    public static boolean isGuided(@NonNull Context context) {
        PackageInfo info = getPackageInfo(context);
        return info != null && getPreferences(context).getInt(KEY_LAST_GUIDED, 0)
                >= info.versionCode;
    }

    /**
     * 查看上次同步信息.
     */
    public static @Nullable Calendar getLastSync(Context context) {
        long timeInMillis = getDefaultSharedPreferences(context).getLong(KEY_LAST_SYNC_TIME, -1);
        if (timeInMillis == -1) {
            return null;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
            return calendar;
        }
    }

    /**
     * 设置同步时间为当前.
     */
    public static void setLastSync(Context context) {
        getDefaultSharedPreferences(context).edit()
                .putLong(KEY_LAST_SYNC_TIME, Calendar.getInstance().getTime().getTime())
                .apply();
    }

    /**
     * 写入用户信息.
     * @param account 账户信息
     */
    public static void setAccountInfo(Context context, @NonNull Account account) {
        getDefaultSharedPreferences(context).edit()
                .putString(KEY_USER_NAME, account.getUsername())
                .putString(KEY_STORED_PASSWORD, account.getStoredPassword())
                .apply();
    }

    /**
     * 提取用户信息.
     * @return 账户信息
     */
    public static Account getAccountInfo(Context context) {
        Account account = new Account(
                getDefaultSharedPreferences(context).getString(KEY_USER_NAME, null),
                getDefaultSharedPreferences(context).getString(KEY_STORED_PASSWORD, null)
        );
        return account.getUsername() == null ? null : account;
    }

    /**
     * 获取自动同步设置.
     */
    public static boolean getAutoSync(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getString(R.string.pref_key_sync_switch), false
        );
    }

    /**
     * 获取仅wifi设置.
     */
    public static boolean getSyncWifiOnly(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getString(R.string.pref_key_sync_wifi), false
        );
    }

    /**
     * 获取同步间隔设置.
     */
    public static int getSyncFreq(Context context) {
        String frequency = (getDefaultSharedPreferences(context).getString(
                context.getString(R.string.pref_key_sync_frequence), null));
        if (frequency == null) return -1;
        switch (frequency) {
        case "每天":
            return Calendar.DATE;
        case "每周":
            return Calendar.WEEK_OF_MONTH;
        case "每月":
            return Calendar.MONTH;
        }
        return -1;
    }

    /**
     * 设置被引导过了.
     *
     * @param context 应用上下文
     */
    public static void setGuided(@NonNull Context context){
        PackageInfo info = getPackageInfo(context);
        if (info == null) {
            return;
        }
        int versionCode = info.versionCode;
        getPreferences(context)//保存修改后的值
                .edit()
                .putInt(KEY_LAST_GUIDED, versionCode)
                .apply();
    }

    public static void clear(Context context) {
        getPreferences(context).edit().clear().apply();
    }
}
