package com.longx.intelligent.android.imessage.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.TransferGroupChannelAdminRecyclerAdapter;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.request.TransferGroupChannelManagerPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityTransferGroupChannelAdminBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class TransferGroupChannelAdminActivity extends BaseActivity {
    private ActivityTransferGroupChannelAdminBinding binding;
    private GroupChannel groupChannel;
    private TransferGroupChannelAdminRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferGroupChannelAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        showContent();
        setupYiers();
    }

    private void intentData() {
        groupChannel = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL);
    }

    private void showContent() {
        List<Channel> associatedChannels = new ArrayList<>();
        groupChannel.getGroupChannelAssociations().forEach(groupChannelAssociation -> {
            if(!groupChannelAssociation.getRequester().getImessageId().equals(groupChannel.getOwner())) {
                associatedChannels.add(groupChannelAssociation.getRequester());
            }
        });
        if(associatedChannels.isEmpty()){
            ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                    .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        }else {
            ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                    .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            binding.noContentLayout.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            adapter = new TransferGroupChannelAdminRecyclerAdapter(this, associatedChannels);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setAdapter(adapter);
        }
    }

    public ActivityTransferGroupChannelAdminBinding getBinding() {
        return binding;
    }

    private void setupYiers() {
        binding.toolbar.getMenu().findItem(R.id.transfer_group_channel_admin).setOnMenuItemClickListener(item -> {
            new ConfirmDialog(this)
                    .setNegativeButton()
                    .setPositiveButton(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Channel selected = adapter.getSelected();
                            TransferGroupChannelManagerPostBody postBody = new TransferGroupChannelManagerPostBody(groupChannel.getGroupChannelId(), selected.getImessageId());
                            GroupChannelApiCaller.transferGroupChannelManager(TransferGroupChannelAdminActivity.this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(TransferGroupChannelAdminActivity.this){
                                @Override
                                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                    super.ok(data, raw, call);
                                    data.commonHandleResult(TransferGroupChannelAdminActivity.this, new int[]{-101, -102, -103, -104, -105}, () -> {
                                        new CustomViewMessageDialog(TransferGroupChannelAdminActivity.this, "已发送移交请求。")
                                                .create().show()
                                                .setOnDismissListener(dialog -> finish());
                                    });
                                }
                            });
                        }
                    })
                    .create().show();
            return true;
        });
    }
}