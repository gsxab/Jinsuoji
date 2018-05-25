package org.jinsuoji.jinsuoji;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SettingsActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);  //【这里注意！！】我不知道这里要弄什么lyout，我就用leftdrawer了
        //加载SettingsFragment
        FragmentManager fragmentManager = getFragmentManager();
        //【注意！】第一个参数是containerView，我也不知道用什么
        fragmentManager.beginTransaction().replace(R.id.settings_frame, new SettingsFragment()).commit();

    }
}
