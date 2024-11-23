package com.longx.intelligent.android.ichat2.activity.settings;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.MainActivity;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.databinding.ActivityUiSettingsBinding;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.ichat2.yier.ChangeUiYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3ListPreference;

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
    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        private Material3ListPreference preferenceChatBubbleColor;
        private Material3ListPreference preferenceBottomNavigationViewLabelVisibilityMode;
        private Material3ListPreference preferenceMainActivityFragmentSwitchMode;
        private Material3ListPreference preferenceSnackbarAppearance;

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
            preferenceBottomNavigationViewLabelVisibilityMode = findPreference(getString(R.string.preference_key_bottom_navigation_view_label_visibility_mode));
            preferenceMainActivityFragmentSwitchMode = findPreference(getString(R.string.preference_key_main_activity_fragment_switch_mode));
            preferenceSnackbarAppearance = findPreference(getString(R.string.preference_key_snackbar_appearance));
        }

        @Override
        protected void showInfo() {
            updateChatBubbleColorSummary(null);
            updateBottomNavigationViewLabelVisibilityMode(null);
            updateMainActivityFragmentSwitchMode(null);
            updateSnackbarAppearance(null);
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

        private void updateBottomNavigationViewLabelVisibilityMode(String newValue){
            if(newValue == null){
                newValue = preferenceBottomNavigationViewLabelVisibilityMode.getValue();
            }
            int index = preferenceBottomNavigationViewLabelVisibilityMode.findIndexOfValue(newValue);
            if(index != -1){
                String entry = getResources().getStringArray(R.array.bottom_navigation_view_label_visibility_mode_entries)[index];
                preferenceBottomNavigationViewLabelVisibilityMode.setSummary(entry);
            }
        }

        private void updateMainActivityFragmentSwitchMode(String newValue){
            if(newValue == null){
                newValue = preferenceMainActivityFragmentSwitchMode.getValue();
            }
            int index = preferenceMainActivityFragmentSwitchMode.findIndexOfValue(newValue);
            if(index != -1){
                String entry = getResources().getStringArray(R.array.main_activity_fragment_switch_mode_entries)[index];
                preferenceMainActivityFragmentSwitchMode.setSummary(entry);
            }
        }

        private void updateSnackbarAppearance(String newValue){
            if(newValue == null){
                newValue = preferenceSnackbarAppearance.getValue();
            }
            int index = preferenceSnackbarAppearance.findIndexOfValue(newValue);
            if(index != -1){
                String entry = getResources().getStringArray(R.array.snackbar_appearance_entries)[index];
                preferenceSnackbarAppearance.setSummary(entry);
            }
        }

        @Override
        protected void setupYiers() {
            preferenceChatBubbleColor.setOnPreferenceChangeListener(this);
            preferenceBottomNavigationViewLabelVisibilityMode.setOnPreferenceChangeListener(this);
            preferenceMainActivityFragmentSwitchMode.setOnPreferenceChangeListener(this);
            preferenceSnackbarAppearance.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            if(preference.equals(preferenceChatBubbleColor)){
                updateChatBubbleColorSummary((String) newValue);
            }else if(preference.equals(preferenceBottomNavigationViewLabelVisibilityMode)){
                updateBottomNavigationViewLabelVisibilityMode((String) newValue);
            }else if(preference.equals(preferenceMainActivityFragmentSwitchMode)){
                updateMainActivityFragmentSwitchMode((String) newValue);
                ActivityOperator.getActivitiesOf(MainActivity.class).forEach(Activity::recreate);
            }else if(preference.equals(preferenceSnackbarAppearance)){
                updateSnackbarAppearance((String) newValue);
            }
            return true;
        }
    }
}