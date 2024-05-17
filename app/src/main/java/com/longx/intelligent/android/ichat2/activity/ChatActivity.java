package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChatMessagesRecyclerAdapter;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.data.request.SendChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityChatBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;
import com.longx.intelligent.android.ichat2.yier.SoftKeyBoardYier;
import com.longx.intelligent.android.ichat2.yier.TextChangedYier;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private Channel channel;
    private ChatMessagesRecyclerAdapter adapter;
    private ChatMessageDatabaseManager chatMessageDatabaseManager;
    private OpenedChatDatabaseManager openedChatDatabaseManager;
    private static final int PS = 50;
    private int previousPn = 0;
    private int nextPn = -1;
    private boolean reachStart;

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
        showContent();
        setupYiers();
    }

    private void showContent(){
        binding.toolbar.setTitle(channel.getUsername());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatMessagesRecyclerAdapter(this, binding.recyclerView);
        binding.recyclerView.setAdapter(adapter);
        showChatMessages();
        if(openedChatDatabaseManager.findNotViewedCount(channel.getIchatId()) > 0) {
            viewAllNewChatMessages();
        }
    }

    private void showChatMessages() {
        previousPage();
        binding.recyclerView.scrollToEnd(false);
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
                });
            }
        });
    }

    private synchronized void previousPage(){
        if(reachStart) return;
        List<ChatMessage> chatMessages = chatMessageDatabaseManager.findLimit(previousPn * PS, PS, true);
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
        SoftKeyBoardYier.setListener(this, new SoftKeyBoardYier.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow() {
                if(!(nextPn < 0)){
                    previousPn = 0;
                    nextPn = -1;
                    adapter.clearAndShow();
                    previousPage();
                }
                binding.recyclerView.scrollToEnd(true);
            }

            @Override
            public void keyBoardHide() {
            }
        });
        binding.sendButton.setOnClickListener(v -> {
            String inputtedMessage = UiUtil.getEditTextString(binding.messageInput);
            if(inputtedMessage == null || inputtedMessage.equals("")) return;
            SendChatMessagePostBody postBody = new SendChatMessagePostBody(SendChatMessagePostBody.TYPE_TEXT, channel.getIchatId(), inputtedMessage);
            ChatApiCaller.sendChatMessage(this, postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this){
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
                        GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                            openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
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
    }
}