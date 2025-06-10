package com.longx.intelligent.android.imessage.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelSettingBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GroupChannelSettingActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivityGroupChannelSettingBinding binding;
    private GroupChannel groupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        showContent();
        setupYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void intentData() {
        groupChannel = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL);
    }

    private void showContent() {
        if(!groupChannel.getOwner().equals(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this).getImessageId())){
            binding.clickViewGroupManage.setVisibility(View.GONE);
        }
    }

    private void setupYiers() {
        binding.clickViewNote.setOnClickListener(v -> {
            Intent intent = new Intent(this, SetGroupChannelNoteActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
            startActivity(intent);
        });
        binding.clickViewTag.setOnClickListener(v -> {
            Intent intent = new Intent(this, SetGroupChannelTagActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
            startActivity(intent);
        });
        binding.clickViewGroupManage.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupManagementActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
            startActivity(intent);
        });
        binding.disconnectChannel.setOnClickListener(v -> {
            if(groupChannel.getOwner().equals(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this).getImessageId())){
                MessageDisplayer.autoShow(this, "你是群主，无法退出群聊。请先转让群主身份再尝试退出。", MessageDisplayer.Duration.LONG);
            }else {
                new ConfirmDialog(this)
                        .setNegativeButton()
                        .setPositiveButton((dialog, which) -> {
                            GroupChannelApiCaller.disconnect(this, groupChannel.getGroupChannelId(), new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                                @Override
                                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                    super.ok(data, raw, call);
                                    data.commonHandleResult(getActivity(), new int[]{-101}, () -> {
                                        MessageDisplayer.showToast(getActivity(), "已退出", Toast.LENGTH_SHORT);
                                        Intent intent = new Intent();
                                        intent.putExtra(ExtraKeys.TRUE, true);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    });
                                }
                            });
                        })
                        .create().show();
            }
        });
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds, Object... objects) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds, Object... objects) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNEL) && objects[0].equals(groupChannel.getGroupChannelId())){
            groupChannel = GroupChannelDatabaseManager.getInstance().findOneAssociation(groupChannel.getGroupChannelId());
            showContent();
        }
    }
}