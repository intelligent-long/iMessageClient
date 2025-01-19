package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.ExcludeBroadcastChannelLinearLayoutViews;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.ChannelAssociation;
import com.longx.intelligent.android.imessage.data.request.ChangeExcludeBroadcastChannelPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityExcludeBroadcastChannelBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.PermissionApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.CollectionUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class ExcludeBroadcastChannelActivity extends BaseActivity {
    private ActivityExcludeBroadcastChannelBinding binding;
    private ExcludeBroadcastChannelLinearLayoutViews linearLayoutViews;
    private Set<String> excludeBroadcastChannelIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExcludeBroadcastChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        boolean excludeBroadcastChannelsLoaded = SharedPreferencesAccessor.BroadcastPref.getExcludeBroadcastChannelsLoaded(this);
        if(!excludeBroadcastChannelsLoaded){
            UiUtil.setViewGroupEnabled(binding.scrollView, false, true);
        }else {
            excludeBroadcastChannelIds = SharedPreferencesAccessor.BroadcastPref.getAppExcludeBroadcastChannels(this);
            init();
            showContent();
        }
    }

    private void init(){
        linearLayoutViews = new ExcludeBroadcastChannelLinearLayoutViews(this, binding.linearLayoutViews, binding.scrollView, new HashSet<>(excludeBroadcastChannelIds));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateServerData();
    }

    private void updateServerData() {
        if(!binding.scrollView.isEnabled()) return;
        Set<String> appExcludeBroadcastChannels = SharedPreferencesAccessor.BroadcastPref.getAppExcludeBroadcastChannels(this);
        if(CollectionUtil.equals(appExcludeBroadcastChannels, linearLayoutViews.getExcludeBroadcastChannelIds())){
            return;
        }
        Set<String> serverExcludeBroadcastChannels = SharedPreferencesAccessor.BroadcastPref.getServerExcludeBroadcastChannels(this);
        ChangeExcludeBroadcastChannelPostBody postBody = new ChangeExcludeBroadcastChannelPostBody(linearLayoutViews.getExcludeBroadcastChannelIds());
        PermissionApiCaller.changeExcludeBroadcastChannels(null, postBody, new RetrofitApiCaller.BaseCommonYier<OperationStatus>(this.getApplicationContext()){
            @Override
            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(ExcludeBroadcastChannelActivity.this, new int[]{}, () -> {
                    SharedPreferencesAccessor.BroadcastPref.saveAppExcludeBroadcastChannels(ExcludeBroadcastChannelActivity.this, linearLayoutViews.getExcludeBroadcastChannelIds());
                    SharedPreferencesAccessor.BroadcastPref.saveServerExcludeBroadcastChannels(ExcludeBroadcastChannelActivity.this, linearLayoutViews.getExcludeBroadcastChannelIds());
                });
            }

            @Override
            public void notOk(int code, String message, Response<OperationStatus> row, Call<OperationStatus> call) {
                super.notOk(code, message, row, call);
                SharedPreferencesAccessor.BroadcastPref.saveAppExcludeBroadcastChannels(ExcludeBroadcastChannelActivity.this, serverExcludeBroadcastChannels);
            }

            @Override
            public void failure(Throwable t, Call<OperationStatus> call) {
                super.failure(t, call);
                SharedPreferencesAccessor.BroadcastPref.saveAppExcludeBroadcastChannels(ExcludeBroadcastChannelActivity.this, serverExcludeBroadcastChannels);
            }
        });
    }
}