package com.longx.intelligent.android.imessage.activity.editgroup;

import android.os.Bundle;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelNamePostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityChangeGroupNameBinding;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class ChangeGroupNameActivity extends BaseActivity {
    private ActivityChangeGroupNameBinding binding;
    private GroupChannel groupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeGroupNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        setupYiers();
    }

    private void intentData() {
        groupChannel = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL);
        binding.groupNameInput.setText(groupChannel.getName());
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.change) {
                String inputtedGroupName = UiUtil.getEditTextString(binding.groupNameInput);
                ChangeGroupChannelNamePostBody postBody = new ChangeGroupChannelNamePostBody(groupChannel.getGroupChannelId(), inputtedGroupName);
                GroupChannelApiCaller.changeGroupName(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this) {
                    @Override
                    public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ChangeGroupNameActivity.this, new int[]{-101, -102, -103}, () -> {
                            new CustomViewMessageDialog(ChangeGroupNameActivity.this, "修改成功").create().show();
                        });
                    }
                });
            }
            return true;
        });
    }
}