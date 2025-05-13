package com.longx.intelligent.android.imessage.activity.editgroup;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.edituser.ChangeImessageIdUserActivity;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelIdUserPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityChangeGroupIdBinding;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class ChangeGroupIdActivity extends BaseActivity {
    private ActivityChangeGroupIdBinding binding;
    private GroupChannel groupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeGroupIdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        checkNowCanChange();
        setupYiers();
    }

    private void intentData() {
        groupChannel = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL);
        binding.groupIdUserInput.setText(groupChannel.getGroupChannelIdUser());
    }

    private void checkNowCanChange() {
        GroupChannelApiCaller.groupChannelIdUserNowCanChange(null, groupChannel.getGroupChannelId(), new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this, 500L) {
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(ChangeGroupIdActivity.this, new int[]{-101, -102}, null,
                        new OperationStatus.HandleResult(101, () -> {
                            String desc = data.getMessage();
                            binding.desc.setText(desc);
                            binding.content.setVisibility(View.VISIBLE);
                            UiUtil.setIconMenuEnabled(binding.toolbar.getMenu().findItem(R.id.change), true);
                        }),
                        new OperationStatus.HandleResult(102, () -> {
                            String desc = data.getMessage();
                            binding.desc.setText(desc);
                            binding.groupIdUserLayout.setVisibility(View.GONE);
                            binding.content.setVisibility(View.VISIBLE);
                        })
                );
            }
        });
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.change) {
                String inputtedGroupChannelIdUser = UiUtil.getEditTextString(binding.groupIdUserInput);
                ChangeGroupChannelIdUserPostBody postBody = new ChangeGroupChannelIdUserPostBody(groupChannel.getGroupChannelId(), inputtedGroupChannelIdUser);
                GroupChannelApiCaller.changeGroupChannelIdUser(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                    @Override
                    public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ChangeGroupIdActivity.this, new int[]{-101, -102, -103, -104, -105, -106}, () -> {
                            new CustomViewMessageDialog(ChangeGroupIdActivity.this, "修改成功").create().show();
                        });
                    }
                });
            }
            return true;
        });
    }

}