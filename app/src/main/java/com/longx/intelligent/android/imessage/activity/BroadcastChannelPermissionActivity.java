package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.BroadcastChannelPermissionLinearLayoutViews;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.BroadcastChannelPermission;
import com.longx.intelligent.android.imessage.data.ChannelAssociation;
import com.longx.intelligent.android.imessage.data.request.ChangeBroadcastChannelPermissionPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityBroadcastChannelPermissionBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.PermissionApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.CollectionUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastChannelPermissionActivity extends BaseActivity {
    private ActivityBroadcastChannelPermissionBinding binding;
    private BroadcastChannelPermissionLinearLayoutViews linearLayoutViews;
    private BroadcastChannelPermission broadcastChannelPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastChannelPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        broadcastChannelPermission = SharedPreferencesAccessor.BroadcastPref.getAppBroadcastChannelPermission(this);
        if(broadcastChannelPermission == null){
            UiUtil.setViewGroupEnabled(binding.scrollView, false, true);
        }else {
            init();
            showContent();
            setupYiers();
        }
    }

    private void init() {
        linearLayoutViews = new BroadcastChannelPermissionLinearLayoutViews(this, binding.linearLayoutViews, binding.scrollView, new HashSet<>(broadcastChannelPermission.getExcludeConnectedChannels()));
    }

    private void showContent() {
        showRadioButtonChecks(broadcastChannelPermission);
        List<ChannelAssociation> associations = ChannelDatabaseManager.getInstance().findAllAssociations();
        List<BroadcastChannelPermissionLinearLayoutViews.ItemData> itemDataList = new ArrayList<>();
        associations.forEach(association -> {
            itemDataList.add(new BroadcastChannelPermissionLinearLayoutViews.ItemData(association.getChannel()));
        });
        itemDataList.sort((o1, o2) -> {
            if (o1.getIndexChar() == '#') return 1;
            if (o2.getIndexChar() == '#') return -1;
            return Character.compare(o1.getIndexChar(), o2.getIndexChar());
        });
        linearLayoutViews.addItemsAndShow(itemDataList);
    }

    private void showRadioButtonChecks(BroadcastChannelPermission broadcastChannelPermission) {
        switch (broadcastChannelPermission.getPermission()){
            case BroadcastChannelPermission.PUBLIC:{
                binding.radioPublic.setChecked(true);
                hideConnectedChannels();
                break;
            }
            case BroadcastChannelPermission.PRIVATE:{
                binding.radioPrivate.setChecked(true);
                hideConnectedChannels();
                break;
            }
            case BroadcastChannelPermission.CONNECTED_CHANNEL_CIRCLE:{
                binding.radioConnectedChannelCircle.setChecked(true);
                showConnectedChannels();
                break;
            }
        }
    }

    private void setupYiers() {
        View.OnClickListener yier = v -> {
            int permission = -1;
            if(v.equals(binding.layoutPublic)){
                permission = BroadcastChannelPermission.PUBLIC;
                binding.radioPublic.setChecked(true);
                binding.radioPrivate.setChecked(false);
                binding.radioConnectedChannelCircle.setChecked(false);
                hideConnectedChannels();
            }else if(v.equals(binding.layoutPrivate)){
                permission = BroadcastChannelPermission.PRIVATE;
                binding.radioPublic.setChecked(false);
                binding.radioPrivate.setChecked(true);
                binding.radioConnectedChannelCircle.setChecked(false);
                hideConnectedChannels();
            }else if(v.equals(binding.layoutConnectedChannelCircle)){
                permission = BroadcastChannelPermission.CONNECTED_CHANNEL_CIRCLE;
                binding.radioPublic.setChecked(false);
                binding.radioPrivate.setChecked(false);
                binding.radioConnectedChannelCircle.setChecked(true);
                showConnectedChannels();
            }
            SharedPreferencesAccessor.BroadcastPref.saveAppBroadcastChannelPermission(BroadcastChannelPermissionActivity.this, new BroadcastChannelPermission(permission, linearLayoutViews.getExcludeConnectedChannels()));
        };
        binding.layoutPublic.setOnClickListener(yier);
        binding.layoutPrivate.setOnClickListener(yier);
        binding.layoutConnectedChannelCircle.setOnClickListener(yier);
    }

    private int getCurrentCheckedPermission() {
        int permission = -1;
        if(binding.radioPrivate.isChecked()){
            permission = BroadcastChannelPermission.PRIVATE;
        }else if(binding.radioPublic.isChecked()){
            permission = BroadcastChannelPermission.PUBLIC;
        }else if(binding.radioConnectedChannelCircle.isChecked()){
            permission = BroadcastChannelPermission.CONNECTED_CHANNEL_CIRCLE;
        }
        return permission;
    }

    private void showConnectedChannels(){
        binding.linearLayoutViews.setVisibility(View.VISIBLE);
    }

    private void hideConnectedChannels(){
        binding.linearLayoutViews.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateServerData();
    }

    private void updateServerData() {
        if(!binding.scrollView.isEnabled()) return;
        int currentCheckedPermission = getCurrentCheckedPermission();
        BroadcastChannelPermission serverBroadcastChannelPermission = SharedPreferencesAccessor.BroadcastPref.getServerBroadcastChannelPermission(this);
        if(serverBroadcastChannelPermission != null &&
                serverBroadcastChannelPermission.getPermission() == currentCheckedPermission &&
                CollectionUtil.equals(serverBroadcastChannelPermission.getExcludeConnectedChannels(), linearLayoutViews.getExcludeConnectedChannels())) {
            return;
        }
        ChangeBroadcastChannelPermissionPostBody postBody = new ChangeBroadcastChannelPermissionPostBody(currentCheckedPermission, linearLayoutViews.getExcludeConnectedChannels());
        PermissionApiCaller.changeBroadcastChannelPermission(null, postBody, new RetrofitApiCaller.BaseCommonYier<OperationStatus>(this.getApplicationContext()){

            @Override
            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(BroadcastChannelPermissionActivity.this, new int[]{}, () -> {
                    SharedPreferencesAccessor.BroadcastPref.saveAppBroadcastChannelPermission(BroadcastChannelPermissionActivity.this, new BroadcastChannelPermission(currentCheckedPermission, linearLayoutViews.getExcludeConnectedChannels()));
                    SharedPreferencesAccessor.BroadcastPref.saveServerBroadcastChannelPermission(BroadcastChannelPermissionActivity.this, new BroadcastChannelPermission(currentCheckedPermission, linearLayoutViews.getExcludeConnectedChannels()));
                });
            }

            @Override
            public void notOk(int code, String message, Response<OperationStatus> row, Call<OperationStatus> call) {
                super.notOk(code, message, row, call);
                SharedPreferencesAccessor.BroadcastPref.saveAppBroadcastChannelPermission(BroadcastChannelPermissionActivity.this, serverBroadcastChannelPermission);
            }

            @Override
            public void failure(Throwable t, Call<OperationStatus> call) {
                super.failure(t, call);
                SharedPreferencesAccessor.BroadcastPref.saveAppBroadcastChannelPermission(BroadcastChannelPermissionActivity.this, serverBroadcastChannelPermission);
            }
        });
    }

    public ActivityBroadcastChannelPermissionBinding getBinding() {
        return binding;
    }
}