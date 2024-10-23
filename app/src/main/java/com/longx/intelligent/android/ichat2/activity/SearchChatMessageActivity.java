package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.SearchChannelLinearLayoutViews;
import com.longx.intelligent.android.ichat2.adapter.SearchChatMessageLinearLayoutViews;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChannelAssociation;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.databinding.ActivitySearchChatMessageBinding;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.yier.TextChangedYier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchChatMessageActivity extends BaseActivity {
    private ActivitySearchChatMessageBinding binding;
    private SearchChatMessageLinearLayoutViews linearLayoutViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchChatMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        init();
        setupYiers();
    }

    private void init(){
        linearLayoutViews = new SearchChatMessageLinearLayoutViews(this, binding.linearLayoutViews, binding.scrollView);
        binding.searchInput.postDelayed(() -> UiUtil.openKeyboard(binding.searchEdit), 240);
    }

    private void setupYiers() {
        binding.searchEdit.addTextChangedListener(new TextChangedYier(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                linearLayoutViews.clear();
                String searchStr = s.toString();
                if(!searchStr.isEmpty()){
                    List<List<ChatMessage>> searchedData = new ArrayList<>();
                    List<ChannelAssociation> allAssociations = ChannelDatabaseManager.getInstance().findAllAssociations();
                    allAssociations.forEach(association -> {
                        ChatMessageDatabaseManager databaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(SearchChatMessageActivity.this, association.getChannelIchatId());
                        List<ChatMessage> searched = databaseManager.search(searchStr);
                        if(!searched.isEmpty()){
                            searchedData.add(searched);
                        }
                    });
                    searchedData.sort((o1, o2) -> - o1.get(0).getTime().compareTo(o2.get(0).getTime()));
                    linearLayoutViews.addItemsAndShow(searchedData);
                }
            }
        });
    }
}