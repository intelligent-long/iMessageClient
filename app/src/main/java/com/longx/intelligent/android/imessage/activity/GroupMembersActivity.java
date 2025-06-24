package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.GroupMembersRecyclerAdapter;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelAssociation;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupMembersBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.ErrorLogger;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GroupMembersActivity extends BaseActivity {
    private ActivityGroupMembersBinding binding;
    private String groupId;
    private List<GroupChannelAssociation> groupChannelAssociations;
    private GroupChannel groupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupMembersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        start();
    }

    private void start() {
        groupId = getIntent().getStringExtra(ExtraKeys.GROUP_CHANNEL_ID);
        try {
            groupChannel = GroupChannelDatabaseManager.getInstance().findOneAssociation(groupId);
            groupChannelAssociations = groupChannel.getGroupChannelAssociations();
            showContent();
        }catch (Exception e){
            GroupChannelApiCaller.findGroupChannelByGroupChannelId(null, groupId, "id", false, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this) {
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleSuccessResult(() -> {
                        groupChannel = data.getData(GroupChannel.class);
                        groupChannelAssociations = groupChannel.getGroupChannelAssociations();
                        showContent();
                    });
                }
            });
        }
    }

    public ActivityGroupMembersBinding getBinding() {
        return binding;
    }

    private void showContent() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<GroupMembersRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        groupChannelAssociations.forEach(groupChannelAssociation -> {
            itemDataList.add(new GroupMembersRecyclerAdapter.ItemData(groupChannelAssociation.getRequester()));
        });
        GroupMembersRecyclerAdapter adapter = new GroupMembersRecyclerAdapter(this, itemDataList, groupChannel);
        adapter.setOnItemClickYier((position, data) -> {
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra(ExtraKeys.IMESSAGE_ID, data.getChannel().getImessageId());
            intent.putExtra(ExtraKeys.CHANNEL, data.getChannel());
            startActivity(intent);
        });
        binding.recyclerView.setAdapter(adapter);
    }
}