package com.longx.intelligent.android.ichat2.bottomsheet;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.edituser.ChangeAvatarActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.BottomSheetEditAvatarBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.UserApiCaller;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/4/16 at 11:40 AM.
 */
public class EditAvatarBottomSheet extends AbstractBottomSheet {
    private BottomSheetEditAvatarBinding bottomSheetEditAvatarBinding;
    private final View.OnClickListener onClickSetAvatarYier;
    private final View.OnClickListener onClickRemoveAvatarYier;

    public EditAvatarBottomSheet(AppCompatActivity activity, View.OnClickListener onClickSetAvatarYier, View.OnClickListener onClickRemoveAvatarYier) {
        super(activity);
        this.onClickSetAvatarYier = onClickSetAvatarYier;
        this.onClickRemoveAvatarYier = onClickRemoveAvatarYier;
    }

    @Override
    protected void onCreate() {
        bottomSheetEditAvatarBinding = BottomSheetEditAvatarBinding.inflate(getActivity().getLayoutInflater());
        Self currentUserInfo = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(getActivity());
        if(currentUserInfo.getAvatar() == null || currentUserInfo.getAvatar().getHash() == null){
            bottomSheetEditAvatarBinding.removeAvatar.setVisibility(View.GONE);
        }
        setContentView(bottomSheetEditAvatarBinding.getRoot());
        setupListeners();
    }

    private void setupListeners() {
        bottomSheetEditAvatarBinding.setAvatar.setOnClickListener(v -> {
            onClickSetAvatarYier.onClick(v);
            dismiss();
        });
        bottomSheetEditAvatarBinding.removeAvatar.setOnClickListener(v -> {
            onClickRemoveAvatarYier.onClick(v);
            dismiss();
        });
    }
}
