package cn.elva.wcfp.ui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.annotation.Nullable;

import cn.elva.wcfp.R;
import cn.elva.wcfp.utils.AESHelper;
import cn.elva.wcfp.utils.FingerprintHandler;

/**
 * The app setting UI
 */
public class PaySettingActivity extends Activity {

    FingerprintHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Replace with PaySettingFragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, new PaySettingFragment());
        fragmentTransaction.commit();
    }

    public static class PaySettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
        private SharedPreferences sharedPreferences;
        private Preference prefEnable;
        private Preference prefPwd;
        private SharedPreferences.Editor spEditor;
        private static final String SHARED_PREFERENCE_KEY = "wcfp";
        private static final String DU_EN_KEY = "ssg9W5h7va0vhmMU";
        private static final String PREF_KEY_ENABLE = "enable";
        private static final String PREF_KEY_PWD = "pwd";
        private Context mContext;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            mContext = getActivity();
            sharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
            super.onCreate(savedInstanceState);
            // Load PaySettingFragment from xml resource
            addPreferencesFromResource(R.xml.fragment_pay_setting);
            prefEnable = findPreference(PREF_KEY_ENABLE);
            prefPwd = findPreference(PREF_KEY_PWD);
            // Add preference change listener
            prefEnable.setOnPreferenceChangeListener(this);
            prefPwd.setOnPreferenceChangeListener(this);
            prefPwd.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            switch (preference.getKey()) {
                case PREF_KEY_ENABLE:
                    return true;
                case PREF_KEY_PWD:
                    spEditor = sharedPreferences.edit();
                    final String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    final String key = AESHelper.encrypt(android_id, DU_EN_KEY);
                    spEditor.putString(PREF_KEY_PWD, AESHelper.encrypt((String) newValue, key));
                    spEditor.apply();
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals(PREF_KEY_PWD)) {
                ((EditTextPreference) preference).setText(sharedPreferences.getString("pwd", ""));
            }
            return true;
        }
    }
}


