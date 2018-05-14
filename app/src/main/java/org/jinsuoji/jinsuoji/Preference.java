package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 封装SharedPreference(偏好设置，储存少量数据，对应于数据文件夹的一个文件).
 */
public class Preference {
    private static final String TAG = "o.j.j.Preference";

    // 偏好设置文件名
    private static final String PREFERENCE_NAME = "my_pref";
    // 上次打开引导界面是在哪个版本
    private static final String KEY_LAST_GUIDED = "guide_activity";
    // 是否显示已完成项
    private static final String KEY_SHOW_FINISHED = "show_finished";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
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

    public static void setShowFinished(Context context, boolean showUnfinished) {
        if (context == null) return;
        getPreferences(context)
                .edit()
                .putBoolean(KEY_SHOW_FINISHED, showUnfinished)
                .apply();
    }

    public static boolean getShowFinished(@NonNull Context context) {
        return getPreferences(context).getBoolean(KEY_SHOW_FINISHED, false);
    }

    public static void clear(Context context) {
        getPreferences(context).edit().clear().apply();
    }
}
