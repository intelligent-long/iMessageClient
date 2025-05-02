package com.longx.intelligent.android.imessage.bottomsheet;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.databinding.BottomSheetEditAvatarBinding;

/**
 * Created by LONG on 2024/4/16 at 11:40 AM.
 */
public class EditAvatarBottomSheet extends AbstractBottomSheet {
    private BottomSheetEditAvatarBinding bottomSheetEditAvatarBinding;
    private final View.OnClickListener onClickSetAvatarYier;
    private final View.OnClickListener onClickRemoveAvatarYier;
    private final boolean hideRemove;

    public EditAvatarBottomSheet(AppCompatActivity activity, boolean hideRemove, View.OnClickListener onClickSetAvatarYier, View.OnClickListener onClickRemoveAvatarYier) {
        super(activity);
        this.onClickSetAvatarYier = onClickSetAvatarYier;
        this.onClickRemoveAvatarYier = onClickRemoveAvatarYier;
        this.hideRemove = hideRemove;
        create();
    }

    @Override
    protected void onCreate() {
        bottomSheetEditAvatarBinding = BottomSheetEditAvatarBinding.inflate(getActivity().getLayoutInflater());
        if(hideRemove){
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
