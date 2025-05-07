package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.GroupChannelAdditionsActivityPagerAdapter;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelAdditionsBinding;

public class GroupChannelAdditionsActivity extends BaseActivity {
    private ActivityGroupChannelAdditionsBinding binding;
    private GroupChannelAdditionsActivityPagerAdapter pagerAdapter;
    private int initTabIndex;
    private static String[] PAGER_TITLES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelAdditionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        setupUi();
    }

    private void intentData() {
        initTabIndex = getIntent().getIntExtra(ExtraKeys.INIT_TAB_INDEX, 0);
    }

    private void setupUi() {
        pagerAdapter = new GroupChannelAdditionsActivityPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        PAGER_TITLES = new String[]{
                getString(R.string.channel_addition_activity_pending),
                getString(R.string.channel_addition_activity_send),
                getString(R.string.channel_addition_activity_receive)
        };
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) -> tab.setText(PAGER_TITLES[position])).attach();
        binding.tabs.post(() -> {
            TabLayout.Tab tab = binding.tabs.getTabAt(initTabIndex);
            if (tab != null) {
                tab.select();
            }
        });
    }
}