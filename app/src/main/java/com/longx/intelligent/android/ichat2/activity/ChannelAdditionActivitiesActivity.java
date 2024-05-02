package com.longx.intelligent.android.ichat2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChannelAdditionActivitiesViewPagerAdapter;
import com.longx.intelligent.android.ichat2.databinding.ActivityChannelAdditionActivitiesBinding;

public class ChannelAdditionActivitiesActivity extends BaseActivity {
    private ActivityChannelAdditionActivitiesBinding binding;
    private static String[] PAGER_TITLES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PAGER_TITLES = new String[]{getString(R.string.channel_addition_activity_pending),
                getString(R.string.channel_addition_activity_send),
                getString(R.string.channel_addition_activity_receive)};
        super.onCreate(savedInstanceState);
        binding = ActivityChannelAdditionActivitiesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupUi();
    }

    private void setupUi() {
        binding.viewPager.setAdapter(new ChannelAdditionActivitiesViewPagerAdapter(this));
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) -> tab.setText(PAGER_TITLES[position])).attach();
    }
}