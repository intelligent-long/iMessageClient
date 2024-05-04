package com.longx.intelligent.android.ichat2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChannelAdditionActivitiesViewPagerAdapter;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionInfo;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.databinding.ActivityChannelAdditionActivitiesBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.yier.ChannelAdditionActivitiesFetchYier;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChannelAdditionActivitiesActivity extends BaseActivity implements ChannelAdditionActivitiesFetchYier {
    private ActivityChannelAdditionActivitiesBinding binding;
    private static String[] PAGER_TITLES;
    private ChannelAdditionActivitiesViewPagerAdapter pagerAdapter;

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
        fetchAndShowContent();
    }

    private void setupUi() {
        pagerAdapter = new ChannelAdditionActivitiesViewPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) -> tab.setText(PAGER_TITLES[position])).attach();
    }

    @Override
    public void onStartFetch() {
        pagerAdapter.getPendingFragment().onStartFetch();
        pagerAdapter.getSendFragment().onStartFetch();
        pagerAdapter.getReceiveFragment().onStartFetch();
    }

    @Override
    public void onFetched(List<ChannelAdditionInfo> channelAdditionInfos) {
        pagerAdapter.getPendingFragment().onFetched(channelAdditionInfos);
        pagerAdapter.getSendFragment().onFetched(channelAdditionInfos);
        pagerAdapter.getReceiveFragment().onFetched(channelAdditionInfos);
    }

    private void fetchAndShowContent() {
        onStartFetch();
        ChannelApiCaller.fetchAllAdditionActivities(this, new RetrofitApiCaller.CommonYier<OperationData>(this, false, true){
            @Override
            public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                super.ok(data, row, call);
                List<ChannelAdditionInfo> channelAdditionInfos = data.getData(new TypeReference<List<ChannelAdditionInfo>>() {
                });
                onFetched(channelAdditionInfos);
            }
        });
    }
}