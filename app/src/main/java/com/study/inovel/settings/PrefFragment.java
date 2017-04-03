package com.study.inovel.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;


import com.study.inovel.R;

/**
 * Created by dnw on 2017/4/3.
 */
public class PrefFragment extends PreferenceFragment {
    ListPreference lp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }
    private void init()
    {
        //从xml中加载布局
        addPreferencesFromResource(R.xml.preferences);
        //getPreferenceManager().setSharedPreferencesName("mySetting");
        lp=(ListPreference)findPreference("time_of_refresh");
        lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if(preference instanceof ListPreference)
                {
                    ListPreference listPreference=(ListPreference)preference;
                    CharSequence[] entries=listPreference.getEntries();
                    int index=listPreference.findIndexOfValue((String)o);
                    listPreference.setSummary(entries[index]);
                }
                return true;
            }
        });
        lp.setSummary(lp.getEntry());
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
