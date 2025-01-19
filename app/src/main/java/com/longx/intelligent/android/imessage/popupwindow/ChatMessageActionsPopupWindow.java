package com.longx.intelligent.android.imessage.popupwindow;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.ForwardMessageActivity;
import com.longx.intelligent.android.imessage.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.imessage.da.privatefile.PrivateFilesAccessor;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.ChatMessage;
import com.longx.intelligent.android.imessage.data.OpenedChat;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.databinding.PopupWindowChatMessageActionsBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.CopyTextDialog;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.imessage.yier.OpenedChatsUpdateYier;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/5/17 at 9:23 AM.
 */
public class ChatMessageActionsPopupWindow {
    private final AppCompatActivity activity;
    private final ChatMessage chatMessage;
    private final PopupWindow popupWindow;
    private final PopupWindowChatMessageActionsBinding binding;
    private final int HEIGHT_DP = 86;
    private OnDeletedYier onDeletedYier;
    private OnMessageUnsentYier onMessageUnsentYier;

    public ChatMessageActionsPopupWindow(AppCompatActivity activity, ChatMessage chatMessage) {
        this.activity = activity;
        this.chatMessage = chatMessage;
        binding = PopupWindowChatMessageActionsBinding.inflate(activity.getLayoutInflater());
        switch (chatMessage.getType()){
            case ChatMessage.TYPE_IMAGE:
            case ChatMessage.TYPE_FILE:
            case ChatMessage.TYPE_VIDEO:
            case ChatMessage.TYPE_VOICE: {
                binding.clickViewCopy.setVisibility(View.GONE);
                break;
            }
        }
        if(!chatMessage.getFrom().equals(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity).getIchatId())){
            binding.clickViewDelete.setVisibility(View.GONE);
            binding.clickViewUnsend.setVisibility(View.GONE);
        }
        if(TimeUtil.isDateAfter(chatMessage.getTime().getTime(), new Date().getTime(), Constants.MAX_ALLOW_UNSEND_MINUTES * 60 * 1000)){
            binding.clickViewUnsend.setVisibility(View.GONE);
        }
        popupWindow = new PopupWindow(binding.getRoot(),  ViewGroup.LayoutParams.WRAP_CONTENT,  UiUtil.dpToPx(activity, HEIGHT_DP), true);
        setupYiers();
    }

    private void setupYiers() {
        binding.clickViewTime.setOnClickListener(v -> {
            popupWindow.dismiss();
            String timeText = TimeUtil.formatDetailedRelativeTime(chatMessage.getTime());
            new CustomViewMessageDialog(activity, timeText).create().show();
        });
        binding.clickViewCopy.setOnClickListener(v -> {
            popupWindow.dismiss();
            new CopyTextDialog(activity, chatMessage.getText()).create().show();
        });
        binding.clickViewDelete.setOnClickListener(v -> {
            new ConfirmDialog(activity, "是否继续？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        popupWindow.dismiss();
                        String other = chatMessage.getOther(activity);
                        ChatMessageDatabaseManager databaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(activity, other);
                        databaseManager.delete(chatMessage.getUuid());
                        switch (chatMessage.getType()){
                            case ChatMessage.TYPE_IMAGE:{
                                PrivateFilesAccessor.ChatImage.delete(chatMessage);
                                break;
                            }
                            case ChatMessage.TYPE_FILE:{
                                PrivateFilesAccessor.ChatFile.delete(chatMessage);
                                break;
                            }
                            case ChatMessage.TYPE_VIDEO:{
                                PrivateFilesAccessor.ChatVideo.delete(chatMessage);
                                break;
                            }
                            case ChatMessage.TYPE_VOICE:{
                                PrivateFilesAccessor.ChatVoice.delete(chatMessage);
                                break;
                            }
                        }
                        onDeletedYier.onDeleted();
                    })
                    .create().show();
        });
        binding.clickViewForward.setOnClickListener(v -> {
            popupWindow.dismiss();
            Intent intent = new Intent(activity, ForwardMessageActivity.class);
            intent.putExtra(ExtraKeys.CHAT_MESSAGE, chatMessage);
            activity.startActivity(intent);
        });
        binding.clickViewUnsend.setOnClickListener(v -> {
            new ConfirmDialog(activity, "是否继续？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        popupWindow.dismiss();
                        ChatApiCaller.unsendChatMessage(activity, chatMessage.getTo(), chatMessage.getUuid(), new RetrofitApiCaller.CommonYier<OperationData>(activity){
                            @Override
                            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                                super.ok(data, raw, call);
                                data.commonHandleResult(activity, new int[]{-101, -102}, () -> {
                                    ChatMessage unsendChatMessage = data.getData(ChatMessage.class);
                                    unsendChatMessage.setViewed(true);
                                    ChatMessage toUnsendMessage = ChatMessageDatabaseManager.getInstanceOrInitAndGet(activity, unsendChatMessage.getTo()).findOne(unsendChatMessage.getUnsendMessageUuid());
                                    ChatMessage.mainDoOnNewChatMessage(unsendChatMessage, activity, results -> {
                                        onMessageUnsentYier.onUnsent(unsendChatMessage, toUnsendMessage);
                                        OpenedChatDatabaseManager.getInstance().insertOrUpdate(new OpenedChat(unsendChatMessage.getTo(), 0, true));
                                        GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                                            openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
                                        });
                                        GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                                            newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                                                newContentBadgeDisplayYier.autoShowNewContentBadge(activity, NewContentBadgeDisplayYier.ID.MESSAGES);
                                            });
                                        });
                                    });
                                });
                            }
                        });
                    })
                    .create().show();
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

    public void setOnDeletedYier(OnDeletedYier onDeletedYier) {
        this.onDeletedYier = onDeletedYier;
    }

    public void setOnMessageUnsentYier(OnMessageUnsentYier onMessageUnsentYier) {
        this.onMessageUnsentYier = onMessageUnsentYier;
    }

    public interface OnDeletedYier{
        void onDeleted();
    }

    public interface OnMessageUnsentYier{
        void onUnsent(ChatMessage unsendChatMessage, ChatMessage toUnsendMessage);
    }
}
