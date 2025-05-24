package com.longx.intelligent.android.imessage.activity.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.Preference;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.color.DynamicColors;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.AuthActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.InstanceStateKeys;
import com.longx.intelligent.android.imessage.activity.edituser.ChangeEmailActivity;
import com.longx.intelligent.android.imessage.activity.helper.ActivityOperator;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.dialog.AbstractDialog;
import com.longx.intelligent.android.imessage.dialog.ChoiceDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.UrlMapApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlobalBehaviors;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.net.ServerConfig;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.databinding.ActivityRootSettingsBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.ServerSettingDialog;
import com.longx.intelligent.android.imessage.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.imessage.util.AppUtil;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.ShareUtil;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Category;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3ListPreference;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3SwitchPreference;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RootSettingsActivity extends BaseSettingsActivity {
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
            restoreInstanceState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getIntent().putExtra(ExtraKeys.NEED_RESTORE_INSTANCE_STATE, true);
        saveInstanceState();
    }

    public void saveInstanceState() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            int appBarVerticalOffset = behavior.getTopAndBottomOffset();
            instanceState.putInt(InstanceStateKeys.RootSettingsActivity.APP_BAR_LAYOUT_STATE, appBarVerticalOffset);
        }
    }

    protected void restoreInstanceState() {
        int appBarVerticalOffset = instanceState.getInt(InstanceStateKeys.RootSettingsActivity.APP_BAR_LAYOUT_STATE, 0);
        binding.appbar.setExpanded(appBarVerticalOffset == 0, false);
    }

    private void setupPreferenceFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.settings.getId(), new SettingsFragment(getIntent().getBooleanExtra(ExtraKeys.AUTH_TO_SETTINGS, false)))
                    .commit();
        }
    }

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, ContentUpdater.OnServerContentUpdateYier {
        private boolean authToSettings;
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
        private Material3Category userCategory;
        private Material3Category privacyCategory;

        public SettingsFragment(){
            super();
        }

        public SettingsFragment(boolean authToSettings) {
            super();
            this.authToSettings = authToSettings;
        }

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_root, rootKey);

            doDefaultActions();
            initNightMode = SharedPreferencesAccessor.DefaultPref.getNightMode(getContext());

            if(savedInstanceState != null){
                authToSettings = savedInstanceState.getBoolean(InstanceStateKeys.RootSettingsActivity.AUTH_TO_SETTINGS, false);
            }
            if(authToSettings){
                userCategory.setVisible(false);
                privacyCategory.setVisible(false);
            }
            GlobalYiersHolder.holdYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putBoolean(InstanceStateKeys.RootSettingsActivity.AUTH_TO_SETTINGS, authToSettings);
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
            userCategory = findPreference(getString(R.string.preference_key_user_category));
            privacyCategory = findPreference(getString(R.string.preference_key_privacy_category));
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
            preferenceShare.setOnPreferenceClickListener(this);
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
            ServerConfig serverConfig;
            if(SharedPreferencesAccessor.ServerPref.isUseCentral(requireContext())){
                serverConfig = SharedPreferencesAccessor.ServerPref.getCentralServerConfig(requireContext());
            }else {
                serverConfig = SharedPreferencesAccessor.ServerPref.getCustomServerConfig(requireContext());
            }
            if(serverConfig == null) return;
            String serverSettingSummary;
            if(serverConfig.getPort() != 80) {
                serverSettingSummary = serverConfig.getHost() + ":" + serverConfig.getPort();
            }else {
                serverSettingSummary = serverConfig.getHost();
            }
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
                new ConfirmDialog(SettingsFragment.this.getActivity(), "是否继续？")
                        .setNegativeButton()
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
                AbstractDialog serverSettingDialog = new ServerSettingDialog(getActivity()).create();
                serverSettingDialog.setOnDismissListener(dialog -> {
                    updateServerSettingSummary();
                });
                serverSettingDialog.show();
            }else if(preference.equals(preferenceOtherAppSettings)){
                startActivity(new Intent(getContext(), OtherAppSettingsSettingsActivity.class));
            }else if(preference.equals(preferenceVersion)){
                startActivity(new Intent(getContext(), VersionSettingsActivity.class));
            }else if(preference.equals(preferenceUi)){
                startActivity(new Intent(getContext(), UiSettingsActivity.class));
            }else if(preference.equals(preferencePrivacy)){
                startActivity(new Intent(requireContext(), PrivacySettingsActivity.class));
            }else if(preference.equals(preferenceShare)){
                shareApp();
            }
            return true;
        }

        private void shareApp() {
            UrlMapApiCaller.fetchImessageWebHomeUrl(requireActivity(), new RetrofitApiCaller.CommonYier<OperationData>(requireActivity()){
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(requireActivity(), new int[]{}, () -> {
                        String imessageWebHomeUrl = data.getData(String.class);
                        Utils.copyTextToClipboard(requireContext(), imessageWebHomeUrl, imessageWebHomeUrl);
                        new ChoiceDialog(requireActivity(), "已将 " + Constants.APP_NAME + " 网站地址复制到剪贴板。")
                                .setPositiveButton("确定", null)
                                .setNeutralButton("直接分享", (dialog, which) -> {
                                    String shareStr = "[" + Constants.APP_NAME + "] 我正在使用 " + Constants.APP_NAME + "，非常不错的通讯应用，你也试试吧\n地址： " + imessageWebHomeUrl;
                                    String title = "分享 " + Constants.APP_NAME;
                                    ShareUtil.shareString(requireContext(), shareStr, title);
                                })
                                .create()
                                .show();
                    });
                }
            });
        }

        @Override
        public void onStartUpdate(String id, List<String> updatingIds, Object... objects) {

        }

        @Override
        public void onUpdateComplete(String id, List<String> updatingIds, Object... objects) {
            if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CURRENT_USER_INFO)){
                updateEmailSummary();
            }
        }
    }
}