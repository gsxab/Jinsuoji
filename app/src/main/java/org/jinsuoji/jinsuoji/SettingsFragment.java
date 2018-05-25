package org.jinsuoji.jinsuoji;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.view.View;
import android.preference.PreferenceManager;

import org.jinsuoji.jinsuoji.R;

/**
 * sync_freq:单选选项同步频率
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{

    private SwitchPreference sync_switch;
    private ListPreference sync_freq;
    private SwitchPreference sync_wifi;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        //从xml文件加载选项
        addPreferencesFromResource(R.xml.pref_settings);

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //prefs.registerOnSharedPreferenceChangeListener(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(this);
        sync_freq = (ListPreference)findPreference("pref_key_sync_frequence");
        sync_freq.setSummary(sync_freq.getEntry());

    }

    @Override
    //在一进去的时候就判断sync_switch是否被勾选
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sync_switch = (SwitchPreference)findPreference("pref_key_sync_switch");
        sync_freq = (ListPreference)findPreference("pref_key_sync_frequence");
        sync_wifi = (SwitchPreference)findPreference("pref_key_sync_wifi");
        if(!sync_switch.isChecked()){
            sync_freq.setEnabled(false);
            sync_wifi.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,Preference preference){
        //找到“同步数据”按钮
        if("pref_key_sync_switch".equals(preference.getKey())){
            sync_switch = (SwitchPreference)findPreference("pref_key_sync_switch");
            sync_freq = (ListPreference)findPreference("pref_key_sync_frequence");
            sync_wifi = (SwitchPreference)findPreference("pref_key_sync_wifi");
            //如果勾选“同步数据”，同步频率和wifi下同步选项都可操作
            sync_freq.setEnabled(sync_switch.isChecked());
            sync_wifi.setEnabled(sync_switch.isChecked());
        }
        return  super.onPreferenceTreeClick(preferenceScreen,preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key){
        sync_freq = (ListPreference)findPreference("pref_key_sync_frequence");
        if("pref_key_sync_frequence".equals(key)){
            sync_freq.setSummary(
                    sharedPreferences .getString("pref_key_sync_frequence","未设置"));
            //sync_freq.setSummary(sync_freq.getEntry());
        }
    }

}
