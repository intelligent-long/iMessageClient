package com.longx.intelligent.android.imessage.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.ChatMessagesRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.behaviorcomponents.VoiceChatMessageBehaviours;
import com.longx.intelligent.android.imessage.da.FileHelper;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChannelAssociation;
import com.longx.intelligent.android.imessage.data.ChatMessage;
import com.longx.intelligent.android.imessage.data.ChatMessageAllow;
import com.longx.intelligent.android.imessage.data.MessageViewed;
import com.longx.intelligent.android.imessage.data.OpenedChat;
import com.longx.intelligent.android.imessage.data.request.SendFileChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.request.SendImageChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.request.SendTextChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.request.SendVideoChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.databinding.ActivityChatBinding;
import com.longx.intelligent.android.imessage.media.MediaType;
import com.longx.intelligent.android.imessage.media.data.MediaInfo;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.FileUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.yier.ChatMessagesUpdateYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.KeyboardVisibilityYier;
import com.longx.intelligent.android.imessage.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.imessage.yier.OpenedChatsUpdateYier;
import com.longx.intelligent.android.imessage.yier.TextChangedYier;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Response;

public class ChatActivity extends BaseActivity implements ChatMessagesUpdateYier {
    private ActivityChatBinding binding;
    private Channel channel;
    private ChatMessagesRecyclerAdapter adapter;
    private ChatMessageDatabaseManager chatMessageDatabaseManager;
    private OpenedChatDatabaseManager openedChatDatabaseManager;
    private static final int PS = 50;
    private int previousPn = 0;
    private int nextPn = -1;
    private boolean reachStart;
    private int initialChatMessageCount;
    private boolean showMorePanelOnKeyboardClosed;
    private Runnable showMessagePopupOnKeyboardClosed;
    private boolean sendingState;
    private boolean cancelSendVoice;
    private ActivityResultLauncher<Intent> sendImageMessageResultLauncher;
    private ActivityResultLauncher<Intent> sendFileMessageResultLauncher;
    private ActivityResultLauncher<Intent> sendVideoMessageResultLauncher;
    private boolean isFabScaledUp;
    private VoiceChatMessageBehaviours voiceChatMessageBehaviours;
    private ChannelDatabaseManager channelDatabaseManager;
    private ChatMessage locateChatMessage;
    private final List<ChatMessage> viewedNewMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFontThemes(R.style.ChatActivity_Font1, R.style.ChatActivity_Font2);
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setAutoCancelInput(false);
        setupBackNavigation(binding.toolbar, ColorUtil.getColor(this, R.color.imessage));
        intentData();
        GlobalYiersHolder.holdYier(this, ChatMessagesUpdateYier.class, this);
        init();
        checkAndLocateMessage();
        showContent();
        checkAndIndicateMessageLocation();
        setupYiers();
        registerResultLauncher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ChatMessagesUpdateYier.class, this);
        if(adapter != null) adapter.onActivityDestroy();
    }

    private void intentData(){
        channel = Objects.requireNonNull(getIntent().getParcelableExtra(ExtraKeys.CHANNEL));
        locateChatMessage = getIntent().getParcelableExtra(ExtraKeys.CHAT_MESSAGE);
    }

    private void init(){
        changeHoldToTalkToNormal();
        changeCancelSendTalkFabToNormal();
        voiceChatMessageBehaviours = new VoiceChatMessageBehaviours(this);
        chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(ChatActivity.this, channel.getImessageId());
        openedChatDatabaseManager = OpenedChatDatabaseManager.getInstance();
        channelDatabaseManager = ChannelDatabaseManager.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatMessagesRecyclerAdapter(this, binding.recyclerView);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean toShow = true;
        for (OpenedChat openedChat : openedChatDatabaseManager.findAllShow()) {
            if(openedChat.getChannelImessageId().equals(channel.getImessageId())){
                toShow = false;
                break;
            }
        }
        if(toShow) {
            openedChatDatabaseManager.updateShow(channel.getImessageId(), true);
            GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
            });
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(ChatActivity.this, NewContentBadgeDisplayYier.ID.MESSAGES);
                });
            });
        }
    }

    private void checkAndLocateMessage() {
        if(locateChatMessage != null) {
            int selectPosition = chatMessageDatabaseManager.findPosition(locateChatMessage.getUuid());
            if(selectPosition != -1){
                int count = chatMessageDatabaseManager.count();
                previousPn = (count - (selectPosition + 1)) / PS;
                nextPn = previousPn - 1;
                int topRestItems = Math.max(count - (previousPn + 1) * PS, 0);
                int recyclerViewPosition = Math.max(selectPosition - topRestItems, 0);
                LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    layoutManager.scrollToPositionWithOffset(recyclerViewPosition, 500);
                }
            }
        }
    }

    private void showContent(){
        binding.toolbar.setTitle(channel.getNote() == null ? channel.getUsername() : channel.getNote());
        synchronized (this) {
            initialChatMessageCount = chatMessageDatabaseManager.count();
            showChatMessages();
        }
//        if(openedChatDatabaseManager.findNotViewedCount(channel.getImessageId()) > 0) {
//            viewAllNewChatMessages();
//        }
        ChannelAssociation association = null;
        try {
            association = channelDatabaseManager.findOneAssociation(channel.getImessageId());
        }catch (Exception e){
            ErrorLogger.log(e);
        }
        if(association != null) {
            ChatMessageAllow chatMessageAllow = association.getChatMessageAllowToThem();
            if (!chatMessageAllow.isAllowVoice()) {
                UiUtil.setViewEnabled(binding.voiceButton, false, true);
            }
        }else {
            binding.voiceButton.setVisibility(View.GONE);
        }
    }

    private void showChatMessages() {
        previousPage();
        if(locateChatMessage == null){
            binding.recyclerView.scrollToEnd(false);
        }
    }

    private void checkAndIndicateMessageLocation() {
        if(locateChatMessage != null){
            adapter.indicateLocation(locateChatMessage.getUuid());
        }
    }

    private void viewNewChatMessages(){
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        List<String> toViewNewMessageIds = new ArrayList<>();
        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
            try {
                if(!adapter.getItemDataList().isEmpty()) {
                    ChatMessage chatMessage = adapter.getItemDataList().get(i).getChatMessage();
                    if (!(chatMessage.isViewed() || chatMessage.isSelfSender(this))) {
                        if (!viewedNewMessages.contains(chatMessage)) {
                            toViewNewMessageIds.add(chatMessage.getUuid());
                            viewedNewMessages.add(chatMessage);
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                ErrorLogger.log(e);
            }
        }
        if(!toViewNewMessageIds.isEmpty()){
            toViewNewMessageIds.forEach(toViewNewMessageId -> {
                ChatApiCaller.viewMessage(null, toViewNewMessageId, new RetrofitApiCaller.BaseCommonYier<OperationData>(this, false){
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ChatActivity.this, new int[]{}, () -> {
                            MessageViewed messageViewed = data.getData(MessageViewed.class);
                            openedChatDatabaseManager.updateNotViewedCount(messageViewed.getNotViewedCount(), messageViewed.getOther());
                            chatMessageDatabaseManager.setOneToViewed(messageViewed.getViewedUuid());
                            GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                                openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
                            });
                            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                                    newContentBadgeDisplayYier.autoShowNewContentBadge(ChatActivity.this, NewContentBadgeDisplayYier.ID.MESSAGES);
                                });
                            });
                        });

                    }
                });
            });
        }
    }

    @Override
    public void onNewChatMessages(List<ChatMessage> newChatMessages) {
        List<ChatMessage> thisChannelNewMessages = new ArrayList<>();
        newChatMessages.forEach(newChatMessage -> {
            if(newChatMessage.getOther(this).equals(channel.getImessageId())){
                thisChannelNewMessages.add(newChatMessage);
            }
        });
        synchronized (this) {
            thisChannelNewMessages.forEach(thisChannelNewMessage -> {
                ChatMessage message = ChatMessageDatabaseManager.getInstanceOrInitAndGet(this, channel.getImessageId()).findOne(thisChannelNewMessage.getUuid());
                if (adapter != null) adapter.addItemAndShow(message);
            });
        }
    }

    @Override
    public void onChatMessagesUpdated(List<ChatMessage> updatedChatMessages) {
        List<ChatMessage> thisChannelUpdatedMessages = new ArrayList<>();
        updatedChatMessages.forEach(updatedChatMessage -> {
            if(updatedChatMessage.getOther(this).equals(channel.getImessageId())){
                thisChannelUpdatedMessages.add(updatedChatMessage);
            }
        });
        synchronized (this){
            thisChannelUpdatedMessages.forEach(thisChannelUpdatedMessage -> {
                if (adapter != null) runOnUiThread(() -> {
                    int updatedIndex = adapter.notifyItemChanged(thisChannelUpdatedMessage);
                    if(updatedIndex == adapter.getItemCount() - 1){
                        binding.recyclerView.scrollToEnd(true);
                    }
                });
            });
        }
    }

    @Override
    public void onUnsendChatMessages(List<ChatMessage> unsendChatMessages, List<ChatMessage> toUnsendChatMessages) {
        List<ChatMessage> thisChannelUnsendMessages = new ArrayList<>();
        unsendChatMessages.forEach(unsendMessage -> {
            if(unsendMessage.getOther(this).equals(channel.getImessageId())){
                thisChannelUnsendMessages.add(unsendMessage);
            }
        });
        synchronized (this) {
            thisChannelUnsendMessages.forEach(thisChannelUnsendMessage -> {
                if (adapter != null) {
                    adapter.addItemAndShow(thisChannelUnsendMessage);
                }
            });
            toUnsendChatMessages.forEach(toUnsendChatMessage -> {
                if (adapter != null) {
                    adapter.removeItemAndShow(toUnsendChatMessage);
                }
            });
        }
    }

    private synchronized void previousPage(){
        if(reachStart) return;
        int startIndex = previousPn * PS;
        int currentChatMessageCount = chatMessageDatabaseManager.count();
        startIndex += (currentChatMessageCount - initialChatMessageCount);
        List<ChatMessage> chatMessages = chatMessageDatabaseManager.findLimit(startIndex, PS, true);
        if (chatMessages.isEmpty()) {
            reachStart = true;
            return;
        }
        adapter.addAllToStartAndShow(chatMessages);
        previousPn ++;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupYiers() {
        for (int i = 0; i < binding.toolbar.getChildCount(); i++) {
            View view = binding.toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView titleTextView = (TextView) view;
                if (titleTextView.getText().equals(binding.toolbar.getTitle())) {
                    titleTextView.setOnClickListener(v -> {
                        Intent intent = new Intent(ChatActivity.this, ChannelActivity.class);
                        intent.putExtra(ExtraKeys.CHANNEL, channel);
                        startActivity(intent);
                    });
                    break;
                }
            }
        }
        binding.recyclerView.addOnApproachEdgeYier(5, new RecyclerView.OnApproachEdgeYier() {
            @Override
            public void onApproachStart() {
                previousPage();
            }

            @Override
            public void onApproachEnd() {

            }
        });
        binding.messageInput.addTextChangedListener(new TextChangedYier(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if(s.length() > 0){
                    binding.layoutSendButtonAndIndicator.setVisibility(View.VISIBLE);
                    binding.moreButton.setVisibility(View.GONE);
                    binding.sendButton.setVisibility(View.VISIBLE);
                }else {
                    binding.layoutSendButtonAndIndicator.setVisibility(View.GONE);
                    binding.moreButton.setVisibility(View.VISIBLE);
                    binding.sendButton.setVisibility(View.GONE);
                }
            }
        });
        new KeyboardVisibilityYier(this).setYier(new KeyboardVisibilityYier.Yier() {

            @Override
            public void onKeyboardOpened() {
                if(!(nextPn < 0)){
                    previousPn = 0;
                    nextPn = -1;
                    adapter.clearAndShow();
                    previousPage();
                }
                binding.recyclerView.scrollToEnd(true);
                hideMorePanel();
            }

            @Override
            public void onKeyboardClosed() {
                if(showMorePanelOnKeyboardClosed) {
                    showMorePanel();
                    showMorePanelOnKeyboardClosed = false;
                }
                if(showMessagePopupOnKeyboardClosed != null){
                    showMessagePopupOnKeyboardClosed.run();
                    showMessagePopupOnKeyboardClosed = null;
                }
                binding.messageInput.clearFocus();
            }
        });
        binding.sendButton.setOnClickListener(v -> {
            String inputtedMessage = UiUtil.getEditTextString(binding.messageInput);
            if(inputtedMessage == null || inputtedMessage.isEmpty()) return;
            SendTextChatMessagePostBody postBody = new SendTextChatMessagePostBody(channel.getImessageId(), inputtedMessage);
            ChatApiCaller.sendTextMessage(this, postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this){
                @Override
                public void start(Call<OperationData> call) {
                    super.start(call);
                    toSendingState();
                }

                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(ChatActivity.this, new int[]{-101}, () -> {
                        binding.messageInput.setText(null);
                        ChatMessage chatMessage = data.getData(ChatMessage.class);
                        chatMessage.setViewed(true);
                        ChatMessage.mainDoOnNewMessage(chatMessage, ChatActivity.this, results -> {
                            adapter.addItemAndShow(chatMessage);
                            OpenedChatDatabaseManager.getInstance().insertOrUpdate(new OpenedChat(chatMessage.getTo(), 0, true));
                            GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                                openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
                            });
                            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                                    newContentBadgeDisplayYier.autoShowNewContentBadge(ChatActivity.this, NewContentBadgeDisplayYier.ID.MESSAGES);
                                });
                            });
                        });
                    });
                }

                @Override
                public void complete(Call<OperationData> call) {
                    super.complete(call);
                    toNormalState();
                }
            });
        });
        binding.voiceButton.setOnClickListener(v -> {
            binding.voiceButton.setVisibility(View.GONE);
            binding.textButton.setVisibility(View.VISIBLE);
            binding.messageInput.setVisibility(View.GONE);
            binding.holdToTalkButton.setVisibility(View.VISIBLE);
            UiUtil.hideKeyboard(binding.messageInput);
            hideMorePanel();
        });
        binding.textButton.setOnClickListener(v -> {
            binding.voiceButton.setVisibility(View.VISIBLE);
            binding.textButton.setVisibility(View.GONE);
            binding.messageInput.setVisibility(View.VISIBLE);
            binding.holdToTalkButton.setVisibility(View.GONE);
            UiUtil.openKeyboard(binding.messageInput);
        });
        binding.moreButton.setOnClickListener(v -> {
            if(new KeyboardVisibilityYier(this).isKeyboardVisible()) {
                UiUtil.hideKeyboard(binding.messageInput);
                showMorePanelOnKeyboardClosed = true;
            }else {
                showMorePanel();
            }
        });
        binding.hideMoreButton.setOnClickListener(v -> {
            hideMorePanel();
        });
        binding.messageInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) hideMorePanel();
        });
        binding.morePanelImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChooseMediasActivity.class);
            intent.putExtra(ExtraKeys.MEDIA_TYPE, MediaType.IMAGE.name());
            intent.putExtra(ExtraKeys.TOOLBAR_TITLE, "发送图片");
            intent.putExtra(ExtraKeys.MENU_TITLE, "发送");
            intent.putExtra(ExtraKeys.RES_ID, R.drawable.send_fill_24px);
            intent.putExtra(ExtraKeys.MAX_ALLOW_IMAGE_COUNT, Constants.MAX_ONCE_SEND_CHAT_MESSAGE_IMAGE_COUNT);
            intent.putExtra(ExtraKeys.MAX_ALLOW_IMAGE_SIZE, Constants.MAX_SEND_CHAT_MESSAGE_IMAGE_SIZE);
            sendImageMessageResultLauncher.launch(intent);
        });
        binding.morePanelTakePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(this, TakePhotoActivity.class);
            intent.putExtra(ExtraKeys.RES_ID, R.drawable.send_fill_24px);
            intent.putExtra(ExtraKeys.MENU_TITLE, "发送");
            sendImageMessageResultLauncher.launch(intent);
        });
        binding.morePanelSendFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent = Intent.createChooser(intent, "选择文件");
            sendFileMessageResultLauncher.launch(intent);
        });
        binding.morePanelVideo.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChooseMediasActivity.class);
            intent.putExtra(ExtraKeys.MEDIA_TYPE, MediaType.VIDEO.name());
            intent.putExtra(ExtraKeys.TOOLBAR_TITLE, "发送视频");
            intent.putExtra(ExtraKeys.RES_ID, R.drawable.send_fill_24px);
            intent.putExtra(ExtraKeys.MENU_TITLE, "发送");
            intent.putExtra(ExtraKeys.MAX_ALLOW_VIDEO_COUNT, Constants.MAX_ONCE_SEND_CHAT_MESSAGE_VIDEO_COUNT);
            intent.putExtra(ExtraKeys.MAX_ALLOW_VIDEO_SIZE, Constants.MAX_SEND_CHAT_MESSAGE_VIDEO_SIZE);
            sendVideoMessageResultLauncher.launch(intent);
        });
        binding.morePanelRecordVideo.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecordVideoActivity.class);
            intent.putExtra(ExtraKeys.RES_ID, R.drawable.send_fill_24px);
            intent.putExtra(ExtraKeys.MENU_TITLE, "发送");
            sendVideoMessageResultLauncher.launch(intent);
        });
        binding.holdToTalkButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cancelSendVoice = false;
                    changeHoldToTalkToHold();
                    voiceChatMessageBehaviours.onStartTalk();
                    break;
                case MotionEvent.ACTION_UP:
                    changeHoldToTalkToNormal();
                    if(cancelSendVoice){
                        voiceChatMessageBehaviours.cancelSendVoice();
                    }else {
                        voiceChatMessageBehaviours.sendVoice();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    synchronized (this) {
                        float x = event.getRawX();
                        float y = event.getRawY();
                        int[] fabXY = {0, 0};
                        binding.cancelSendTalkFab.getLocationOnScreen(fabXY);
                        float fabX = fabXY[0];
                        float fabY = fabXY[1];
                        int fabWidth = binding.cancelSendTalkFab.getWidth();
                        int fabHeight = binding.cancelSendTalkFab.getHeight();
                        boolean isInsideFab = x > fabX + 5 && x < (fabX + fabWidth - 5) && y > fabY + 5 && y < (fabY + fabHeight - 5);
                        boolean isOutsideFab = (x < fabX - 21 || x > (fabX + fabWidth + 21)) || (y < fabY - 21 || y > (fabY + fabHeight + 21));
                        if (isInsideFab) {
                            if (!cancelSendVoice) cancelSendVoice = true;
                            if (!isFabScaledUp) {
                                isFabScaledUp = true;
                                binding.cancelSendTalkFab.post(this::changeCancelSendTalkFabToCancel);
                            }
                        } else if(isOutsideFab) {
                            if (cancelSendVoice) cancelSendVoice = false;
                            if (isFabScaledUp) {
                                isFabScaledUp = false;
                                binding.cancelSendTalkFab.post(this::changeCancelSendTalkFabToNormal);
                            }
                        }
                    }
                    break;
            }
            return false;
        });
        binding.recyclerView.setOnTouchListener((v, event) -> {
            adapter.cancelIndicateLocation();
            return false;
        });
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                viewNewChatMessages();
            }
        });
    }

    public void toSendingState(){
        sendingState = true;
        hideMorePanel();
        binding.voiceButton.setVisibility(View.GONE);
        binding.textButton.setVisibility(View.GONE);
        binding.messageInput.setVisibility(View.GONE);
        binding.messageInput.setText(null);
        binding.holdToTalkButton.setVisibility(View.GONE);
        binding.layoutSendButtonAndIndicator.setVisibility(View.VISIBLE);
        binding.moreButton.setVisibility(View.GONE);
        binding.sendButton.setVisibility(View.GONE);
        binding.sendIndicator.setVisibility(View.VISIBLE);
        binding.sendItemCountIndicator.setVisibility(View.GONE);
        binding.sendProgressIndicator.setVisibility(View.GONE);
    }

    public void toSendingProgressState(){
        sendingState = true;
        hideMorePanel();
        binding.voiceButton.setVisibility(View.GONE);
        binding.textButton.setVisibility(View.GONE);
        binding.messageInput.setVisibility(View.GONE);
        binding.messageInput.setText(null);
        binding.holdToTalkButton.setVisibility(View.GONE);
        binding.layoutSendButtonAndIndicator.setVisibility(View.VISIBLE);
        binding.moreButton.setVisibility(View.GONE);
        binding.sendButton.setVisibility(View.GONE);
        binding.sendIndicator.setVisibility(View.GONE);
        binding.sendItemCountIndicator.setVisibility(View.VISIBLE);
        binding.sendProgressIndicator.setVisibility(View.VISIBLE);
    }

    public void toNormalState(){
        sendingState = false;
        hideMorePanel();
        binding.voiceButton.setVisibility(View.VISIBLE);
        binding.textButton.setVisibility(View.GONE);
        binding.messageInput.setVisibility(View.VISIBLE);
        binding.messageInput.setText(null);
        binding.holdToTalkButton.setVisibility(View.GONE);
        binding.layoutSendButtonAndIndicator.setVisibility(View.GONE);
        binding.moreButton.setVisibility(View.VISIBLE);
        binding.sendButton.setVisibility(View.GONE);
        binding.sendIndicator.setVisibility(View.GONE);
        binding.sendItemCountIndicator.setVisibility(View.GONE);
        binding.sendProgressIndicator.setVisibility(View.GONE);
    }

    public void toVoiceState(){
        sendingState = false;
        hideMorePanel();
        binding.voiceButton.setVisibility(View.GONE);
        binding.textButton.setVisibility(View.VISIBLE);
        binding.messageInput.setVisibility(View.GONE);
        binding.messageInput.setText(null);
        binding.holdToTalkButton.setVisibility(View.VISIBLE);
        binding.layoutSendButtonAndIndicator.setVisibility(View.GONE);
        binding.moreButton.setVisibility(View.VISIBLE);
        binding.sendButton.setVisibility(View.GONE);
        binding.sendIndicator.setVisibility(View.GONE);
        binding.sendItemCountIndicator.setVisibility(View.GONE);
        binding.sendProgressIndicator.setVisibility(View.GONE);
    }

    public void changeHoldToTalkToHold(){
        binding.holdToTalkButton.setBackgroundTintList(ColorStateList.valueOf(ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainerHighest)));
        binding.holdToTalkButton.setText("松开 发送");
        binding.holdToTalkButton.setTextColor(ColorUtil.getAlphaAttrColor(this, com.google.android.material.R.attr.colorControlNormal, 230));
        binding.holdToTalkButton.setIconTint(ColorStateList.valueOf(getColor(R.color.imessage)));
    }

    public void changeHoldToTalkToNormal(){
        binding.holdToTalkButton.setBackgroundTintList(ColorStateList.valueOf(ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer)));
        binding.holdToTalkButton.setText("按住 说话");
        binding.holdToTalkButton.setTextColor(ColorUtil.getAlphaAttrColor(this, com.google.android.material.R.attr.colorControlNormal, 230));
        binding.holdToTalkButton.setIconTint(ColorStateList.valueOf(ColorUtil.getAlphaAttrColor(this, com.google.android.material.R.attr.colorControlNormal, 230)));
    }

    private void changeCancelSendTalkFabToCancel() {
        binding.cancelSendTalkFab.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.negative_red)));
        binding.cancelSendTalkFab.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(() -> isFabScaledUp = true).start();
    }

    public void changeCancelSendTalkFabToNormal() {
        binding.cancelSendTalkFab.setSupportImageTintList(ColorStateList.valueOf(ColorUtil.getAlphaAttrColor(this, com.google.android.material.R.attr.colorControlNormal, 200)));
        binding.cancelSendTalkFab.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> isFabScaledUp = false).start();
    }

    private void showMorePanel(){
        binding.morePanel.postDelayed(() -> binding.recyclerView.scrollToEnd(true), 21);
        binding.morePanel.setVisibility(View.VISIBLE);
        binding.moreButton.setVisibility(View.GONE);
        binding.hideMoreButton.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.bar.getLayoutParams();
        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        binding.bar.setLayoutParams(params);
    }

    private void hideMorePanel(){
        binding.morePanel.setVisibility(View.GONE);
        String messageInputString = UiUtil.getEditTextString(binding.messageInput);
        if(messageInputString != null && !messageInputString.isEmpty()){
            binding.layoutSendButtonAndIndicator.setVisibility(View.VISIBLE);
            binding.moreButton.setVisibility(View.GONE);
            binding.sendButton.setVisibility(View.VISIBLE);
        }else {
            binding.layoutSendButtonAndIndicator.setVisibility(View.GONE);
            binding.moreButton.setVisibility(View.VISIBLE);
            binding.sendButton.setVisibility(View.GONE);
        }
        binding.hideMoreButton.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.bar.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        binding.bar.setLayoutParams(params);
    }

    public Channel getChannel() {
        return channel;
    }

    public ActivityChatBinding getBinding(){
        return binding;
    }

    public ChatMessagesRecyclerAdapter getAdapter() {
        return adapter;
    }

    public void setShowMessagePopupOnKeyboardClosed(Runnable showMessagePopupOnKeyboardClosed) {
        this.showMessagePopupOnKeyboardClosed = showMessagePopupOnKeyboardClosed;
    }

    private void registerResultLauncher() {
        sendImageMessageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        Parcelable[] parcelableArrayExtra = Objects.requireNonNull(data.getParcelableArrayExtra(ExtraKeys.MEDIA_INFOS));
                        List<MediaInfo> mediaInfos = Utils.parseParcelableArray(parcelableArrayExtra);
                        for (MediaInfo mediaInfo : mediaInfos) {
                            long fileSize = FileUtil.getFileSize(this, mediaInfo.getUri());
                            if(fileSize > Constants.MAX_SEND_CHAT_MESSAGE_IMAGE_SIZE){
                                MessageDisplayer.autoShow(this, "发送图片文件最大不能超过 " + FileUtil.formatFileSize(Constants.MAX_SEND_CHAT_MESSAGE_VIDEO_SIZE), MessageDisplayer.Duration.LONG);
                                return;
                            }
                        }
                        if(mediaInfos.size() > Constants.MAX_ONCE_SEND_CHAT_MESSAGE_IMAGE_COUNT){
                            MessageDisplayer.autoShow(this, "最多一次发送 " + Constants.MAX_ONCE_SEND_CHAT_MESSAGE_IMAGE_COUNT + " 张图片", MessageDisplayer.Duration.LONG);
                        }else {
                            onSendImageMessages(mediaInfos);
                        }
                    }
                }
        );
        sendFileMessageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if(data != null){
                            List<Uri> uriList = new ArrayList<>();
                            if (data.getClipData() != null) {
                                int count = data.getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                                    uriList.add(fileUri);
                                }
                            } else if (data.getData() != null) {
                                Uri fileUri = data.getData();
                                uriList.add(fileUri);
                            }
                            for (Uri uri : uriList) {
                                long fileSize = FileUtil.getFileSize(this, uri);
                                if(fileSize > Constants.MAX_SEND_CHAT_MESSAGE_FILE_SIZE){
                                    MessageDisplayer.autoShow(this, "发送文件最大不能超过 " + FileUtil.formatFileSize(Constants.MAX_SEND_CHAT_MESSAGE_FILE_SIZE), MessageDisplayer.Duration.LONG);
                                    return;
                                }
                            }
                            if(uriList.size() > Constants.MAX_ONCE_SEND_CHAT_MESSAGE_FILE_COUNT){
                                MessageDisplayer.autoShow(this, "最多一次发送 " + Constants.MAX_ONCE_SEND_CHAT_MESSAGE_FILE_COUNT + " 个文件", MessageDisplayer.Duration.LONG);
                            }else {
                                onSendFileMessages(uriList);
                            }
                        }
                    }
                }
        );
        sendVideoMessageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Parcelable[] parcelableArrayExtra = Objects.requireNonNull(data.getParcelableArrayExtra(ExtraKeys.MEDIA_INFOS));
                            List<MediaInfo> mediaInfos = Utils.parseParcelableArray(parcelableArrayExtra);
                            for (MediaInfo mediaInfo : mediaInfos) {
                                long fileSize = FileUtil.getFileSize(this, mediaInfo.getUri());
                                if(fileSize > Constants.MAX_SEND_CHAT_MESSAGE_VIDEO_SIZE){
                                    MessageDisplayer.autoShow(this, "发送视频文件最大不能超过 " + FileUtil.formatFileSize(Constants.MAX_SEND_CHAT_MESSAGE_VIDEO_SIZE), MessageDisplayer.Duration.LONG);
                                    return;
                                }
                            }
                            if(mediaInfos.size() > Constants.MAX_ONCE_SEND_CHAT_MESSAGE_VIDEO_COUNT){
                                MessageDisplayer.autoShow(this, "最多一次发送 " + Constants.MAX_ONCE_SEND_CHAT_MESSAGE_VIDEO_COUNT + " 个视频", MessageDisplayer.Duration.LONG);
                            }else {
                                onSendVideoMessages(mediaInfos);
                            }
                        }
                    }
                }
        );
    }

    private void onSendImageMessages(List<MediaInfo> mediaInfos){
        AtomicInteger index = new AtomicInteger();
        sendImageMessages(mediaInfos, index);
    }

    private void sendImageMessages(List<MediaInfo> mediaInfos, AtomicInteger index){
        if(index.get() == mediaInfos.size()) return;
        MediaInfo mediaInfo = mediaInfos.get(index.get());
        String fileName = FileHelper.getFileNameFromUri(this, mediaInfo.getUri());
        SendImageChatMessagePostBody postBody = new SendImageChatMessagePostBody(channel.getImessageId(), fileName);
        ChatApiCaller.sendImageMessage(this, this, mediaInfo.getUri(), postBody, null, new RetrofitApiCaller.BaseCommonYier<OperationData>(this) {
            @Override
            public void start(Call<OperationData> call) {
                super.start(call);
                binding.sendProgressIndicator.setProgress(0, false);
                if(index.get() == 0) {
                    toSendingProgressState();
                }
            }

            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(ChatActivity.this, new int[]{-101, -102}, () -> {
                    ChatMessage chatMessage = data.getData(ChatMessage.class);
                    ChatMessage.mainDoOnNewMessage(chatMessage, ChatActivity.this, results -> {
                        adapter.addItemAndShow(chatMessage);
                        OpenedChatDatabaseManager.getInstance().insertOrUpdate(new OpenedChat(chatMessage.getTo(), 0, true));
                        GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                            openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
                        });
                        GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                            newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                                newContentBadgeDisplayYier.autoShowNewContentBadge(ChatActivity.this, NewContentBadgeDisplayYier.ID.MESSAGES);
                            });
                        });
                    });
                });
                index.incrementAndGet();
                sendImageMessages(mediaInfos, index);
            }

            @Override
            public void notOk(int code, String message, Response<OperationData> row, Call<OperationData> call) {
                super.notOk(code, message, row, call);
                toNormalState();
            }

            @Override
            public void failure(Throwable t, Call<OperationData> call) {
                super.failure(t, call);
                toNormalState();
            }

            @Override
            public void complete(Call<OperationData> call) {
                super.complete(call);
                if(index.get() == mediaInfos.size()){
                    toNormalState();
                }
            }
        }, (current, total) -> {
            runOnUiThread(() -> {
                binding.sendItemCountIndicator.setText(index.get() + 1 + " / " + mediaInfos.size());
                int progress = (int)((current / (double) total) * binding.sendProgressIndicator.getMax());
                binding.sendProgressIndicator.setProgress(progress, true);
            });
        });
    }

    private void onSendFileMessages(List<Uri> uriList) {
        AtomicInteger index = new AtomicInteger();
        sendFileMessages(uriList, index);
    }

    private void sendFileMessages(List<Uri> uriList, AtomicInteger index){
        if(index.get() == uriList.size()) return;
        Uri uri = uriList.get(index.get());
        String fileName = FileHelper.getFileNameFromUri(this, uri);
        SendFileChatMessagePostBody postBody = new SendFileChatMessagePostBody(channel.getImessageId(), fileName);
        ChatApiCaller.sendFileMessage(this, this, uri, postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this) {
            @Override
            public void start(Call<OperationData> call) {
                super.start(call);
                binding.sendProgressIndicator.setProgress(0, false);
                if (index.get() == 0) {
                    toSendingProgressState();
                }
            }

            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(ChatActivity.this, new int[]{-101, -102}, () -> {
                    ChatMessage chatMessage = data.getData(ChatMessage.class);
                    chatMessage.setViewed(true);
                    ChatMessage.mainDoOnNewMessage(chatMessage, ChatActivity.this, results -> {
                        adapter.addItemAndShow(chatMessage);
                        OpenedChatDatabaseManager.getInstance().insertOrUpdate(new OpenedChat(chatMessage.getTo(), 0, true));
                        GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                            openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
                        });
                        GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                            newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                                newContentBadgeDisplayYier.autoShowNewContentBadge(ChatActivity.this, NewContentBadgeDisplayYier.ID.MESSAGES);
                            });
                        });
                    });
                });
                index.incrementAndGet();
                sendFileMessages(uriList, index);
            }

            @Override
            public void notOk(int code, String message, Response<OperationData> row, Call<OperationData> call) {
                super.notOk(code, message, row, call);
                toNormalState();
            }

            @Override
            public void failure(Throwable t, Call<OperationData> call) {
                super.failure(t, call);
                toNormalState();
            }

            @Override
            public void complete(Call<OperationData> call) {
                super.complete(call);
                if (index.get() == uriList.size()) {
                    toNormalState();
                }
            }
        }, (current, total) -> {
            runOnUiThread(() -> {
                binding.sendItemCountIndicator.setText(index.get() + 1 + " / " + uriList.size());
                int progress = (int)((current / (double) total) * binding.sendProgressIndicator.getMax());
                binding.sendProgressIndicator.setProgress(progress, true);
            });
        });
    }

    private void onSendVideoMessages(List<MediaInfo> mediaInfos){
        AtomicInteger index = new AtomicInteger();
        sendVideoMessages(mediaInfos, index);
    }

    private void sendVideoMessages(List<MediaInfo> mediaInfos, AtomicInteger index){
        if(index.get() == mediaInfos.size()) return;
        MediaInfo mediaInfo = mediaInfos.get(index.get());
        String fileName = FileHelper.getFileNameFromUri(this, mediaInfo.getUri());
        SendVideoChatMessagePostBody postBody = new SendVideoChatMessagePostBody(channel.getImessageId(), fileName);
        ChatApiCaller.sendVideoMessage(this, this, mediaInfo.getUri(), postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this){
            @Override
            public void start(Call<OperationData> call) {
                super.start(call);
                binding.sendProgressIndicator.setProgress(0, false);
                if (index.get() == 0) {
                    toSendingProgressState();
                }
            }

            @Override
            public void notOk(int code, String message, Response<OperationData> row, Call<OperationData> call) {
                super.notOk(code, message, row, call);
                toNormalState();
            }

            @Override
            public void failure(Throwable t, Call<OperationData> call) {
                super.failure(t, call);
                toNormalState();
            }

            @Override
            public void complete(Call<OperationData> call) {
                super.complete(call);
                if (index.get() == mediaInfos.size()) {
                    toNormalState();
                }
            }

            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(ChatActivity.this, new int[]{-101, -102}, () -> {
                    ChatMessage chatMessage = data.getData(ChatMessage.class);
                    chatMessage.setViewed(true);
                    ChatMessage.mainDoOnNewMessage(chatMessage, ChatActivity.this, results -> {
                        adapter.addItemAndShow(chatMessage);
                        OpenedChatDatabaseManager.getInstance().insertOrUpdate(new OpenedChat(chatMessage.getTo(), 0, true));
                        GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                            openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
                        });
                        GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                            newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                                newContentBadgeDisplayYier.autoShowNewContentBadge(ChatActivity.this, NewContentBadgeDisplayYier.ID.MESSAGES);
                            });
                        });
                    });
                });
                index.incrementAndGet();
                sendVideoMessages(mediaInfos, index);
            }

        }, (current, total) -> {
            runOnUiThread(() -> {
                binding.sendItemCountIndicator.setText(index.get() + 1 + " / " + mediaInfos.size());
                int progress = (int)((current / (double) total) * binding.sendProgressIndicator.getMax());
                binding.sendProgressIndicator.setProgress(progress, true);
            });
        });
    }
}