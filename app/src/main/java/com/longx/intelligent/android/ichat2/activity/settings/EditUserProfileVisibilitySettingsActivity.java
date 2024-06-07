package com.longx.intelligent.android.ichat2.activity.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.UserInfo;
import com.longx.intelligent.android.ichat2.data.request.ChangeUserProfileVisibilityPostBody;
import com.longx.intelligent.android.ichat2.databinding.ActivityEditUserProfileVisibilitySettingsBinding;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.PrivacyApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3SwitchPreference;

import java.util.List;

public class EditUserProfileVisibilitySettingsActivity extends BaseActivity {
    private ActivityEditUserProfileVisibilitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditUserProfileVisibilitySettingsBinding.inflate(getLayoutInflater());
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
            UserInfo.UserProfileVisibility appUserProfileVisibility = SharedPreferencesAccessor.UserProfilePref.getAppUserProfileVisibility(requireContext());
            if(appUserProfileVisibility == null){
                preferenceChangeEmailVisibility.setEnabled(false);
                preferenceChangeSexVisibility.setEnabled(false);
                preferenceChangeRegionVisibility.setEnabled(false);
            }else {
                preferenceChangeEmailVisibility.setChecked(appUserProfileVisibility.isEmailVisible());
                preferenceChangeSexVisibility.setChecked(appUserProfileVisibility.isSexVisible());
                preferenceChangeRegionVisibility.setChecked(appUserProfileVisibility.isRegionVisible());
            }
        }

        @Override
        protected void setupYiers() {
            preferenceChangeEmailVisibility.setOnPreferenceChangeListener(this);
            preferenceChangeSexVisibility.setOnPreferenceChangeListener(this);
            preferenceChangeRegionVisibility.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            boolean emailChecked = preferenceChangeEmailVisibility.isChecked();
            boolean sexChecked = preferenceChangeSexVisibility.isChecked();
            boolean regionChecked = preferenceChangeRegionVisibility.isChecked();
            if(preference.equals(preferenceChangeEmailVisibility)){
                emailChecked = (boolean) newValue;
            }else if(preference.equals(preferenceChangeSexVisibility)){
                sexChecked = (boolean) newValue;
            }else if(preference.equals(preferenceChangeRegionVisibility)){
                regionChecked = (boolean) newValue;
            }
            SharedPreferencesAccessor.UserProfilePref.saveAppUserProfileVisibility(requireContext(),
                    new UserInfo.UserProfileVisibility(emailChecked, sexChecked, regionChecked));
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

        @Override
        public void onDetach() {
            super.onDetach();
            updateServerData();
        }

        private void updateServerData() {
            ChangeUserProfileVisibilityPostBody postBody = new ChangeUserProfileVisibilityPostBody(preferenceChangeEmailVisibility.isChecked(), preferenceChangeSexVisibility.isChecked(), preferenceChangeRegionVisibility.isChecked());
            PrivacyApiCaller.changeUserProfileVisibility(null, postBody, new RetrofitApiCaller.BaseCommonYier<>(requireContext().getApplicationContext()));
        }
    }
}