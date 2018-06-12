package org.jinsuoji.jinsuoji.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.RemindActivity;
import org.jinsuoji.jinsuoji.RemindReceiver;

import java.util.Date;

public class MyAlarmManager {
    // 添加或修改提醒
    public static void replaceAlarm(Context context, int id, Date remindTime){
        Intent intent = new Intent(context, RemindReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putInt(RemindActivity.TODO_TO_BE_REMINDED, id);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent,0);
        //注册新提醒
        AlarmManager alarmManager;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Toast.makeText(context, R.string.no_alarms_found, Toast.LENGTH_SHORT).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, remindTime.getTime(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, remindTime.getTime(), pendingIntent);
            }
        }
    }

    // 移除提醒
    public static void removeAlarmIfExists(Context context, int id) {
        Intent intent = new Intent(context, RemindReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putInt(RemindActivity.TODO_TO_BE_REMINDED, id);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent,0);
        AlarmManager alarmManager;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Toast.makeText(context, R.string.no_alarms_found, Toast.LENGTH_SHORT).show();
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }
}
