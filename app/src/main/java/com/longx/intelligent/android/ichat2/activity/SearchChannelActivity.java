package com.longx.intelligent.android.ichat2.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.SearchChannelLinearLayoutViews;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.databinding.ActivitySearchChannelBinding;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.yier.TextChangedYier;

import java.util.ArrayList;
import java.util.List;

public class SearchChannelActivity extends BaseActivity {
    private ActivitySearchChannelBinding binding;
    private SearchChannelLinearLayoutViews linearLayoutViews;
    private String searchStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        init();
        setupYiers();
    }

    private void init(){
        linearLayoutViews = new SearchChannelLinearLayoutViews(this, binding.linearLayoutViews, binding.scrollView);
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
                    List<Channel> searched = ChannelDatabaseManager.getInstance().search(searchStr);
                    List<SearchChannelLinearLayoutViews.ItemData> itemDataList = new ArrayList<>();
                    searched.forEach(channel -> itemDataList.add(new SearchChannelLinearLayoutViews.ItemData(channel)));
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

    public ActivitySearchChannelBinding getBinding() {
        return binding;
    }

    public String getSearchStr() {
        return searchStr;
    }
}