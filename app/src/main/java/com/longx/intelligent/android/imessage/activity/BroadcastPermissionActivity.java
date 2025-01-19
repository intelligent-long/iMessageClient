package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.BroadcastPermissionLinearLayoutViews;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Broadcast;
import com.longx.intelligent.android.imessage.data.BroadcastPermission;
import com.longx.intelligent.android.imessage.data.ChannelAssociation;
import com.longx.intelligent.android.imessage.data.request.ChangeBroadcastPermissionPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.databinding.ActivityBroadcastPermissionBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.PermissionApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.yier.BroadcastUpdateYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastPermissionActivity extends BaseActivity {
    private ActivityBroadcastPermissionBinding binding;
    private BroadcastPermission broadcastPermission;
    private BroadcastPermissionLinearLayoutViews linearLayoutViews;
    private boolean isChangePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intentData();
        if(isChangePermission){
            setupDefaultBackNavigation(binding.toolbar);
        }else {
            setupCloseBackNavigation(binding.toolbar);
        }
        init();
        showContent();
        setupYiers();
    }

    private void init() {
        linearLayoutViews = new BroadcastPermissionLinearLayoutViews(this, binding.linearLayoutViews, binding.scrollView, new HashSet<>(broadcastPermission.getExcludeConnectedChannels()));
    }

    private void intentData() {
        broadcastPermission = getIntent().getParcelableExtra(ExtraKeys.BROADCAST_PERMISSION);
        isChangePermission = getIntent().getBooleanExtra(ExtraKeys.CHANGE_PERMISSION, false);
    }

    private void showContent() {
        if(isChangePermission){
            binding.layoutChangeButton.setVisibility(View.VISIBLE);
        }
        showRadioButtonChecks(broadcastPermission);
        List<ChannelAssociation> associations = ChannelDatabaseManager.getInstance().findAllAssociations();
        List<BroadcastPermissionLinearLayoutViews.ItemData> itemDataList = new ArrayList<>();
        associations.forEach(association -> {
            itemDataList.add(new BroadcastPermissionLinearLayoutViews.ItemData(association.getChannel()));
        });
        itemDataList.sort((o1, o2) -> {
            if (o1.getIndexChar() == '#') return 1;
            if (o2.getIndexChar() == '#') return -1;
            return Character.compare(o1.getIndexChar(), o2.getIndexChar());
        });
        linearLayoutViews.addItemsAndShow(itemDataList);
    }

    private void setupYiers() {
        View.OnClickListener yier = v -> {
            if(v.equals(binding.layoutPublic)){
                broadcastPermission.setPermission(BroadcastPermission.PUBLIC);
                binding.radioPublic.setChecked(true);
                binding.radioPrivate.setChecked(false);
                binding.radioConnectedChannelCircle.setChecked(false);
                hideConnectedChannels();
            }else if(v.equals(binding.layoutPrivate)){
                broadcastPermission.setPermission(BroadcastPermission.PRIVATE);
                binding.radioPublic.setChecked(false);
                binding.radioPrivate.setChecked(true);
                binding.radioConnectedChannelCircle.setChecked(false);
                hideConnectedChannels();
            }else if(v.equals(binding.layoutConnectedChannelCircle)){
                broadcastPermission.setPermission(BroadcastPermission.CONNECTED_CHANNEL_CIRCLE);
                binding.radioPublic.setChecked(false);
                binding.radioPrivate.setChecked(false);
                binding.radioConnectedChannelCircle.setChecked(true);
                showConnectedChannels();
            }
            setResult();
        };
        binding.layoutPublic.setOnClickListener(yier);
        binding.layoutPrivate.setOnClickListener(yier);
        binding.layoutConnectedChannelCircle.setOnClickListener(yier);
        binding.changeButton.setOnClickListener(v -> {
            PermissionApiCaller.changeBroadcastPermission(this, new ChangeBroadcastPermissionPostBody(broadcastPermission), new RetrofitApiCaller.CommonYier<OperationData>(this) {
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(BroadcastPermissionActivity.this, new int[]{-101}, () -> {
                        Broadcast broadcast = data.getData(Broadcast.class);
                        GlobalYiersHolder.getYiers(BroadcastUpdateYier.class).ifPresent(broadcastUpdateYiers -> {
                            broadcastUpdateYiers.forEach(broadcastUpdateYier -> broadcastUpdateYier.updateOneBroadcast(broadcast));
                        });
                        finish();
                    });
                }
            });
        });
    }

    private void showRadioButtonChecks(BroadcastPermission broadcastPermission) {
        switch (broadcastPermission.getPermission()){
            case BroadcastPermission.PUBLIC:{
                binding.radioPublic.setChecked(true);
                hideConnectedChannels();
                break;
            }
            case BroadcastPermission.PRIVATE:{
                binding.radioPrivate.setChecked(true);
                hideConnectedChannels();
                break;
            }
            case BroadcastPermission.CONNECTED_CHANNEL_CIRCLE:{
                binding.radioConnectedChannelCircle.setChecked(true);
                showConnectedChannels();
                break;
            }
        }
    }

    private void showConnectedChannels(){
        binding.linearLayoutViews.setVisibility(View.VISIBLE);
    }

    private void hideConnectedChannels(){
        binding.linearLayoutViews.setVisibility(View.GONE);
    }

    public ActivityBroadcastPermissionBinding getBinding() {
        return binding;
    }

    public BroadcastPermission getBroadcastPermission() {
        return broadcastPermission;
    }

    public void setResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ExtraKeys.BROADCAST_PERMISSION, broadcastPermission);
        setResult(RESULT_OK, resultIntent);
    }
}