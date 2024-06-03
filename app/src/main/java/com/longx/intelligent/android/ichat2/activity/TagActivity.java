package com.longx.intelligent.android.ichat2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.bottomsheet.AddChannelTagBottomSheet;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.databinding.ActivityTagBinding;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;

import java.util.List;

public class TagActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivityTagBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        showContent();
        setUpYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void showContent() {
        List<ChannelTag> allChannelTags = ChannelDatabaseManager.getInstance().findAllChannelTags();
        if(allChannelTags.size() == 0){
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        }else {
            binding.noContentLayout.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.recyclerView.setLayoutManager(layoutManager);
            ChannelTagsRecyclerAdapter adapter = new ChannelTagsRecyclerAdapter(this, allChannelTags);
            binding.recyclerView.setAdapter(adapter);
        }
    }

    private void setUpYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.add){
                new AddChannelTagBottomSheet(this).show();
            }
            return true;
        });
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CHANNEL_TAGS)){
            showContent();
        }
    }
}