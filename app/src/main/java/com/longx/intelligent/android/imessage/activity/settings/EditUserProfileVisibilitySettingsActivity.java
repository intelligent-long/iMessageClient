package com.longx.intelligent.android.imessage.activity.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.UserInfo;
import com.longx.intelligent.android.imessage.data.request.ChangeUserProfileVisibilityPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityEditUserProfileVisibilitySettingsBinding;
import com.longx.intelligent.android.imessage.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.imessage.net.retrofit.caller.PermissionApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3SwitchPreference;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class EditUserProfileVisibilitySettingsActivity extends BaseSettingsActivity {
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
        private Activity activity;

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_edit_user_profile_visibility, rootKey);
            doDefaultActions();
            GlobalYiersHolder.holdYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
            activity = requireActivity();
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
            Context applicationContext = requireContext().getApplicationContext();
            if(!preferenceChangeEmailVisibility.isEnabled()) return;
            if(!preferenceChangeSexVisibility.isEnabled()) return;
            if(!preferenceChangeRegionVisibility.isEnabled()) return;
            UserInfo.UserProfileVisibility serverUserProfileVisibility = SharedPreferencesAccessor.UserProfilePref.getServerUserProfileVisibility(applicationContext);
            boolean emailVisibilityChecked = preferenceChangeEmailVisibility.isChecked();
            boolean sexVisibilityChecked = preferenceChangeSexVisibility.isChecked();
            boolean regionVisibilityChecked = preferenceChangeRegionVisibility.isChecked();
            if(serverUserProfileVisibility != null && serverUserProfileVisibility.isEmailVisible() == emailVisibilityChecked && serverUserProfileVisibility.isSexVisible() == sexVisibilityChecked && serverUserProfileVisibility.isRegionVisible() == regionVisibilityChecked){
                return;
            }
            ChangeUserProfileVisibilityPostBody postBody = new ChangeUserProfileVisibilityPostBody(emailVisibilityChecked, sexVisibilityChecked, regionVisibilityChecked);
            PermissionApiCaller.changeUserProfileVisibility(null, postBody, new RetrofitApiCaller.BaseCommonYier<OperationStatus>(applicationContext){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(activity, new int[]{}, () -> {
                        SharedPreferencesAccessor.UserProfilePref.saveServerUserProfileVisibility(applicationContext,
                                new UserInfo.UserProfileVisibility(emailVisibilityChecked, sexVisibilityChecked, regionVisibilityChecked));
                        SharedPreferencesAccessor.UserProfilePref.saveAppUserProfileVisibility(applicationContext,
                                new UserInfo.UserProfileVisibility(emailVisibilityChecked, sexVisibilityChecked, regionVisibilityChecked));
                    });
                }

                @Override
                public void notOk(int code, String message, Response<OperationStatus> row, Call<OperationStatus> call) {
                    super.notOk(code, message, row, call);
                    SharedPreferencesAccessor.UserProfilePref.saveAppUserProfileVisibility(applicationContext,
                            new UserInfo.UserProfileVisibility(serverUserProfileVisibility.isEmailVisible(), serverUserProfileVisibility.isSexVisible(), serverUserProfileVisibility.isRegionVisible()));
                }

                @Override
                public void failure(Throwable t, Call<OperationStatus> call) {
                    super.failure(t, call);
                    SharedPreferencesAccessor.UserProfilePref.saveAppUserProfileVisibility(applicationContext,
                            new UserInfo.UserProfileVisibility(serverUserProfileVisibility.isEmailVisible(), serverUserProfileVisibility.isSexVisible(), serverUserProfileVisibility.isRegionVisible()));
                }
            });
        }
    }
}