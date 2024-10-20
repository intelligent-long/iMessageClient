package com.longx.intelligent.android.ichat2.activity.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.Preference;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.color.DynamicColors;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.AuthActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.InstanceStateKeys;
import com.longx.intelligent.android.ichat2.activity.edituser.ChangeEmailActivity;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.procedure.GlobalBehaviors;
import com.longx.intelligent.android.ichat2.procedure.ContentUpdater;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ServerSetting;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.ActivityRootSettingsBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.dialog.ServerSettingDialog;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.ichat2.util.AppUtil;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3ListPreference;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3SwitchPreference;

import java.util.List;

public class RootSettingsActivity extends BaseActivity {
    private ActivityRootSettingsBinding binding;
    public static final Bundle instanceState = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRootSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupPreferenceFragment(savedInstanceState);
        boolean needRestoreInstanceState = getIntent().getBooleanExtra(ExtraKeys.NEED_RESTORE_INSTANCE_STATE, true);
        if(needRestoreInstanceState){
            onRestoreInstanceState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getIntent().putExtra(ExtraKeys.NEED_RESTORE_INSTANCE_STATE, true);
        onSaveInstanceState();
    }

    public void onSaveInstanceState() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            int appBarVerticalOffset = behavior.getTopAndBottomOffset();
            instanceState.putInt(InstanceStateKeys.RootSettingsActivity.APP_BAR_LAYOUT_STATE, appBarVerticalOffset);
        }
    }

    protected void onRestoreInstanceState() {
        int appBarVerticalOffset = instanceState.getInt(InstanceStateKeys.RootSettingsActivity.APP_BAR_LAYOUT_STATE, 0);
        binding.appbar.setExpanded(appBarVerticalOffset == 0, false);
    }

    private void setupPreferenceFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.settings.getId(), new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, ContentUpdater.OnServerContentUpdateYier {
        private Material3Preference preferenceEditUser;
        private Material3Preference preferenceLogout;
        private Material3Preference preferencePrivacy;
        private Material3Preference preferenceEmail;
        private Material3Preference preferenceResetPassword;
        private Material3Preference preferenceServerSetting;
        private Material3ListPreference preferenceNightMode;
        private Material3SwitchPreference preferenceUseDynamicColor;
        private Material3Preference preferenceOtherAppSettings;
        private Material3Preference preferenceShare;
        private Material3Preference preferenceVersion;
        private Material3Preference preferenceUi;
        private int initNightMode;

        public SettingsFragment() {
            super();
        }

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_root, rootKey);
            doDefaultActions();
            initNightMode = SharedPreferencesAccessor.DefaultPref.getNightMode(getContext());
            GlobalYiersHolder.holdYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            GlobalYiersHolder.removeYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
        }

        @Override
        protected void bindPreferences() {
            preferenceEditUser = findPreference(getString(R.string.preference_key_edit_user));
            preferenceLogout = findPreference(getString(R.string.preference_key_logout));
            preferencePrivacy = findPreference(getString(R.string.preference_key_privacy));
            preferenceEmail = findPreference(getString(R.string.preference_key_email));
            preferenceResetPassword = findPreference(getString(R.string.preference_key_reset_password));
            preferenceServerSetting = findPreference(getString(R.string.preference_key_server_setting));
            preferenceNightMode = findPreference(getString(R.string.preference_key_night_mode));
            preferenceUseDynamicColor = findPreference(getString(R.string.preference_key_use_dynamic_color));
            preferenceOtherAppSettings = findPreference(getString(R.string.preference_key_other_app_settings));
            preferenceShare = findPreference(getString(R.string.preference_key_share));
            preferenceVersion = findPreference(getString(R.string.preference_key_version));
            preferenceUi = findPreference(getString(R.string.preference_key_ui));
        }

        @Override
        protected void showInfo() {
            updateNightModeSummary(null);
            updateVersionSummary();
            updateServerSettingSummary();
            updateEmailSummary();
            if (!DynamicColors.isDynamicColorAvailable()) {
                preferenceUseDynamicColor.setVisible(false);
            }
        }

        @Override
        protected void setupYiers() {
            preferenceEditUser.setOnPreferenceClickListener(this);
            preferenceLogout.setOnPreferenceClickListener(this);
            preferenceEmail.setOnPreferenceClickListener(this);
            preferenceResetPassword.setOnPreferenceClickListener(this);
            preferenceUseDynamicColor.setOnPreferenceClickListener(this);
            preferenceNightMode.setOnPreferenceChangeListener(this);
            preferenceServerSetting.setOnPreferenceClickListener(this);
            preferenceOtherAppSettings.setOnPreferenceClickListener(this);
            preferenceVersion.setOnPreferenceClickListener(this);
            preferenceUi.setOnPreferenceClickListener(this);
            preferencePrivacy.setOnPreferenceClickListener(this);
        }

        private void updateNightModeSummary(String newValue){
            if(newValue == null) {
                newValue = preferenceNightMode.getValue();
            }
            int index = preferenceNightMode.findIndexOfValue(newValue);
            if (index != -1) {
                String entry = getResources().getStringArray(R.array.night_mode_entries)[index];
                preferenceNightMode.setSummary(entry);
            }
        }

        private void checkAndChangeNightMode(String newValue){
            int nightModeNow = Integer.parseInt(newValue);
            if(nightModeNow != initNightMode){
                ActivityOperator.recreateAll();
            }
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            if(preference.getKey().equals(getString(R.string.preference_key_night_mode))){
                updateNightModeSummary((String) newValue);
                checkAndChangeNightMode((String) newValue);
            }
            return true;
        }

        private void updateEmailSummary(){
            Self self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(requireContext());
            preferenceEmail.setSummary(self.getEmail());
        }

        private void updateServerSettingSummary(){
            ServerSetting serverSetting = SharedPreferencesAccessor.ServerSettingPref.getServerSetting(requireContext());
            String serverSettingSummary = serverSetting.getHost() + ":" + serverSetting.getPort();
            preferenceServerSetting.setSummary(serverSettingSummary);
        }

        private void updateVersionSummary(){
            String versionSummary = AppUtil.getVersionName(requireContext()) + " (" + AppUtil.getVersionCode(requireContext()) + ")";
            preferenceVersion.setSummary(versionSummary);
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if(preference.equals(preferenceEditUser)){
                startActivity(new Intent(getActivity(), EditUserSettingsActivity.class));
            }else if(preference.equals(preferenceLogout)) {
                new ConfirmDialog((AppCompatActivity) SettingsFragment.this.getActivity(), "是否继续？")
                        .setNegativeButton(null)
                        .setPositiveButton((dialogInterface, i) -> {
                            GlobalBehaviors.doLogout(SettingsFragment.this.getActivity(), null, null);
                        })
                        .create().show();
            }else if(preference.equals(preferenceEmail)){
                startActivity(new Intent(getContext(), ChangeEmailActivity.class));
            }else if(preference.equals(preferenceResetPassword)){
                Intent intent = new Intent(getContext(), AuthActivity.class);
                intent.putExtra(ExtraKeys.CHANGE_PASSWORD_MODE, true);
                startActivity(intent);
            }else if(preference.equals(preferenceUseDynamicColor)){
                ActivityOperator.recreateAll();
            }else if(preference.equals(preferenceServerSetting)){
                new ServerSettingDialog((AppCompatActivity) getActivity()).create().show();
            }else if(preference.equals(preferenceOtherAppSettings)){
                startActivity(new Intent(getContext(), OtherAppSettingsSettingsActivity.class));
            }else if(preference.equals(preferenceVersion)){
                startActivity(new Intent(getContext(), VersionSettingsActivity.class));
            }else if(preference.equals(preferenceUi)){
                startActivity(new Intent(getContext(), UiSettingsActivity.class));
            }else if(preference.equals(preferencePrivacy)){
                startActivity(new Intent(requireContext(), PrivacySettingsActivity.class));
            }
            return true;
        }

        @Override
        public void onStartUpdate(String id, List<String> updatingIds) {

        }

        @Override
        public void onUpdateComplete(String id,List<String> updatingIds) {
            if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CURRENT_USER_INFO)){
                updateEmailSummary();
            }
        }
    }
}