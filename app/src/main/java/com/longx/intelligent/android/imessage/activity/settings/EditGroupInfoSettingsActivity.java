package com.longx.intelligent.android.imessage.activity.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.editgroup.ChangeGroupNameActivity;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.ActivityEditGroupInfoSettingsBinding;
import com.longx.intelligent.android.imessage.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.imessage.preference.ChangeAvatarPreference;
import com.longx.intelligent.android.imessage.preference.ProfileItemPreference;

public class EditGroupInfoSettingsActivity extends BaseSettingsActivity{
    private ActivityEditGroupInfoSettingsBinding binding;
    private GroupChannel groupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditGroupInfoSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intentData();
        setupDefaultBackNavigation(binding.toolbar);
        setupPreferenceFragment(savedInstanceState);
    }

    private void intentData() {
        groupChannel = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL);
    }

    private void setupPreferenceFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.settings.getId(), new SettingsFragment(groupChannel))
                    .commit();
        }
    }

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
        private ChangeAvatarPreference preferenceChangeAvatar;
        private ProfileItemPreference preferenceChangeGroupIdUser;
        private ProfileItemPreference preferenceChangeGroupName;
        private ProfileItemPreference preferenceChangeRegion;
        private GroupChannel groupChannel;

        public SettingsFragment(GroupChannel groupChannel) {
            this.groupChannel = groupChannel;
        }

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_edit_group, rootKey);
            doDefaultActions();
        }

        @Override
        protected void bindPreferences() {
            preferenceChangeAvatar = findPreference(getString(R.string.preference_key_change_avatar));
            preferenceChangeGroupIdUser = findPreference(getString(R.string.preference_key_change_group_id_user));
            preferenceChangeGroupName = findPreference(getString(R.string.preference_key_change_group_name));
            preferenceChangeRegion = findPreference(getString(R.string.preference_key_change_region));
        }

        @Override
        protected void showInfo() {
            String doNotSet = getString(R.string.do_not_set);
            String groupChannelIdUser = groupChannel.getGroupChannelIdUser();
            preferenceChangeGroupIdUser.setTitle(groupChannelIdUser == null ? doNotSet : groupChannelIdUser);
            String name = groupChannel.getName();
            preferenceChangeGroupName.setTitle(name == null ? doNotSet : name);
            String regionDesc = groupChannel.buildRegionDesc();
            preferenceChangeRegion.setTitle(regionDesc == null ? doNotSet : regionDesc);
        }

        @Override
        protected void setupYiers() {
            preferenceChangeAvatar.setOnPreferenceClickListener(this);
            preferenceChangeGroupIdUser.setOnPreferenceClickListener(this);
            preferenceChangeGroupName.setOnPreferenceClickListener(this);
            preferenceChangeRegion.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if(preference.equals(preferenceChangeAvatar)){

            }else if(preference.equals(preferenceChangeGroupIdUser)){

            }else if(preference.equals(preferenceChangeGroupName)){
                Intent intent = new Intent(requireContext(), ChangeGroupNameActivity.class);
                intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                startActivity(intent);
            }else if(preference.equals(preferenceChangeRegion)){

            }
            return true;
        }
    }
}