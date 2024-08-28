package com.longx.intelligent.android.ichat2.activity;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChatMessagesRecyclerAdapter;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.behavior.VoiceChatMessageBehaviours;
import com.longx.intelligent.android.ichat2.da.FileHelper;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.data.ChatMessageAllow;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.data.request.SendFileChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendImageChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendTextChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendVideoChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityChatBinding;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.MediaInfo;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.FileUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.ChatMessageUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.KeyboardVisibilityYier;
import com.longx.intelligent.android.ichat2.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;
import com.longx.intelligent.android.ichat2.yier.TextChangedYier;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Response;

public class ChatActivity extends BaseActivity implements ChatMessageUpdateYier {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setAutoCancelInput(false);
        setupBackNavigation(binding.toolbar, ColorUtil.getColor(this, R.color.ichat));
        channel = Objects.requireNonNull(getIntent().getParcelableExtra(ExtraKeys.CHANNEL));
        chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(ChatActivity.this, channel.getIchatId());
        openedChatDatabaseManager = OpenedChatDatabaseManager.getInstance();
        openedChatDatabaseManager.updateShow(channel.getIchatId(), true);
        channelDatabaseManager = ChannelDatabaseManager.getInstance();
        GlobalYiersHolder.holdYier(this, ChatMessageUpdateYier.class, this);
        init();
        showContent();
        setupYiers();
        initResultLauncher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ChatMessageUpdateYier.class, this);
        if(adapter != null) adapter.onActivityDestroy();
    }

    private void init(){
        changeHoldToTalkToNormal();
        changeCancelSendTalkFabToNormal();
        voiceChatMessageBehaviours = new VoiceChatMessageBehaviours(this);
    }

    private void showContent(){
        binding.toolbar.setTitle(channel.getNote() == null ? channel.getUsername() : channel.getNote());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        synchronized (this) {
            initialChatMessageCount = chatMessageDatabaseManager.count();
            adapter = new ChatMessagesRecyclerAdapter(this, binding.recyclerView);
            binding.recyclerView.setAdapter(adapter);
            showChatMessages();
        }
        if(openedChatDatabaseManager.findNotViewedCount(channel.getIchatId()) > 0) {
            viewAllNewChatMessages();
        }
        ChatMessageAllow chatMessageAllow = channelDatabaseManager.findOneAssociations(channel.getIchatId()).getChatMessageAllowToThem();
        if(!chatMessageAllow.isAllowVoice()){
            UiUtil.setViewEnabled(binding.voiceButton, false, true);
        }
    }

    private void showChatMessages() {
        previousPage();
        binding.recyclerView.scrollToEnd(false);
    }

    @Override
    public void onNewChatMessage(List<ChatMessage> newChatMessages) {
        List<ChatMessage> thisChannelNewMessages = new ArrayList<>();
        newChatMessages.forEach(newChatMessage -> {
            if(newChatMessage.getOther(this).equals(channel.getIchatId())){
                thisChannelNewMessages.add(newChatMessage);
            }
        });
        if(!thisChannelNewMessages.isEmpty()) {
            viewAllNewChatMessages();
            thisChannelNewMessages.sort(Comparator.comparing(ChatMessage::getTime));
            synchronized (this) {
                thisChannelNewMessages.forEach(thisChannelNewMessage -> {
                    if (adapter != null) adapter.addItemAndShow(thisChannelNewMessage);
                });
            }
        }
    }

    private void viewAllNewChatMessages() {
        ChatApiCaller.viewAllNewMessage(this, channel.getIchatId(), new RetrofitApiCaller.BaseCommonYier<OperationStatus>(this){
            @Override
            public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                super.ok(data, row, call);
                data.commonHandleResult(ChatActivity.this, new int[]{}, () -> {
                    openedChatDatabaseManager.updateNotViewedCount(0, channel.getIchatId());
                    chatMessageDatabaseManager.setAllToViewed();
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
        KeyboardVisibilityYier.setYier(this, new KeyboardVisibilityYier.Yier() {
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
            SendTextChatMessagePostBody postBody = new SendTextChatMessagePostBody(channel.getIchatId(), inputtedMessage);
            ChatApiCaller.sendTextChatMessage(this, postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this){
                @Override
                public void start(Call<OperationData> call) {
                    super.start(call);
                    toSendingState();
                }

                @Override
                public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                    super.ok(data, row, call);
                    data.commonHandleResult(ChatActivity.this, new int[]{-101}, () -> {
                        binding.messageInput.setText(null);
                        ChatMessage chatMessage = data.getData(ChatMessage.class);
                        chatMessage.setViewed(true);
                        ChatMessage.mainDoOnNewChatMessage(chatMessage, ChatActivity.this, results -> {
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
            if(KeyboardVisibilityYier.isKeyboardVisible(this)) {
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
            intent.putExtra(ExtraKeys.MAX_ALLOW_IMAGE_SIZE, Constants.MAX_ONCE_SEND_CHAT_MESSAGE_IMAGE_COUNT);
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
            intent.putExtra(ExtraKeys.MAX_ALLOW_VIDEO_SIZE, Constants.MAX_ONCE_SEND_CHAT_MESSAGE_VIDEO_COUNT);
            sendVideoMessageResultLauncher.launch(intent);
        });
        binding.morePanelRecordVideo.setOnClickListener(v -> {
            sendVideoMessageResultLauncher.launch(new Intent(this, RecordAndSendVideoActivity.class));
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
        binding.holdToTalkButton.setIconTint(ColorStateList.valueOf(getColor(R.color.ichat)));
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

    private void initResultLauncher() {
        sendImageMessageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        Parcelable[] parcelableArrayExtra = Objects.requireNonNull(data.getParcelableArrayExtra(ExtraKeys.MEDIA_INFOS));
                        List<MediaInfo> uriList = Utils.parseParcelableArray(parcelableArrayExtra);
                        if(uriList.size() > Constants.MAX_ONCE_SEND_CHAT_MESSAGE_IMAGE_COUNT){
                            MessageDisplayer.autoShow(this, "最多一次发送 " + Constants.MAX_ONCE_SEND_CHAT_MESSAGE_IMAGE_COUNT + " 张图片", MessageDisplayer.Duration.LONG);
                        }else {
                            onSendImageMessages(uriList);
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
                                    MessageDisplayer.autoShow(this, "发送文件最大不能超过 " + FileUtil.formatFileSize(Constants.MAX_SEND_CHAT_MESSAGE_VIDEO_SIZE), MessageDisplayer.Duration.LONG);
                                    return;
                                }
                            }
                            if(mediaInfos.size() > Constants.MAX_ONCE_SEND_CHAT_MESSAGE_VIDEO_COUNT){
                                MessageDisplayer.autoShow(this, "最多一次发送 " + Constants.MAX_ONCE_SEND_CHAT_MESSAGE_VIDEO_COUNT + " 个文件", MessageDisplayer.Duration.LONG);
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
        SendImageChatMessagePostBody postBody = new SendImageChatMessagePostBody(channel.getIchatId(), fileName);
        ChatApiCaller.sendImageChatMessage(this, this, mediaInfo.getUri(), postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this) {
            @Override
            public void start(Call<OperationData> call) {
                super.start(call);
                binding.sendProgressIndicator.setProgress(0, false);
                if(index.get() == 0) {
                    toSendingProgressState();
                }
            }

            @Override
            public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                super.ok(data, row, call);
                data.commonHandleResult(ChatActivity.this, new int[]{-101, -102}, () -> {
                    ChatMessage chatMessage = data.getData(ChatMessage.class);
                    chatMessage.setViewed(true);
                    ChatMessage.mainDoOnNewChatMessage(chatMessage, ChatActivity.this, results -> {
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
        }, (current, total, i) -> {
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
        SendFileChatMessagePostBody postBody = new SendFileChatMessagePostBody(channel.getIchatId(), fileName);
        ChatApiCaller.sendFileChatMessage(this, this, uri, postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this) {
            @Override
            public void start(Call<OperationData> call) {
                super.start(call);
                binding.sendProgressIndicator.setProgress(0, false);
                if (index.get() == 0) {
                    toSendingProgressState();
                }
            }

            @Override
            public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                super.ok(data, row, call);
                data.commonHandleResult(ChatActivity.this, new int[]{-101, -102}, () -> {
                    ChatMessage chatMessage = data.getData(ChatMessage.class);
                    chatMessage.setViewed(true);
                    ChatMessage.mainDoOnNewChatMessage(chatMessage, ChatActivity.this, results -> {
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
        }, (current, total, i) -> {
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
        SendVideoChatMessagePostBody postBody = new SendVideoChatMessagePostBody(channel.getIchatId(), fileName);
        ChatApiCaller.sendVideoChatMessage(this, this, mediaInfo.getUri(), postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this){
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
            public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                super.ok(data, row, call);
                data.commonHandleResult(ChatActivity.this, new int[]{-101, -102}, () -> {
                    ChatMessage chatMessage = data.getData(ChatMessage.class);
                    chatMessage.setViewed(true);
                    ChatMessage.mainDoOnNewChatMessage(chatMessage, ChatActivity.this, results -> {
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

        }, (current, total, i) -> {
            runOnUiThread(() -> {
                binding.sendItemCountIndicator.setText(index.get() + 1 + " / " + mediaInfos.size());
                int progress = (int)((current / (double) total) * binding.sendProgressIndicator.getMax());
                binding.sendProgressIndicator.setProgress(progress, true);
            });
        });
    }
}