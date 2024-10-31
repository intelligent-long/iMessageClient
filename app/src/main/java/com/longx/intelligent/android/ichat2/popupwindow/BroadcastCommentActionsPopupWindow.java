package com.longx.intelligent.android.ichat2.popupwindow;

import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.BroadcastComment;
import com.longx.intelligent.android.ichat2.databinding.PopupWindowBroadcastCommentActionsBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.dialog.CopyTextDialog;
import com.longx.intelligent.android.ichat2.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;

/**
 * Created by LONG on 2024/5/17 at 9:23 AM.
 */
public class BroadcastCommentActionsPopupWindow {
    private final AppCompatActivity activity;
    private final BroadcastComment broadcastComment;
    private final PopupWindow popupWindow;
    private final PopupWindowBroadcastCommentActionsBinding binding;
    private final int HEIGHT_DP = 86;
    private View.OnClickListener deleteYier;
    private View.OnClickListener viewToCommentYier;

    public BroadcastCommentActionsPopupWindow(AppCompatActivity activity, BroadcastComment broadcastComment) {
        this.activity = activity;
        this.broadcastComment = broadcastComment;
        binding = PopupWindowBroadcastCommentActionsBinding.inflate(activity.getLayoutInflater());
        if(broadcastComment.getToCommentId() == null){
            binding.clickViewViewToComment.setVisibility(View.GONE);
        }
        if(!broadcastComment.getFromId().equals(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity).getIchatId())){
            binding.clickViewDelete.setVisibility(View.GONE);
        }
        popupWindow = new PopupWindow(binding.getRoot(),  ViewGroup.LayoutParams.WRAP_CONTENT,  UiUtil.dpToPx(activity, HEIGHT_DP), true);
        setupYiers();
    }

    private void setupYiers() {
        binding.clickViewTime.setOnClickListener(v -> {
            popupWindow.dismiss();
            String timeText = TimeUtil.formatDetailedRelativeTime(broadcastComment.getCommentTime());
            new CustomViewMessageDialog(activity, timeText).create().show();
        });
        binding.clickViewCopy.setOnClickListener(v -> {
            popupWindow.dismiss();
            new CopyTextDialog(activity, broadcastComment.getText()).create().show();
        });
        binding.clickViewDelete.setOnClickListener(v -> {
            if(deleteYier == null) return;
            new ConfirmDialog(activity)
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        popupWindow.dismiss();
                        deleteYier.onClick(v);
                    })
                    .create().show();
        });
        binding.clickViewViewToComment.setOnClickListener(v -> {
            if(viewToCommentYier == null) return;
            popupWindow.dismiss();
            viewToCommentYier.onClick(v);
        });
    }

    public void show(View anchorView) {
        int yOffset = -anchorView.getHeight() - UiUtil.dpToPx(activity, HEIGHT_DP);
        int screenWidth = activity.getWindow().getDecorView().getWidth();
        binding.getRoot().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = binding.getRoot().getMeasuredWidth();
        int xOffset = (screenWidth - popupWidth) / 2;
        popupWindow.showAsDropDown(anchorView, xOffset, yOffset);
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public BroadcastCommentActionsPopupWindow setDeleteYier(View.OnClickListener deleteYier) {
        this.deleteYier = deleteYier;
        return this;
    }

    public BroadcastCommentActionsPopupWindow setViewToCommentYier(View.OnClickListener viewToCommentYier) {
        this.viewToCommentYier = viewToCommentYier;
        return this;
    }
}
