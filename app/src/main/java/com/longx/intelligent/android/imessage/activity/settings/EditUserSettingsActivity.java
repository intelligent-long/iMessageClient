package com.longx.intelligent.android.imessage.activity.settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.preference.Preference;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.CropImageActivity;
import com.longx.intelligent.android.imessage.activity.edituser.ChangeEmailActivity;
import com.longx.intelligent.android.imessage.activity.edituser.ChangeImessageIdUserActivity;
import com.longx.intelligent.android.imessage.activity.edituser.ChangeRegionActivity;
import com.longx.intelligent.android.imessage.activity.edituser.ChangeSexActivity;
import com.longx.intelligent.android.imessage.activity.edituser.ChangeUsernameActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.bottomsheet.EditAvatarBottomSheet;
import com.longx.intelligent.android.imessage.da.SharedImageViewModel;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.data.UserInfo;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityEditUserSettingsBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.imessage.preference.ChangeAvatarPreference;
import com.longx.intelligent.android.imessage.preference.ProfileItemPreference;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class EditUserSettingsActivity extends BaseSettingsActivity{
    private ActivityEditUserSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditUserSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupPreferenceFragment(savedInstanceState);
        setupYiers();
    }

    private void setupPreferenceFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.settings.getId(), new SettingsFragment())
                    .commit();
        }
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.more){
                startActivity(new Intent(EditUserSettingsActivity.this, EditUserProfileVisibilitySettingsActivity.class));
            }
            return false;
        });
    }

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener, ContentUpdater.OnServerContentUpdateYier {
        private ChangeAvatarPreference preferenceChangeAvatar;
        private ProfileItemPreference preferenceChangeImessageIdUser;
        private ProfileItemPreference preferenceChangeUsername;
        private ProfileItemPreference preferenceChangeEmail;
        private ProfileItemPreference preferenceChangeSex;
        private ProfileItemPreference preferenceChangeRegion;

        private final ActivityResultLauncher<Intent> imageCropedActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        SharedImageViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext().getApplicationContext()).get(SharedImageViewModel.class);
                        viewModel.getImage().observe(this, croppedBitmap -> {
                            if (croppedBitmap != null) {
                                byte[] croppedImageBytes = Utils.encodeBitmapToBytes(croppedBitmap, Bitmap.CompressFormat.PNG, 100);
                                UserApiCaller.changeAvatar(this, croppedImageBytes, new RetrofitApiCaller.CommonYier<OperationStatus>(requireActivity()){
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
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_edit_user, rootKey);
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
            preferenceChangeImessageIdUser = findPreference(getString(R.string.preference_key_change_group_id_user));
            preferenceChangeUsername = findPreference(getString(R.string.preference_key_change_username));
            preferenceChangeEmail = findPreference(getString(R.string.preference_key_change_email));
            preferenceChangeSex = findPreference(getString(R.string.preference_key_change_sex));
            preferenceChangeRegion = findPreference(getString(R.string.preference_key_change_region));
        }

        @Override
        protected void showInfo() {
            Self self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(requireContext());
            String doNotSet = getString(R.string.do_not_set);
            String imessageIdUser = self.getImessageIdUser();
            String username = self.getUsername();
            String email = self.getEmail();
            Integer sex = self.getSex();
            String sexString = Self.sexValueToString(requireContext(), sex);
            preferenceChangeImessageIdUser.setTitle(imessageIdUser == null ? doNotSet : imessageIdUser);
            preferenceChangeUsername.setTitle(username == null ? doNotSet : username);
            preferenceChangeEmail.setTitle(email == null ? doNotSet : email);
            preferenceChangeSex.setTitle(sexString);
            String regionDesc = self.buildRegionDesc();
            preferenceChangeRegion.setTitle(regionDesc == null ? doNotSet : regionDesc);
            preferenceChangeAvatar.setAvatar(self.getAvatar() == null ? null : self.getAvatar().getHash());
            UserInfo.UserProfileVisibility userProfileVisibility = self.getUserProfileVisibility();
            preferenceChangeEmail.setProfileVisibility(userProfileVisibility.isEmailVisible());
            preferenceChangeSex.setProfileVisibility(userProfileVisibility.isSexVisible());
            preferenceChangeRegion.setProfileVisibility(userProfileVisibility.isRegionVisible());
        }

        @Override
        protected void setupYiers() {
            preferenceChangeAvatar.setOnPreferenceClickListener(this);
            preferenceChangeImessageIdUser.setOnPreferenceClickListener(this);
            preferenceChangeUsername.setOnPreferenceClickListener(this);
            preferenceChangeEmail.setOnPreferenceClickListener(this);
            preferenceChangeSex.setOnPreferenceClickListener(this);
            preferenceChangeRegion.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if(preference.equals(preferenceChangeAvatar)){
                new EditAvatarBottomSheet((AppCompatActivity) getActivity(), v -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    imageChosenActivityResultLauncher.launch(intent);
                }, v -> {
                    new ConfirmDialog(getActivity(), "是否继续？")
                            .setNegativeButton()
                            .setPositiveButton((dialog, which) -> {
                                UserApiCaller.removeAvatar(getActivity(), new RetrofitApiCaller.CommonYier<OperationStatus>((AppCompatActivity) getActivity()) {
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
            }else if(preference.equals(preferenceChangeImessageIdUser)){
                startActivity(new Intent(getContext(), ChangeImessageIdUserActivity.class));
            }else if(preference.equals(preferenceChangeUsername)){
                startActivity(new Intent(getContext(), ChangeUsernameActivity.class));
            }else if(preference.equals(preferenceChangeEmail)){
                startActivity(new Intent(getContext(), ChangeEmailActivity.class));
            }else if(preference.equals(preferenceChangeSex)){
                startActivity(new Intent(getContext(), ChangeSexActivity.class));
            }else if(preference.equals(preferenceChangeRegion)){
                startActivity(new Intent(getContext(), ChangeRegionActivity.class));
            }
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
    }
}