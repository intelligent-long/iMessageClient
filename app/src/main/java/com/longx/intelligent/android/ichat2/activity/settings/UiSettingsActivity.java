package com.longx.intelligent.android.ichat2.activity.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.databinding.ActivityUiSettingsBinding;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3ListPreference;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3SwitchPreference;

public class UiSettingsActivity extends BaseActivity {
    private ActivityUiSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUiSettingsBinding.inflate(getLayoutInflater());
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
    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Material3ListPreference preferenceChatBubbleColor;

        public SettingsFragment() {
            super();
        }

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_ui, rootKey);
            doDefaultActions();
        }

        @Override
        protected void bindPreferences() {
            preferenceChatBubbleColor = findPreference(getString(R.string.preference_key_chat_bubble_color));
        }

        @Override
        protected void showInfo() {
            updateChatBubbleColorSummary(null);
        }

        private void updateChatBubbleColorSummary(String newValue){
            if(newValue == null) {
                newValue = preferenceChatBubbleColor.getValue();
            }
            int index = preferenceChatBubbleColor.findIndexOfValue(newValue);
            if (index != -1) {
                String entry = getResources().getStringArray(R.array.chat_bubble_color_entries)[index];
                preferenceChatBubbleColor.setSummary(entry);
            }
        }

        @Override
        protected void setupYiers() {
            preferenceChatBubbleColor.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            if(preference.equals(preferenceChatBubbleColor)){
                updateChatBubbleColorSummary((String) newValue);
            }
            return true;
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            return true;
        }
    }
}