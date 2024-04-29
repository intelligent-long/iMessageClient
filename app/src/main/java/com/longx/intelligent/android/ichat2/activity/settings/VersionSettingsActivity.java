package com.longx.intelligent.android.ichat2.activity.settings;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import android.content.Intent;
import android.os.Bundle;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.OpenSourceLicensesActivity;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.databinding.ActivityVersionSettingsBinding;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.ichat2.util.AppUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;

public class VersionSettingsActivity extends BaseActivity {
    private ActivityVersionSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVersionSettingsBinding.inflate(getLayoutInflater());
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
        private Material3Preference preferenceAuthor;
        private Material3Preference preferenceVersionName;
        private Material3Preference preferenceVersionCode;
        private Material3Preference preferenceOpenSourceLicenses;

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_version, rootKey);
            doDefaultActions();
        }

        @Override
        protected void bindPreferences() {
            preferenceAuthor = findPreference(getString(R.string.preference_key_author));
            preferenceVersionName = findPreference(getString(R.string.preference_key_version_name));
            preferenceVersionCode = findPreference(getString(R.string.preference_key_version_code));
            preferenceOpenSourceLicenses = findPreference(getString(R.string.preference_key_open_source_licenses));
        }

        @Override
        protected void showInfo() {
            preferenceAuthor.setSummary(Constants.AUTHOR);
            preferenceVersionName.setSummary(AppUtil.getVersionName(requireContext()));
            preferenceVersionCode.setSummary(String.valueOf(AppUtil.getVersionCode(requireContext())));
        }

        @Override
        protected void setupYiers() {
            preferenceOpenSourceLicenses.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            return false;
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if(preference.equals(preferenceOpenSourceLicenses)){
                startActivity(new Intent(requireContext(), OpenSourceLicensesActivity.class));
            }
            return true;
        }
    }
}