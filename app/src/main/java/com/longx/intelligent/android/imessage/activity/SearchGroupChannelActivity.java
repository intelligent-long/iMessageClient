package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.SearchChannelLinearLayoutViews;
import com.longx.intelligent.android.imessage.adapter.SearchGroupChannelLinearLayoutViews;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.ActivitySearchChannelBinding;
import com.longx.intelligent.android.imessage.databinding.ActivitySearchGroupChannelBinding;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.yier.TextChangedYier;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupChannelActivity extends BaseActivity {
    private ActivitySearchGroupChannelBinding binding;
    private SearchGroupChannelLinearLayoutViews linearLayoutViews;
    private String searchStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchGroupChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        init();
        setupYiers();
    }

    private void init(){
        linearLayoutViews = new SearchGroupChannelLinearLayoutViews(this, binding.linearLayoutViews, binding.scrollView);
        binding.searchInput.postDelayed(() -> UiUtil.openKeyboard(binding.searchEdit), 240);
    }

    private void setupYiers() {
        binding.searchEdit.addTextChangedListener(new TextChangedYier(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                linearLayoutViews.clear();
                searchStr = s.toString();
                if(!searchStr.isEmpty()){
                    List<GroupChannel> searched = GroupChannelDatabaseManager.getInstance().search(searchStr);
                    List<SearchGroupChannelLinearLayoutViews.ItemData> itemDataList = new ArrayList<>();
                    searched.forEach(groupChannel -> itemDataList.add(new SearchGroupChannelLinearLayoutViews.ItemData(groupChannel)));
                    itemDataList.sort((o1, o2) -> {
                        if (o1.getIndexChar() == '#') return 1;
                        if (o2.getIndexChar() == '#') return -1;
                        return Character.compare(o1.getIndexChar(), o2.getIndexChar());
                    });
                    linearLayoutViews.addItemsAndShow(itemDataList);
                }
            }
        });
    }

    public ActivitySearchGroupChannelBinding getBinding() {
        return binding;
    }

    public String getSearchStr() {
        return searchStr;
    }
}