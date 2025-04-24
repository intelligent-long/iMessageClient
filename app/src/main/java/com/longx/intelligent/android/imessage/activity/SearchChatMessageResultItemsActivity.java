package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.SearchChatMessageResultItemsRecyclerAdapter;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.ChatMessage;
import com.longx.intelligent.android.imessage.databinding.ActivitySearchChatMessageResultItemsBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchChatMessageResultItemsActivity extends BaseActivity {
    private ActivitySearchChatMessageResultItemsBinding binding;
    private List<ChatMessage> searchedChatMessages;
    private SearchChatMessageResultItemsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchChatMessageResultItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        init();
        showContent();
        setupYiers();
    }

    private void intentData() {
        searchedChatMessages = getIntent().getParcelableArrayListExtra(ExtraKeys.CHAT_MESSAGES);
    }

    private void init(){
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<SearchChatMessageResultItemsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        searchedChatMessages.forEach(searchedChatMessage -> itemDataList.add(new SearchChatMessageResultItemsRecyclerAdapter.ItemData(searchedChatMessage)));
        adapter = new SearchChatMessageResultItemsRecyclerAdapter(this, itemDataList);
    }

    private void showContent() {
        String name = ChannelDatabaseManager.getInstance().findOneChannel(searchedChatMessages.get(0).getOther(this)).autoGetName();
        binding.toolbar.setTitle("和 " + name + " 的相关消息记录");
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupYiers() {

    }
}