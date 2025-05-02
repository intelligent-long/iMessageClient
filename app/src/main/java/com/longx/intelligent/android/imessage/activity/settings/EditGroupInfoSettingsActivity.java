package com.longx.intelligent.android.imessage.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.preference.Preference;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.CropImageActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.editgroup.ChangeGroupIdActivity;
import com.longx.intelligent.android.imessage.activity.editgroup.ChangeGroupNameActivity;
import com.longx.intelligent.android.imessage.activity.editgroup.ChangeGroupRegionActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.bottomsheet.EditAvatarBottomSheet;
import com.longx.intelligent.android.imessage.da.SharedImageViewModel;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityEditGroupInfoSettingsBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.preference.ChangeGroupAvatarPreference;
import com.longx.intelligent.android.imessage.preference.ProfileItemPreference;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class EditGroupInfoSettingsActivity extends BaseSettingsActivity {
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
        private ChangeGroupAvatarPreference preferenceChangeGroupAvatar;
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
            preferenceChangeGroupAvatar = findPreference(getString(R.string.preference_key_change_avatar));
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
            preferenceChangeGroupAvatar.setAvatar(groupChannel.getGroupAvatar() == null ? null : groupChannel.getGroupAvatar().getHash());
        }

        @Override
        protected void setupYiers() {
            preferenceChangeGroupAvatar.setOnPreferenceClickListener(this);
            preferenceChangeGroupIdUser.setOnPreferenceClickListener(this);
            preferenceChangeGroupName.setOnPreferenceClickListener(this);
            preferenceChangeRegion.setOnPreferenceClickListener(this);
        }

        private final ActivityResultLauncher<Intent> imageCropedActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        SharedImageViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext().getApplicationContext()).get(SharedImageViewModel.class);
                        viewModel.getImage().observe(this, croppedBitmap -> {
                            if (croppedBitmap != null) {
                                byte[] croppedImageBytes = Utils.encodeBitmapToBytes(croppedBitmap, Bitmap.CompressFormat.PNG, 100);
                                GroupChannelApiCaller.changeGroupChannelAvatar(this, croppedImageBytes, groupChannel.getGroupChannelId(), new RetrofitApiCaller.CommonYier<OperationStatus>(requireActivity()){
                                    @Override
                                    public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                        super.ok(data, raw, call);
                                        data.commonHandleResult(requireActivity(), new int[]{-101, -102}, () -> {
                                            new MessageDialog(requireActivity(), "修改成功")
                                                    .create().show();
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
        );

        private final ActivityResultLauncher<Intent> imageChosenActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = new Intent(getActivity(), CropImageActivity.class);
                        intent.putExtra(ExtraKeys.URI, result.getData().getData().toString());
                        imageCropedActivityResultLauncher.launch(intent);
                    }
                }
        );

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if(preference.equals(preferenceChangeGroupAvatar)){
                new EditAvatarBottomSheet((AppCompatActivity) getActivity(), groupChannel == null || groupChannel.getAvatarHash() == null, v -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    imageChosenActivityResultLauncher.launch(intent);
                }, v -> {
                    new ConfirmDialog(getActivity(), "是否继续？")
                            .setNegativeButton()
                            .setPositiveButton((dialog, which) -> {
                                GroupChannelApiCaller.removeGroupChannelAvatar(this, groupChannel.getGroupChannelId(), new RetrofitApiCaller.CommonYier<OperationStatus>(getActivity()) {
                                    @Override
                                    public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                        super.ok(data, raw, call);
                                        data.commonHandleResult(getActivity(), new int[]{}, () -> {
                                            MessageDisplayer.autoShow(getActivity(), "头像已移除", MessageDisplayer.Duration.SHORT);
                                        });
                                    }
                                });
                            }).create().show();
                }).show();
            }else if(preference.equals(preferenceChangeGroupIdUser)){
                Intent intent = new Intent(requireContext(), ChangeGroupIdActivity.class);
                intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                startActivity(intent);
            }else if(preference.equals(preferenceChangeGroupName)){
                Intent intent = new Intent(requireContext(), ChangeGroupNameActivity.class);
                intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                startActivity(intent);
            }else if(preference.equals(preferenceChangeRegion)){
                Intent intent = new Intent(requireContext(), ChangeGroupRegionActivity.class);
                intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                startActivity(intent);
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
                preferenceChangeGroupAvatar.setAvatar(groupChannel.getGroupAvatar() == null ? null : groupChannel.getGroupAvatar().getHash());
            }
        }
    }
}