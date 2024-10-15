package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastChannelPermissionLinearLayoutViews;
import com.longx.intelligent.android.ichat2.adapter.ExcludeBroadcastChannelLinearLayoutViews;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.ChannelAssociation;
import com.longx.intelligent.android.ichat2.databinding.ActivityExcludeBroadcastChannelBinding;

import java.util.ArrayList;
import java.util.List;

public class ExcludeBroadcastChannelActivity extends BaseActivity {
    private ActivityExcludeBroadcastChannelBinding binding;
    private ExcludeBroadcastChannelLinearLayoutViews linearLayoutViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExcludeBroadcastChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        init();
        showContent();
    }

    private void init(){
        linearLayoutViews = new ExcludeBroadcastChannelLinearLayoutViews(this, binding.linearLayoutViews, binding.scrollView);
    }

    private void showContent(){
        List<ChannelAssociation> associations = ChannelDatabaseManager.getInstance().findAllAssociations();
        List<ExcludeBroadcastChannelLinearLayoutViews.ItemData> itemDataList = new ArrayList<>();
        associations.forEach(association -> {
            itemDataList.add(new ExcludeBroadcastChannelLinearLayoutViews.ItemData(association.getChannel()));
        });
        itemDataList.sort((o1, o2) -> {
            if (o1.getIndexChar() == '#') return 1;
            if (o2.getIndexChar() == '#') return -1;
            return Character.compare(o1.getIndexChar(), o2.getIndexChar());
        });
        linearLayoutViews.addItemsAndShow(itemDataList);
    }
}