package com.longx.intelligent.android.ichat2.activity;

import android.app.KeyguardManager;
import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastChannelPermissionLinearLayoutViews;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.BroadcastChannelPermission;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChannelAssociation;
import com.longx.intelligent.android.ichat2.data.UserInfo;
import com.longx.intelligent.android.ichat2.data.request.ChangeBroadcastChannelPermissionPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastChannelPermissionBinding;
import com.longx.intelligent.android.ichat2.databinding.LinearLayoutViewsFooterBroadcastChannelPermissionBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.PermissionApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.CollectionUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastChannelPermissionActivity extends BaseActivity {
    private ActivityBroadcastChannelPermissionBinding binding;
    private BroadcastChannelPermissionLinearLayoutViews linearLayoutViews;
    private LinearLayoutViewsFooterBroadcastChannelPermissionBinding footerBinding;
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
            showRadioButtonChecks(broadcastChannelPermission);
            init();
            setupYiers();
        }
    }

    private void init() {
        linearLayoutViews = new BroadcastChannelPermissionLinearLayoutViews(this, binding.linearLayoutViews, binding.scrollView, broadcastChannelPermission.getExcludeConnectedChannels());
        footerBinding = LinearLayoutViewsFooterBroadcastChannelPermissionBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        linearLayoutViews.setFooter(footerBinding.getRoot());
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
            if(v.equals(binding.layoutPublic)){
                binding.radioPublic.setChecked(true);
                binding.radioPrivate.setChecked(false);
                binding.radioConnectedChannelCircle.setChecked(false);
                hideConnectedChannels();
            }else if(v.equals(binding.layoutPrivate)){
                binding.radioPublic.setChecked(false);
                binding.radioPrivate.setChecked(true);
                binding.radioConnectedChannelCircle.setChecked(false);
                hideConnectedChannels();
            }else if(v.equals(binding.layoutConnectedChannelCircle)){
                binding.radioPublic.setChecked(false);
                binding.radioPrivate.setChecked(false);
                binding.radioConnectedChannelCircle.setChecked(true);
                showConnectedChannels();
            }
            int currentCheckedPermission = getCurrentCheckedPermission();
            SharedPreferencesAccessor.BroadcastPref.saveAppBroadcastChannelPermission(this, new BroadcastChannelPermission(currentCheckedPermission, null));
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
        BroadcastChannelPermission appBroadcastChannelPermission = SharedPreferencesAccessor.BroadcastPref.getAppBroadcastChannelPermission(this);
        if(appBroadcastChannelPermission != null &&
                appBroadcastChannelPermission.getPermission() == currentCheckedPermission &&
                CollectionUtil.equals(appBroadcastChannelPermission.getExcludeConnectedChannels(), null)) { //TODO null
            return;
        }
        BroadcastChannelPermission serverBroadcastChannelPermission = SharedPreferencesAccessor.BroadcastPref.getServerBroadcastChannelPermission(this);
        ChangeBroadcastChannelPermissionPostBody postBody = new ChangeBroadcastChannelPermissionPostBody(currentCheckedPermission, null);
        PermissionApiCaller.changeBroadcastChannelPermission(null, postBody, new RetrofitApiCaller.BaseCommonYier<OperationStatus>(this.getApplicationContext()){

            @Override
            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(BroadcastChannelPermissionActivity.this, new int[]{}, () -> {
                    SharedPreferencesAccessor.BroadcastPref.saveAppBroadcastChannelPermission(BroadcastChannelPermissionActivity.this, new BroadcastChannelPermission(currentCheckedPermission, null));
                    SharedPreferencesAccessor.BroadcastPref.saveServerBroadcastChannelPermission(BroadcastChannelPermissionActivity.this, new BroadcastChannelPermission(currentCheckedPermission, null));
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