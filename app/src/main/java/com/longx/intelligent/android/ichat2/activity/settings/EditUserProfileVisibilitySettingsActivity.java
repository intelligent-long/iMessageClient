package com.longx.intelligent.android.ichat2.activity.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.ActivityEditUserProfileVisibilityBinding;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3SwitchPreference;

import java.util.List;

public class EditUserProfileVisibilitySettingsActivity extends BaseActivity {
    private ActivityEditUserProfileVisibilityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditUserProfileVisibilityBinding.inflate(getLayoutInflater());
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

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, ContentUpdater.OnServerContentUpdateYier {
        private Material3SwitchPreference preferenceChangeEmailVisibility;
        private Material3SwitchPreference preferenceChangeSexVisibility;
        private Material3SwitchPreference preferenceChangeRegionVisibility;

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_edit_user_profile_visibility, rootKey);
            doDefaultActions();
            GlobalYiersHolder.holdYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            GlobalYiersHolder.removeYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
        }

        @Override
        protected void bindPreferences() {
            preferenceChangeEmailVisibility = findPreference(getString(R.string.preference_key_change_email_visibility));
            preferenceChangeSexVisibility = findPreference(getString(R.string.preference_key_change_sex_visibility));
            preferenceChangeRegionVisibility = findPreference(getString(R.string.preference_key_change_region_visibility));
        }

        @Override
        protected void showInfo() {
            Self self = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(requireContext());
        }

        @Override
        protected void setupYiers() {
            preferenceChangeEmailVisibility.setOnPreferenceChangeListener(this);
            preferenceChangeSexVisibility.setOnPreferenceChangeListener(this);
            preferenceChangeRegionVisibility.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            return true;
        }

        @Override
        public void onStartUpdate(String id, List<String> updatingIds) {

        }

        @Override
        public void onUpdateComplete(String id, List<String> updatingIds) {
            if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CURRENT_USER_INFO)){
                showInfo();
            }
        }
    }
}