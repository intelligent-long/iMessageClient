package com.longx.intelligent.android.ichat2.activity;

import android.app.KeyguardManager;
import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastChannelPermissionLinearLayoutViews;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.BroadcastChannelPermission;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastChannelPermissionBinding;
import com.longx.intelligent.android.ichat2.databinding.LinearLayoutViewsFooterBroadcastChannelPermissionBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.PermissionApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastChannelPermissionActivity extends BaseActivity {
    private ActivityBroadcastChannelPermissionBinding binding;
    private BroadcastChannelPermissionLinearLayoutViews linearLayoutViews;
    private LinearLayoutViewsFooterBroadcastChannelPermissionBinding footerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastChannelPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        init();
        showContent();
        setupYiers();
    }

    private void init() {
        linearLayoutViews = new BroadcastChannelPermissionLinearLayoutViews(this, binding.linearLayoutViews, binding.scrollView);
        footerBinding = LinearLayoutViewsFooterBroadcastChannelPermissionBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        linearLayoutViews.setFooter(footerBinding.getRoot());
    }

    private void showContent() {
        BroadcastChannelPermission broadcastChannelPermission = SharedPreferencesAccessor.BroadcastPref.getAppBroadcastChannelPermission(this);
        if(broadcastChannelPermission == null){
            UiUtil.setViewGroupEnabled(binding.scrollView, false, true);
            PermissionApiCaller.fetchBroadcastChannelPermission(this, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this){
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(BroadcastChannelPermissionActivity.this, new int[]{}, () -> {
                        BroadcastChannelPermission broadcastChannelPermissionFetched = data.getData(BroadcastChannelPermission.class);
                        UiUtil.setViewGroupEnabled(binding.scrollView, true, true);
                        showRadioButtonChecks(broadcastChannelPermissionFetched);
                        SharedPreferencesAccessor.BroadcastPref.saveAppBroadcastChannelPermission(BroadcastChannelPermissionActivity.this, broadcastChannelPermissionFetched);
                        SharedPreferencesAccessor.BroadcastPref.saveServerBroadcastChannelPermission(BroadcastChannelPermissionActivity.this, broadcastChannelPermissionFetched);
                    });
                }
            });
        }else {
            showRadioButtonChecks(broadcastChannelPermission);
        }
    }

    private void showRadioButtonChecks(BroadcastChannelPermission broadcastChannelPermission) {
        switch (broadcastChannelPermission.getPermission()){
            case BroadcastChannelPermission.PUBLIC:{
                binding.radioPublic.setChecked(true);
                break;
            }
            case BroadcastChannelPermission.PRIVATE:{
                binding.radioPrivate.setChecked(true);
                break;
            }
            case BroadcastChannelPermission.CONNECTED_CHANNEL_CIRCLE:{
                binding.radioConnectedChannelCircle.setChecked(true);
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
            }else if(v.equals(binding.layoutPrivate)){
                binding.radioPublic.setChecked(false);
                binding.radioPrivate.setChecked(true);
                binding.radioConnectedChannelCircle.setChecked(false);
            }else if(v.equals(binding.layoutConnectedChannelCircle)){
                binding.radioPublic.setChecked(false);
                binding.radioPrivate.setChecked(false);
                binding.radioConnectedChannelCircle.setChecked(true);
            }
        };
        binding.layoutPublic.setOnClickListener(yier);
        binding.layoutPrivate.setOnClickListener(yier);
        binding.layoutConnectedChannelCircle.setOnClickListener(yier);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateServerData();
    }

    private void updateServerData() {

    }
}