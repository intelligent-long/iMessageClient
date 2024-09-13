package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayoutMediator;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastInteractionsPagerAdapter;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastInteractionsBinding;

public class BroadcastInteractionsActivity extends BaseActivity {
    private ActivityBroadcastInteractionsBinding binding;
    private static String[] PAGER_TITLES;
    private BroadcastInteractionsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PAGER_TITLES = new String[]{getString(R.string.broadcast_interactions_activity_like),
                getString(R.string.broadcast_interactions_activity_comment),
                getString(R.string.broadcast_interactions_activity_reply)};
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastInteractionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        init();
    }

    private void init(){
        pagerAdapter = new BroadcastInteractionsPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) -> tab.setText(PAGER_TITLES[position])).attach();
    }
}