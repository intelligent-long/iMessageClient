package com.longx.intelligent.android.imessage.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.GroupMembersRecyclerAdapter;
import com.longx.intelligent.android.imessage.adapter.RemoveGroupChannelRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelAssociation;
import com.longx.intelligent.android.imessage.data.request.ManageGroupChannelDisconnectPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelRemoveBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.ErrorLogger;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GroupChannelRemoveActivity extends BaseActivity {
    private ActivityGroupChannelRemoveBinding binding;
    private List<GroupChannelAssociation> groupChannelAssociations;
    private RemoveGroupChannelRecyclerAdapter adapter;
    private String groupId;
    private GroupChannel groupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelRemoveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        showContent();
        setupYiers();
    }

    private void intentData() {
        groupChannel = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL);
        groupId = groupChannel.getGroupChannelId();
    }

    private void showContent() {
        groupChannelAssociations = GroupChannelDatabaseManager.getInstance().findOneAssociation(groupId).getGroupChannelAssociations();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<RemoveGroupChannelRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        groupChannelAssociations.forEach(groupChannelAssociation -> {
            itemDataList.add(new RemoveGroupChannelRecyclerAdapter.ItemData(groupChannelAssociation.getRequester()));
        });
        adapter = new RemoveGroupChannelRecyclerAdapter(this, itemDataList, groupChannel);
        adapter.setOnItemClickYier((position, data) -> {
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra(ExtraKeys.IMESSAGE_ID, data.getChannel().getImessageId());
            intent.putExtra(ExtraKeys.CHANNEL, data.getChannel());
            startActivity(intent);
        });
        binding.recyclerView.setAdapter(adapter);
    }

    public ActivityGroupChannelRemoveBinding getBinding() {
        return binding;
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.remove) {
                new ConfirmDialog(this)
                        .setNegativeButton()
                        .setPositiveButton((dialog, which) -> {
                            List<Channel> checkedChannel = adapter.getCheckedChannel();
                            List<String> channelIds = new ArrayList<>();
                            checkedChannel.forEach(channel -> {
                                channelIds.add(channel.getImessageId());
                            });
                            ManageGroupChannelDisconnectPostBody postBody = new ManageGroupChannelDisconnectPostBody(channelIds);
                            GroupChannelApiCaller.manageGroupChannelDisconnectChannel(this, groupId, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this) {
                                @Override
                                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                    super.ok(data, raw, call);
                                    data.commonHandleResult(GroupChannelRemoveActivity.this, new int[]{-101, -102, -103}, () -> {
                                        MessageDisplayer.showToast(GroupChannelRemoveActivity.this, "已移除", Toast.LENGTH_SHORT);
                                        finish();
                                    });
                                }
                            });
                        })
                        .create().show();
            }
            return true;
        });
    }
}