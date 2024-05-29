package com.longx.intelligent.android.ichat2.activity.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.databinding.ActivityPermissionBinding;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.ichat2.permission.SpecialPermissionOperator;
import com.longx.intelligent.android.ichat2.permission.LinkPermissionOperatorActivity;
import com.longx.intelligent.android.ichat2.permission.PermissionOperator;
import com.longx.intelligent.android.ichat2.permission.PermissionUtil;
import com.longx.intelligent.android.ichat2.permission.ToRequestPermissions;
import com.longx.intelligent.android.ichat2.permission.ToRequestPermissionsItems;
import com.longx.intelligent.android.ichat2.preference.PermissionPreference;

import java.util.ArrayList;
import java.util.List;

public class PermissionActivity extends BaseActivity {
    private ActivityPermissionBinding binding;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupPreferenceFragment(savedInstanceState);
    }

    private void setupPreferenceFragment(Bundle savedInstanceState) {
        settingsFragment = new SettingsFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.settings.getId(), settingsFragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SpecialPermissionOperator.IGNORE_BATTERY_OPTIMIZATIONS_REQUEST_CODE){
            if(SpecialPermissionOperator.isIgnoringBatteryOptimizations(this)){
                SharedPreferencesAccessor.DefaultPref.enableRequestIgnoreBatteryOptimize(this);
            }
            if(settingsFragment != null){
                settingsFragment.showBatteryRestriction();
            }
        }
    }

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
        private PermissionPreference preferenceBatteryRestriction;
        private PermissionPreference preferenceNotification;
        private PermissionPreference preferenceStorage;
        private PermissionPreference preferenceReadMediaImagesAndVideos;

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_permission, rootKey);
            doDefaultActions();
        }

        @Override
        protected void bindPreferences() {
            preferenceBatteryRestriction = findPreference(getString(R.string.preference_key_battery_restriction));
            preferenceNotification = findPreference(getString(R.string.preference_key_notification));
            preferenceStorage = findPreference(getString(R.string.preference_key_storage));
            preferenceReadMediaImagesAndVideos = findPreference(getString(R.string.preference_key_read_media_images_and_videos));
        }

        @Override
        protected void showInfo() {
            showBatteryRestriction();
            showNotification();
            showStoragePermission();
            showReadMediaImagesAndVideosPermission();
        }

        @Override
        public void onResume() {
            super.onResume();
            showInfo();
        }

        private void showBatteryRestriction() {
            boolean ignoringBatteryOptimizations = SpecialPermissionOperator.isIgnoringBatteryOptimizations(requireContext());
            preferenceBatteryRestriction.setChecked(ignoringBatteryOptimizations);
        }

        private void showNotification() {
            if(PermissionUtil.needNotificationPermission()){
                boolean hasPermissions = PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.showNotification);
                preferenceNotification.setChecked(hasPermissions);
            }else {
                preferenceNotification.setVisible(false);
            }
        }

        private void showStoragePermission() {
            if(PermissionUtil.needExternalStoragePermission()) {
                boolean hasPermissions = PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.writeAndReadExternalStorage);
                preferenceStorage.setChecked(hasPermissions);
            }else {
                preferenceStorage.setVisible(false);
            }
        }

        private void showReadMediaImagesAndVideosPermission() {
            if(PermissionUtil.needReadMediaImageAndVideoPermission()) {
                boolean hasPermissions = PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.readMediaImagesAndVideos);
                preferenceReadMediaImagesAndVideos.setChecked(hasPermissions);
            }else {
                preferenceReadMediaImagesAndVideos.setVisible(false);
            }
        }

        @Override
        protected void setupYiers() {
            preferenceBatteryRestriction.setOnPreferenceClickListener(this);
            preferenceNotification.setOnPreferenceClickListener(this);
            preferenceStorage.setOnPreferenceClickListener(this);
            preferenceReadMediaImagesAndVideos.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if (preference.equals(preferenceBatteryRestriction)) {
                if(!SpecialPermissionOperator.isIgnoringBatteryOptimizations(requireContext())){
                    boolean success = SpecialPermissionOperator.requestIgnoreBatteryOptimizations(requireActivity());
                    if(!success){
                        MessageDisplayer.autoShow(requireContext(), "错误", MessageDisplayer.Duration.LONG);
                    }
                }else {
                    showPermissionGrantedMessage();
                }
            } else if (preference.equals(preferenceStorage)) {
                if(PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.writeAndReadExternalStorage)){
                    showPermissionGrantedMessage();
                }else {
                    List<ToRequestPermissions> toRequestPermissionsList = new ArrayList<>();
                    toRequestPermissionsList.add(ToRequestPermissionsItems.writeAndReadExternalStorage);
                    new PermissionOperator(requireActivity(), toRequestPermissionsList,
                            new PermissionOperator.ShowCommonMessagePermissionResultCallback(requireActivity()) {
                                @Override
                                public void onPermissionGranted(int requestCode) {
                                    super.onPermissionGranted(requestCode);
                                    showStoragePermission();
                                }
                            }).startRequestPermissions((LinkPermissionOperatorActivity) requireActivity());
                }
            }else if(preference.equals(preferenceReadMediaImagesAndVideos)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if(PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.readMediaImagesAndVideos)){
                        showPermissionGrantedMessage();
                    }else {
                        List<ToRequestPermissions> toRequestPermissionsList = new ArrayList<>();
                        toRequestPermissionsList.add(ToRequestPermissionsItems.readMediaImagesAndVideos);
                        new PermissionOperator(requireActivity(), toRequestPermissionsList,
                                new PermissionOperator.ShowCommonMessagePermissionResultCallback(requireActivity()) {
                                    @Override
                                    public void onPermissionGranted(int requestCode) {
                                        super.onPermissionGranted(requestCode);
                                        showReadMediaImagesAndVideosPermission();
                                    }
                                }).startRequestPermissions((LinkPermissionOperatorActivity) requireActivity());
                    }
                }
            }else if(preference.equals(preferenceNotification)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if(PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.showNotification)){
                        showPermissionGrantedMessage();
                    }else {
                        List<ToRequestPermissions> toRequestPermissionsList = new ArrayList<>();
                        toRequestPermissionsList.add(ToRequestPermissionsItems.showNotification);
                        new PermissionOperator(requireActivity(), toRequestPermissionsList,
                                new PermissionOperator.ShowCommonMessagePermissionResultCallback(requireActivity()) {
                                    @Override
                                    public void onPermissionGranted(int requestCode) {
                                        super.onPermissionGranted(requestCode);
                                        showNotification();
                                    }
                                }).startRequestPermissions((LinkPermissionOperatorActivity) requireActivity());
                    }
                }
            }
            return true;
        }

        private void showPermissionGrantedMessage() {
            MessageDisplayer.autoShow(requireActivity(), "已授权", MessageDisplayer.Duration.SHORT);
        }
    }
}