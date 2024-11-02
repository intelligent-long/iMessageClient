package com.longx.intelligent.android.ichat2.activity.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.databinding.ActivityPermissionSettingsBinding;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.ichat2.permission.SpecialPermissionOperator;
import com.longx.intelligent.android.ichat2.permission.LinkPermissionOperatorActivity;
import com.longx.intelligent.android.ichat2.permission.PermissionOperator;
import com.longx.intelligent.android.ichat2.permission.PermissionRequirementChecker;
import com.longx.intelligent.android.ichat2.permission.ToRequestPermissions;
import com.longx.intelligent.android.ichat2.permission.ToRequestPermissionsItems;
import com.longx.intelligent.android.ichat2.preference.PermissionPreference;

import java.util.ArrayList;
import java.util.List;

public class PermissionSettingsActivity extends BaseActivity {
    private ActivityPermissionSettingsBinding binding;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionSettingsBinding.inflate(getLayoutInflater());
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
        private PermissionPreference preferenceManageExternalStorage;
        private PermissionPreference preferenceRecordAudio;
        private PermissionPreference preferenceBluetoothConnect;

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
            preferenceManageExternalStorage = findPreference(getString(R.string.preference_key_manage_storage));
            preferenceRecordAudio = findPreference(getString(R.string.preference_key_record_audio));
            preferenceBluetoothConnect = findPreference(getString(R.string.preference_key_bluetooth_connect));
        }

        @Override
        public void onResume() {
            super.onResume();
            showInfo();
        }

        @Override
        protected void showInfo() {
            showBatteryRestriction();
            showNotificationPermission();
            showStoragePermission();
            showReadMediaImagesAndVideosPermission();
            showManageExternalStoragePermission();
            showRecordAudioPermission();
            showBluetoothConnectPermission();
        }

        private void showBatteryRestriction() {
            boolean ignoringBatteryOptimizations = SpecialPermissionOperator.isIgnoringBatteryOptimizations(requireContext());
            preferenceBatteryRestriction.setChecked(ignoringBatteryOptimizations);
        }

        private void showNotificationPermission() {
            if(PermissionRequirementChecker.needNotificationPermission()){
                boolean hasPermissions = PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.showNotification);
                preferenceNotification.setChecked(hasPermissions);
            }else {
                preferenceNotification.setVisible(false);
            }
        }

        private void showStoragePermission() {
            if(PermissionRequirementChecker.needExternalStoragePermission()) {
                boolean hasPermissions = PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.writeAndReadExternalStorage);
                preferenceStorage.setChecked(hasPermissions);
            }else {
                preferenceStorage.setVisible(false);
            }
        }

        private void showReadMediaImagesAndVideosPermission() {
            if(PermissionRequirementChecker.needReadMediaImageAndVideoPermission()) {
                boolean hasPermissions = PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.readMediaImagesAndVideos);
                preferenceReadMediaImagesAndVideos.setChecked(hasPermissions);
            }else {
                preferenceReadMediaImagesAndVideos.setVisible(false);
            }
        }

        private void showManageExternalStoragePermission(){
            if(PermissionRequirementChecker.needManageExternalStoragePermission()){
                boolean externalStorageManager = SpecialPermissionOperator.isExternalStorageManager();
                preferenceManageExternalStorage.setChecked(externalStorageManager);
            }else {
                preferenceManageExternalStorage.setVisible(false);
            }
        }

        private void showRecordAudioPermission() {
            boolean hasPermissions = PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.recordAudio);
            preferenceRecordAudio.setChecked(hasPermissions);
        }

        private void showBluetoothConnectPermission() {
            if(PermissionRequirementChecker.needBluetoothConnectPermission()){
                boolean hasPermissions = PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.bluetoothConnect);
                preferenceBluetoothConnect.setChecked(hasPermissions);
            }else {
                preferenceBluetoothConnect.setVisible(false);
            }
        }

        @Override
        protected void setupYiers() {
            preferenceBatteryRestriction.setOnPreferenceClickListener(this);
            preferenceNotification.setOnPreferenceClickListener(this);
            preferenceStorage.setOnPreferenceClickListener(this);
            preferenceReadMediaImagesAndVideos.setOnPreferenceClickListener(this);
            preferenceManageExternalStorage.setOnPreferenceClickListener(this);
            preferenceRecordAudio.setOnPreferenceClickListener(this);
            preferenceBluetoothConnect.setOnPreferenceClickListener(this);
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
                if (PermissionRequirementChecker.needReadMediaImageAndVideoPermission()) {
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
                if (PermissionRequirementChecker.needNotificationPermission()) {
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
                                        showNotificationPermission();
                                    }
                                }).startRequestPermissions((LinkPermissionOperatorActivity) requireActivity());
                    }
                }
            }else if(preference.equals(preferenceManageExternalStorage)){
                if(SpecialPermissionOperator.isExternalStorageManager()){
                    showPermissionGrantedMessage();
                }else {
                    boolean success = SpecialPermissionOperator.requestManageExternalStorage(requireActivity());
                    if(!success){
                        MessageDisplayer.autoShow(requireActivity(), "错误", MessageDisplayer.Duration.LONG);
                    }
                }
            }else if(preference.equals(preferenceRecordAudio)){
                if(PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.recordAudio)){
                    showPermissionGrantedMessage();
                }else {
                    List<ToRequestPermissions> toRequestPermissionsList = new ArrayList<>();
                    toRequestPermissionsList.add(ToRequestPermissionsItems.recordAudio);
                    new PermissionOperator(requireActivity(), toRequestPermissionsList,
                            new PermissionOperator.ShowCommonMessagePermissionResultCallback(requireActivity()){
                                @Override
                                public void onPermissionGranted(int requestCode) {
                                    super.onPermissionGranted(requestCode);
                                    showRecordAudioPermission();
                                }
                            }).startRequestPermissions((LinkPermissionOperatorActivity) requireActivity());

                }
            }else if(preference.equals(preferenceBluetoothConnect)){
                if(PermissionRequirementChecker.needBluetoothConnectPermission()){
                    if(PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.bluetoothConnect)){
                        showPermissionGrantedMessage();
                    }else {
                        List<ToRequestPermissions> toRequestPermissionsList = new ArrayList<>();
                        toRequestPermissionsList.add(ToRequestPermissionsItems.bluetoothConnect);
                        new PermissionOperator(requireActivity(), toRequestPermissionsList,
                                new PermissionOperator.ShowCommonMessagePermissionResultCallback(requireActivity()) {
                                    @Override
                                    public void onPermissionGranted(int requestCode) {
                                        super.onPermissionGranted(requestCode);
                                        showBluetoothConnectPermission();
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