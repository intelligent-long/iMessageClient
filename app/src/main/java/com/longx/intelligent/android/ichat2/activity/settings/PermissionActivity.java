package com.longx.intelligent.android.ichat2.activity.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;

import android.content.Intent;
import android.os.Bundle;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.databinding.ActivityPermissionBinding;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.ichat2.permission.BatteryRestrictionOperator;
import com.longx.intelligent.android.ichat2.permission.LinkPermissionOperatorActivity;
import com.longx.intelligent.android.ichat2.permission.PermissionOperator;
import com.longx.intelligent.android.ichat2.permission.PermissionUtil;
import com.longx.intelligent.android.ichat2.permission.ToRequestPermissionsItems;
import com.longx.intelligent.android.ichat2.preference.PermissionPreference;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;

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
        if(requestCode == BatteryRestrictionOperator.REQUEST_CODE){
            if(BatteryRestrictionOperator.isIgnoringBatteryOptimizations(this)){
                SharedPreferencesAccessor.DefaultPref.enableRequestIgnoreBatteryOptimize(this);
            }
            if(settingsFragment != null){
                settingsFragment.showBatteryRestriction();
            }
        }
    }

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
        private PermissionPreference preferenceBatteryRestriction;
        private PermissionPreference preferenceStorage;

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_permission, rootKey);
            doDefaultActions();
        }

        @Override
        protected void bindPreferences() {
            preferenceBatteryRestriction = findPreference(getString(R.string.preference_key_battery_restriction));
            preferenceStorage = findPreference(getString(R.string.preference_key_storage));
        }

        @Override
        protected void showInfo() {
            showBatteryRestriction();
            showStoragePermission();
        }

        @Override
        public void onResume() {
            super.onResume();
            showInfo();
        }

        private void showBatteryRestriction() {
            boolean ignoringBatteryOptimizations = BatteryRestrictionOperator.isIgnoringBatteryOptimizations(requireContext());
            preferenceBatteryRestriction.setChecked(ignoringBatteryOptimizations);
        }

        private void showStoragePermission() {
            if(PermissionUtil.needExternalStoragePermission()) {
                boolean hasWriteAndReadExternalStoragePermissions = PermissionOperator.hasPermissions(requireActivity(), ToRequestPermissionsItems.writeAndReadExternalStorage);
                preferenceStorage.setChecked(hasWriteAndReadExternalStoragePermissions);
            }else {
                preferenceStorage.setVisible(false);
            }
        }

        @Override
        protected void setupYiers() {
            preferenceBatteryRestriction.setOnPreferenceClickListener(this);
            preferenceStorage.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if (preference.equals(preferenceBatteryRestriction)) {
                if(!BatteryRestrictionOperator.isIgnoringBatteryOptimizations(requireContext())){
                    boolean success = BatteryRestrictionOperator.requestIgnoreBatteryOptimizations(requireActivity());
                    if(!success){
                        MessageDisplayer.autoShow(requireContext(), "错误", MessageDisplayer.Duration.LONG);
                    }
                }else {
                    showPermissionGrantedMessage();
                }
            } else if (preference.equals(preferenceStorage)) {
                boolean requested = new PermissionOperator(requireActivity(), ToRequestPermissionsItems.writeAndReadExternalStorage,
                        new PermissionOperator.ShowCommonMessagePermissionResultCallback(requireActivity()) {
                            @Override
                            public void onPermissionGranted() {
                                super.onPermissionGranted();
                                showStoragePermission();
                            }
                        }).requestPermissions((LinkPermissionOperatorActivity) requireActivity());
                if(!requested){
                    showPermissionGrantedMessage();
                }
            }
            return true;
        }

        private void showPermissionGrantedMessage() {
            MessageDisplayer.autoShow(requireActivity(), "已授权", MessageDisplayer.Duration.SHORT);
        }
    }
}