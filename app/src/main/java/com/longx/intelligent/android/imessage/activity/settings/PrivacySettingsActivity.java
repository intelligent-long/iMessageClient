package com.longx.intelligent.android.imessage.activity.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.BroadcastChannelPermissionActivity;
import com.longx.intelligent.android.imessage.activity.ExcludeBroadcastChannelActivity;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.databinding.ActivityPrivacySettingsBinding;
import com.longx.intelligent.android.imessage.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;

public class PrivacySettingsActivity extends BaseActivity {
    private ActivityPrivacySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacySettingsBinding.inflate(getLayoutInflater());
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

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
        private Material3Preference preferenceUserProfileVisibility;
        private Material3Preference preferenceWaysToFindMe;
        private Material3Preference preferenceBroadcastChannelPermission;
        private Material3Preference preferenceDoNotSeeBroadcastChannel;

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_privacy, rootKey);
            doDefaultActions();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        protected void bindPreferences() {
            preferenceUserProfileVisibility = findPreference(getString(R.string.preference_key_user_profile_visibility));
            preferenceWaysToFindMe = findPreference(getString(R.string.preference_key_ways_to_find_me));
            preferenceBroadcastChannelPermission = findPreference(getString(R.string.preference_key_broadcast_channel_permission));
            preferenceDoNotSeeBroadcastChannel = findPreference(getString(R.string.preference_key_do_not_see_broadcast_channel));
        }

        @Override
        protected void showInfo() {
        }

        @Override
        protected void setupYiers() {
            preferenceUserProfileVisibility.setOnPreferenceClickListener(this);
            preferenceWaysToFindMe.setOnPreferenceClickListener(this);
            preferenceBroadcastChannelPermission.setOnPreferenceClickListener(this);
            preferenceDoNotSeeBroadcastChannel.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if(preference.equals(preferenceUserProfileVisibility)){
                startActivity(new Intent(requireContext(), EditUserProfileVisibilitySettingsActivity.class));
            }else if(preference.equals(preferenceWaysToFindMe)){
                startActivity(new Intent(requireContext(), EditWaysToFindMeActivity.class));
            }else if(preference.equals(preferenceBroadcastChannelPermission)){
                startActivity(new Intent(requireContext(), BroadcastChannelPermissionActivity.class));
            }else if(preference.equals(preferenceDoNotSeeBroadcastChannel)){
                startActivity(new Intent(requireActivity(), ExcludeBroadcastChannelActivity.class));
            }
            return true;
        }
    }
}