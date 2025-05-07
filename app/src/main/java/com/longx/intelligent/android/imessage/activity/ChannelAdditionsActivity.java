package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.ChannelAdditionActivitiesPagerAdapter;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.ChannelAddition;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.databinding.ActivityChannelAdditionsBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.yier.ChannelAdditionActivitiesFetchYier;
import com.longx.intelligent.android.imessage.yier.ChannelAdditionActivitiesUpdateYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChannelAdditionsActivity extends BaseActivity implements ChannelAdditionActivitiesFetchYier, ChannelAdditionActivitiesUpdateYier {
    private ActivityChannelAdditionsBinding binding;
    private int initTabIndex;
    private static String[] PAGER_TITLES;
    private ChannelAdditionActivitiesPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PAGER_TITLES = new String[]{getString(R.string.channel_addition_activity_pending),
                getString(R.string.channel_addition_activity_send),
                getString(R.string.channel_addition_activity_receive)};
        super.onCreate(savedInstanceState);
        initTabIndex = getIntent().getIntExtra(ExtraKeys.INIT_TAB_INDEX, 0);
        binding = ActivityChannelAdditionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupUi();
        GlobalYiersHolder.holdYier(this, ChannelAdditionActivitiesUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ChannelAdditionActivitiesUpdateYier.class, this);
    }

    private void setupUi() {
        pagerAdapter = new ChannelAdditionActivitiesPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) -> tab.setText(PAGER_TITLES[position])).attach();
        binding.tabs.post(() -> {
            TabLayout.Tab tab = binding.tabs.getTabAt(initTabIndex);
            if (tab != null) {
                tab.select();
            }
        });
    }

    @Override
    public void onStartFetch() {
        pagerAdapter.getPendingFragment().onStartFetch();
        pagerAdapter.getSendFragment().onStartFetch();
        pagerAdapter.getReceiveFragment().onStartFetch();
    }

    @Override
    public void onFetched(List<ChannelAddition> channelAdditions) {
        SharedPreferencesAccessor.ApiJson.ChannelAdditionActivities.clearRecords(this);
        channelAdditions.forEach(channelAdditionInfo -> {
            SharedPreferencesAccessor.ApiJson.ChannelAdditionActivities.addRecord(this, channelAdditionInfo);
        });
        pagerAdapter.getPendingFragment().onFetched(channelAdditions);
        pagerAdapter.getSendFragment().onFetched(channelAdditions);
        pagerAdapter.getReceiveFragment().onFetched(channelAdditions);
    }

    @Override
    public void onFailure(String failureMessage) {
        pagerAdapter.getPendingFragment().onFailure(failureMessage);
        pagerAdapter.getSendFragment().onFailure(failureMessage);
        pagerAdapter.getReceiveFragment().onFailure(failureMessage);
    }

    private void fetchAndShowContent() {
        onStartFetch();
        ChannelApiCaller.fetchAllAdditionActivities(this, new RetrofitApiCaller.CommonYier<OperationData>(this, false, true){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(ChannelAdditionsActivity.this, new int[]{}, () -> {
                    List<ChannelAddition> channelAdditions = data.getData(new TypeReference<List<ChannelAddition>>() {
                    });
                    onFetched(channelAdditions);
                });
            }

            @Override
            public void notOk(int code, String message, Response<OperationData> row, Call<OperationData> call) {
                super.notOk(code, message, row, call);
                ChannelAdditionsActivity.this.onFailure("HTTP 状态码异常 > " + code);
            }

            @Override
            public void failure(Throwable t, Call<OperationData> call) {
                super.failure(t, call);
                ChannelAdditionsActivity.this.onFailure("出错了 > " + t.getClass().getName());
            }
        });
    }

    @Override
    public void onChannelAdditionActivitiesUpdate() {
        runOnUiThread(this::fetchAndShowContent);
    }
}