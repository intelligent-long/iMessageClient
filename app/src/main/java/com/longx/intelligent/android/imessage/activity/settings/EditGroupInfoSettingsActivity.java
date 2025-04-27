package com.longx.intelligent.android.imessage.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.editgroup.ChangeGroupIdActivity;
import com.longx.intelligent.android.imessage.activity.editgroup.ChangeGroupNameActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.ActivityEditGroupInfoSettingsBinding;
import com.longx.intelligent.android.imessage.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.imessage.preference.ChangeAvatarPreference;
import com.longx.intelligent.android.imessage.preference.ProfileItemPreference;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.List;

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
        String groupChannelId = getIntent().getStringExtra(ExtraKeys.GROUP_CHANNEL_ID);
        groupChannel = GroupChannelDatabaseManager.getInstance().findOneAssociation(groupChannelId);
    }

    private void setupPreferenceFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.settings.getId(), SettingsFragment.newInstance(groupChannel))
                    .commit();
        }
    }

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements ContentUpdater.OnServerContentUpdateYier, Preference.OnPreferenceClickListener {
        private ChangeAvatarPreference preferenceChangeAvatar;
        private ProfileItemPreference preferenceChangeGroupIdUser;
        private ProfileItemPreference preferenceChangeGroupName;
        private ProfileItemPreference preferenceChangeRegion;
        private GroupChannel groupChannel;
        private String doNotSet;

        public static SettingsFragment newInstance(GroupChannel groupChannel) {
            SettingsFragment settingsFragment = new SettingsFragment();
            Bundle args = new Bundle();
            args.putParcelable(ExtraKeys.GROUP_CHANNEL, groupChannel);
            settingsFragment.setArguments(args);
            return settingsFragment;
        }

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            doNotSet = getString(R.string.do_not_set);
            groupChannel = getArguments() != null ? getArguments().getParcelable(ExtraKeys.GROUP_CHANNEL) : null;
            setPreferencesFromResource(R.xml.preferences_edit_group, rootKey);
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
            preferenceChangeAvatar = findPreference(getString(R.string.preference_key_change_avatar));
            preferenceChangeGroupIdUser = findPreference(getString(R.string.preference_key_change_group_id_user));
            preferenceChangeGroupName = findPreference(getString(R.string.preference_key_change_group_name));
            preferenceChangeRegion = findPreference(getString(R.string.preference_key_change_region));
        }

        @Override
        protected void showInfo() {
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
                Intent intent = new Intent(requireContext(), ChangeGroupIdActivity.class);
                intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                startActivity(intent);
            }else if(preference.equals(preferenceChangeGroupName)){
                Intent intent = new Intent(requireContext(), ChangeGroupNameActivity.class);
                intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                startActivity(intent);
            }else if(preference.equals(preferenceChangeRegion)){

            }
            return true;
        }

        @Override
        public void onStartUpdate(String id, List<String> updatingIds) {

        }

        @Override
        public void onUpdateComplete(String id, List<String> updatingIds) {
            if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNEL)){
                groupChannel = GroupChannelDatabaseManager.getInstance().findOneAssociation(groupChannel.getGroupChannelId());
                preferenceChangeGroupName.setTitle(groupChannel.getName() == null ? doNotSet : groupChannel.getName());
                preferenceChangeGroupIdUser.setTitle(groupChannel.getGroupChannelIdUser() == null ? doNotSet : groupChannel.getGroupChannelIdUser());
                preferenceChangeRegion.setTitle(groupChannel.buildRegionDesc() == null ? doNotSet : groupChannel.buildRegionDesc());
            }
        }
    }
}