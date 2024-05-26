package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChatMessagesRecyclerAdapter;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.data.request.SendImageChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendTextChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityChatBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.MediaUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
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
    private boolean showingMorePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setAutoCancelInput(false);
        setupDefaultBackNavigation(binding.toolbar, ColorUtil.getColor(this, R.color.ichat));
        channel = Objects.requireNonNull(getIntent().getParcelableExtra(ExtraKeys.CHANNEL));
        chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(ChatActivity.this, channel.getIchatId());
        openedChatDatabaseManager = OpenedChatDatabaseManager.getInstance();
        GlobalYiersHolder.holdYier(this, ChatMessageUpdateYier.class, this);
        showContent();
        setupYiers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ChatMessageUpdateYier.class, this);
    }

    private void showContent(){
        binding.toolbar.setTitle(channel.getUsername());
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
        if(thisChannelNewMessages.size() > 0) {
            viewAllNewChatMessages();
            thisChannelNewMessages.sort(Comparator.comparing(ChatMessage::getTime));
            synchronized (this) {
                thisChannelNewMessages.forEach(thisChannelNewMessage -> {
                    if (adapter != null) adapter.addItemToEndAndShow(thisChannelNewMessage);
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
        if (chatMessages.size() == 0) {
            reachStart = true;
            return;
        }
        adapter.addAllToStartAndShow(chatMessages);
        previousPn ++;
    }

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
        binding.recyclerView.addOnApproachEdgeYier(new RecyclerView.OnApproachEdgeYier() {
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
                }else {
                    binding.layoutSendButtonAndIndicator.setVisibility(View.GONE);
                    binding.moreButton.setVisibility(View.VISIBLE);
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
                if(showingMorePanel) {
                    showMorePanel();
                    showingMorePanel = false;
                }
            }
        });
        binding.sendButton.setOnClickListener(v -> {
            String inputtedMessage = UiUtil.getEditTextString(binding.messageInput);
            if(inputtedMessage == null || inputtedMessage.equals("")) return;
            SendTextChatMessagePostBody postBody = new SendTextChatMessagePostBody(channel.getIchatId(), inputtedMessage);
            ChatApiCaller.sendTextChatMessage(this, postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this){
                @Override
                public void start(Call<OperationData> call) {
                    super.start(call);
                    binding.sendButton.setVisibility(View.GONE);
                    binding.sendIndicator.setVisibility(View.VISIBLE);
                }

                @Override
                public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                    super.ok(data, row, call);
                    data.commonHandleResult(ChatActivity.this, new int[]{}, () -> {
                        binding.messageInput.setText(null);
                        ChatMessage chatMessage = data.getData(ChatMessage.class);
                        chatMessage.setViewed(true);
                        ChatMessage.insertToDatabaseAndDetermineShowTime(chatMessage, ChatActivity.this);
                        adapter.addItemToEndAndShow(chatMessage);
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
                }

                @Override
                public void complete(Call<OperationData> call) {
                    super.complete(call);
                    binding.sendButton.setVisibility(View.VISIBLE);
                    binding.sendIndicator.setVisibility(View.GONE);
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
                showingMorePanel = true;
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
        ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        Parcelable[] parcelableArrayExtra = Objects.requireNonNull(data.getParcelableArrayExtra(ExtraKeys.TO_SEND_URIS));
                        for (Parcelable parcelableUri : parcelableArrayExtra) {
                            Uri uri = (Uri) parcelableUri;
                            String imageBase64 = MediaUtil.readUriToBase64(uri, getApplicationContext());
                            String extension = DocumentFile.fromSingleUri(this, uri).getType().replace("image/", "");
                            SendImageChatMessagePostBody postBody = new SendImageChatMessagePostBody(channel.getIchatId(), imageBase64, extension);
                            ChatApiCaller.sendImageChatMessage(this, postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this) {
                                @Override
                                public void start(Call<OperationData> call) {
                                    super.start(call);
                                    binding.sendButton.setVisibility(View.GONE);
                                    binding.sendIndicator.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                                    super.ok(data, row, call);
                                    data.commonHandleResult(ChatActivity.this, new int[]{}, () -> {
                                        ChatMessage chatMessage = data.getData(ChatMessage.class);
                                        chatMessage.setViewed(true);
                                        ChatMessage.insertToDatabaseAndDetermineShowTime(chatMessage, ChatActivity.this);
                                        adapter.addItemToEndAndShow(chatMessage);
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
                                }

                                @Override
                                public void complete(Call<OperationData> call) {
                                    super.complete(call);
                                    binding.sendButton.setVisibility(View.VISIBLE);
                                    binding.sendIndicator.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
        );
        binding.morePanelImage.setOnClickListener(v -> {
            startForResult.launch(new Intent(this, SendImageMessagesActivity.class));
        });
    }

    private void showMorePanel(){
        binding.morePanel.setVisibility(View.VISIBLE);
        binding.moreButton.setVisibility(View.GONE);
        binding.hideMoreButton.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.bar.getLayoutParams();
        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        binding.bar.setLayoutParams(params);
    }

    private void hideMorePanel(){
        binding.morePanel.setVisibility(View.GONE);
        binding.moreButton.setVisibility(View.VISIBLE);
        binding.hideMoreButton.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.bar.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        binding.bar.setLayoutParams(params);
    }

    public Channel getChannel() {
        return channel;
    }
}