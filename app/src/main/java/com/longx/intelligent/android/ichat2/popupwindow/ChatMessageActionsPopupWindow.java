package com.longx.intelligent.android.ichat2.popupwindow;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.databinding.PopupWindowChatMessageActionsBinding;
import com.longx.intelligent.android.ichat2.dialog.MessageDialog;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;

/**
 * Created by LONG on 2024/5/17 at 9:23 AM.
 */
public class ChatMessageActionsPopupWindow {
    private final AppCompatActivity activity;
    private final ChatMessage chatMessage;
    private final PopupWindow popupWindow;
    private final PopupWindowChatMessageActionsBinding binding;
    private final int HEIGHT_DP = 86;

    public ChatMessageActionsPopupWindow(AppCompatActivity activity, ChatMessage chatMessage) {
        this.activity = activity;
        this.chatMessage = chatMessage;
        binding = PopupWindowChatMessageActionsBinding.inflate(activity.getLayoutInflater());
        switch (chatMessage.getType()){
            case ChatMessage.TYPE_IMAGE:{
                binding.clickViewCopy.setVisibility(View.GONE);
                break;
            }
        }
        popupWindow = new PopupWindow(binding.getRoot(),  ViewGroup.LayoutParams.WRAP_CONTENT,  UiUtil.dpToPx(activity, HEIGHT_DP), true);
        setupYiers();
    }

    private void setupYiers() {
        binding.clickViewTime.setOnClickListener(v -> {
            popupWindow.dismiss();
            String timeText = TimeUtil.formatRelativeTime(chatMessage.getTime());
            new MessageDialog(activity, timeText).show();
        });
    }

    public void show(View anchorView, boolean right) {
        int yOffset = -anchorView.getHeight() - UiUtil.dpToPx(activity, HEIGHT_DP + 5);
        int xOffset;
        if(right){
            xOffset = 0;
        }else {
            xOffset = -UiUtil.dpToPx(activity, 210);
        }
        popupWindow.showAsDropDown(anchorView, xOffset, yOffset);
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }
}
