package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 封装SharedPreference(偏好设置，储存少量数据，对应于数据文件夹的一个文件).
 */
public class Preference {
    //偏好设置文件名
    private static final String PREFERENCE_NAME = "my_pref";
    //引导界面KEY
    private static final String KEY_GUIDED = "guide_activity";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 判断是否引导过.
     *
     * @param context 应用上下文
     * @return  是否已经引导过 true引导过了 false未引导
     */
    public static boolean isGuided(Context context) {
        return context != null
                && getPreferences(context).getBoolean(KEY_GUIDED, false);
    }

    /**
     * 设置被引导过了.
     *
     * @param context 应用上下文
     */
    public static void setGuided(Context context){
        if (context==null) return;
        getPreferences(context)//保存修改后的值
                .edit()
                .putBoolean(KEY_GUIDED, true)
                .apply();
    }
}
