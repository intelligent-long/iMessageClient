package com.longx.intelligent.android.imessage.activity.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.UserInfo;
import com.longx.intelligent.android.imessage.data.request.ChangeWaysToFindMePostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityEditWaysToFindMeBinding;
import com.longx.intelligent.android.imessage.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.imessage.net.retrofit.caller.PermissionApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3SwitchPreference;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

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
        private Material3SwitchPreference preferenceChangeCanFindMeByImessageId;
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
            preferenceChangeCanFindMeByImessageId = findPreference(getString(R.string.preference_key_change_can_find_me_by_imessage_id));
            preferenceChangeCanFindMeByEmail = findPreference(getString(R.string.preference_key_change_can_find_me_by_email));
        }

        @Override
        protected void showInfo() {
            UserInfo.WaysToFindMe appWaysToFindMe = SharedPreferencesAccessor.UserProfilePref.getAppWaysToFindMe(requireContext());
            if(appWaysToFindMe == null){
                preferenceChangeCanFindMeByImessageId.setEnabled(false);
                preferenceChangeCanFindMeByEmail.setEnabled(false);
            }else {
                preferenceChangeCanFindMeByImessageId.setChecked(appWaysToFindMe.isByImessageIdUser());
                preferenceChangeCanFindMeByEmail.setChecked(appWaysToFindMe.isByEmail());
            }
        }

        @Override
        protected void setupYiers() {
            preferenceChangeCanFindMeByImessageId.setOnPreferenceChangeListener(this);
            preferenceChangeCanFindMeByEmail.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            boolean findMeByImessageId = preferenceChangeCanFindMeByImessageId.isChecked();
            boolean findMeByEmail = preferenceChangeCanFindMeByEmail.isChecked();
            if(preference.equals(preferenceChangeCanFindMeByImessageId)){
                findMeByImessageId = (boolean) newValue;
            }else if(preference.equals(preferenceChangeCanFindMeByEmail)){
                findMeByEmail = (boolean) newValue;
            }
            SharedPreferencesAccessor.UserProfilePref.saveAppWaysToFindMe(requireContext(),
                    new UserInfo.WaysToFindMe(findMeByImessageId, findMeByEmail));
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
            Context applicationContext = requireContext().getApplicationContext();
            if(!preferenceChangeCanFindMeByImessageId.isEnabled()) return;
            if(!preferenceChangeCanFindMeByEmail.isEnabled()) return;
            boolean findMeByImessageIdChecked = preferenceChangeCanFindMeByImessageId.isChecked();
            boolean findMeByEmailChecked = preferenceChangeCanFindMeByEmail.isChecked();
            UserInfo.WaysToFindMe serverWaysToFindMe = SharedPreferencesAccessor.UserProfilePref.getServerWaysToFindMe(applicationContext);
            if(serverWaysToFindMe != null && serverWaysToFindMe.isByImessageIdUser() == findMeByImessageIdChecked && serverWaysToFindMe.isByEmail() == findMeByEmailChecked){
                return;
            }
            FragmentActivity fragmentActivity = requireActivity();
            ChangeWaysToFindMePostBody postBody = new ChangeWaysToFindMePostBody(findMeByImessageIdChecked, findMeByEmailChecked);
            PermissionApiCaller.changeWaysToFindMe(null, postBody, new RetrofitApiCaller.BaseCommonYier<OperationStatus>(applicationContext){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(fragmentActivity, new int[]{}, () -> {
                        SharedPreferencesAccessor.UserProfilePref.saveServerWaysToFindMe(applicationContext,
                                new UserInfo.WaysToFindMe(findMeByImessageIdChecked, findMeByEmailChecked));
                        SharedPreferencesAccessor.UserProfilePref.saveAppWaysToFindMe(applicationContext,
                                new UserInfo.WaysToFindMe(findMeByImessageIdChecked, findMeByEmailChecked));
                    });
                }

                @Override
                public void notOk(int code, String message, Response<OperationStatus> row, Call<OperationStatus> call) {
                    super.notOk(code, message, row, call);
                    if (serverWaysToFindMe != null) {
                        SharedPreferencesAccessor.UserProfilePref.saveAppWaysToFindMe(applicationContext,
                                new UserInfo.WaysToFindMe(serverWaysToFindMe.isByImessageIdUser(), serverWaysToFindMe.isByEmail()));
                    }
                }

                @Override
                public void failure(Throwable t, Call<OperationStatus> call) {
                    super.failure(t, call);
                    if (serverWaysToFindMe != null) {
                        SharedPreferencesAccessor.UserProfilePref.saveAppWaysToFindMe(applicationContext,
                                new UserInfo.WaysToFindMe(serverWaysToFindMe.isByImessageIdUser(), serverWaysToFindMe.isByEmail()));
                    }
                }
            });
        }
    }
}