package org.jinsuoji.jinsuoji;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

public class SettingsActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        //加载SettingsFragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, new SettingsFragment())
                .commit();
    }
}
