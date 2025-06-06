package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.GroupChannelAdditionsActivityPagerAdapter;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.GroupChannelActivity;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelAdditionsBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.GroupChannelAdditionActivitiesFetchYier;
import com.longx.intelligent.android.imessage.yier.GroupChannelAdditionActivitiesUpdateYier;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GroupChannelAdditionsActivity extends BaseActivity implements GroupChannelAdditionActivitiesUpdateYier, GroupChannelAdditionActivitiesFetchYier {
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
        GlobalYiersHolder.holdYier(this, GroupChannelAdditionActivitiesUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, GroupChannelAdditionActivitiesUpdateYier.class, this);
    }

    private void intentData() {
        initTabIndex = getIntent().getIntExtra(ExtraKeys.INIT_TAB_INDEX, 0);
    }

    private void setupUi() {
        pagerAdapter = new GroupChannelAdditionsActivityPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        PAGER_TITLES = new String[]{
                getString(R.string.group_channel_addition_activity_pending),
                getString(R.string.group_channel_addition_activity_send),
                getString(R.string.group_channel_addition_activity_receive),
                getString(R.string.group_channel_addition_activity_invite)
        };
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
        pagerAdapter.getInviteFragment().onStartFetch();
    }

    @Override
    public void onFetched(List<com.longx.intelligent.android.imessage.data.GroupChannelActivity> groupChannelActivities) {
        SharedPreferencesAccessor.ApiJson.GroupChannelAdditionActivities.clearRecords(this);
        groupChannelActivities.forEach(groupChannelActivity-> {
            SharedPreferencesAccessor.ApiJson.GroupChannelAdditionActivities.addRecord(this, groupChannelActivity);
        });
        pagerAdapter.getPendingFragment().onFetched(groupChannelActivities);
        pagerAdapter.getSendFragment().onFetched(groupChannelActivities);
        pagerAdapter.getReceiveFragment().onFetched(groupChannelActivities);
        pagerAdapter.getInviteFragment().onFetched(groupChannelActivities);
    }

    @Override
    public void onFailure(String failureMessage) {
        pagerAdapter.getPendingFragment().onFailure(failureMessage);
        pagerAdapter.getSendFragment().onFailure(failureMessage);
        pagerAdapter.getReceiveFragment().onFailure(failureMessage);
        pagerAdapter.getInviteFragment().onFailure(failureMessage);
    }

    private void fetchAndShowContent() {
        onStartFetch();
        GroupChannelApiCaller.fetchAllGroupAdditionActivities(this, new RetrofitApiCaller.CommonYier<OperationData>(this, false, true){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(GroupChannelAdditionsActivity.this, new int[]{}, () -> {
                    List<GroupChannelActivity> groupChannelActivities = data.getData(new TypeReference<List<GroupChannelActivity>>() {
                    });
                    onFetched(groupChannelActivities);
                });
            }

            @Override
            public void notOk(int code, String message, Response<OperationData> row, Call<OperationData> call) {
                super.notOk(code, message, row, call);
                GroupChannelAdditionsActivity.this.onFailure("HTTP 状态码异常 > " + code);
            }

            @Override
            public void failure(Throwable t, Call<OperationData> call) {
                super.failure(t, call);
                GroupChannelAdditionsActivity.this.onFailure("出错了 > " + t.getClass().getName());
            }
        });
    }

    @Override
    public void onGroupChannelAdditionActivitiesUpdate() {
        runOnUiThread(this::fetchAndShowContent);
    }
}