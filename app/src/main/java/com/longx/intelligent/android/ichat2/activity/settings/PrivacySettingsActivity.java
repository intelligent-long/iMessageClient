package com.longx.intelligent.android.ichat2.activity.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.Preference;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.ActivityPrivacySettingsBinding;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3SwitchPreference;

import java.util.List;

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
        }

        @Override
        protected void showInfo() {
        }

        @Override
        protected void setupYiers() {
            preferenceUserProfileVisibility.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            startActivity(new Intent(requireContext(), EditUserProfileVisibilitySettingsActivity.class));
            return true;
        }
    }
}