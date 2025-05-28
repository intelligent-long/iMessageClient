package com.longx.intelligent.android.imessage.activity.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import android.content.Intent;
import android.os.Bundle;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.databinding.ActivityOtherAppSettingsSettingsBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.imessage.util.AppUtil;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;

public class OtherAppSettingsSettingsActivity extends BaseSettingsActivity {
    private ActivityOtherAppSettingsSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherAppSettingsSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupPreferenceFragment(savedInstanceState);
    }

    private void setupPreferenceFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.settings.getId(), new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Material3Preference preferenceAppSettings;
        private Material3Preference preferencePermission;
        private Material3Preference preferenceRestart;

        public SettingsFragment() {
            super();
        }

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_other_app_settings, rootKey);
            doDefaultActions();
        }

        @Override
        protected void bindPreferences() {
            preferenceAppSettings = findPreference(getString(R.string.preference_key_app_settings));
            preferencePermission = findPreference(getString(R.string.preference_key_permission));
            preferenceRestart = findPreference(getString(R.string.preference_key_restart));
        }

        @Override
        protected void showInfo() {

        }

        @Override
        protected void setupYiers() {
            preferenceAppSettings.setOnPreferenceClickListener(this);
            preferencePermission.setOnPreferenceClickListener(this);
            preferenceRestart.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            return false;
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if(preference.equals(preferenceAppSettings)){
                AppUtil.startSystemAppSettingActivity(getContext());
            }else if(preference.equals(preferencePermission)){
                startActivity(new Intent(getActivity(), PermissionSettingsActivity.class));
            }else if(preference.equals(preferenceRestart)){
                new ConfirmDialog((AppCompatActivity) getActivity(), "如果应用出现异常，重新启动可能可以解决问题。\n是否确定要继续？\n注意：此操作有极低概率导致数据异常。")
                        .setNegativeButton()
                        .setPositiveButton((dialog, which) -> {
                            AppUtil.restartApp(requireContext());
                        })
                        .create().show();
            }
            return true;
        }
    }
}