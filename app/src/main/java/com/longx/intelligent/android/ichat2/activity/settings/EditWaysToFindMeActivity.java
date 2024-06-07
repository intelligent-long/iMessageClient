package com.longx.intelligent.android.ichat2.activity.settings;

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
import com.longx.intelligent.android.ichat2.data.UserInfo;
import com.longx.intelligent.android.ichat2.data.request.ChangeUserProfileVisibilityPostBody;
import com.longx.intelligent.android.ichat2.databinding.ActivityEditWaysToFindMeBinding;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.PrivacyApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3SwitchPreference;

import java.util.List;

public class EditWaysToFindMeActivity extends BaseActivity {
    private ActivityEditWaysToFindMeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditWaysToFindMeBinding.inflate(getLayoutInflater());
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
        private Material3SwitchPreference preferenceChangeCanFindMeByIchatId;
        private Material3SwitchPreference preferenceChangeCanFindMeByEmail;

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_edit_ways_to_find_me, rootKey);
            doDefaultActions();
            GlobalYiersHolder.holdYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            GlobalYiersHolder.removeYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
        }

        @Override
        protected void bindPreferences() {
            preferenceChangeCanFindMeByIchatId = findPreference(getString(R.string.preference_key_change_can_find_me_by_ichat_id));
            preferenceChangeCanFindMeByEmail = findPreference(getString(R.string.preference_key_change_can_find_me_by_email));
        }

        @Override
        protected void showInfo() {

        }

        @Override
        protected void setupYiers() {
            preferenceChangeCanFindMeByIchatId.setOnPreferenceChangeListener(this);
            preferenceChangeCanFindMeByEmail.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
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
        }
    }
}