package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelJoinVerificationPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupManagementBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class GroupManagementActivity extends BaseActivity {
    private ActivityGroupManagementBinding binding;
    private GroupChannel groupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupManagementBinding.inflate(getLayoutInflater());
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
        binding.switchJoinVerification.setChecked(groupChannel.getGroupJoinVerification() != null && groupChannel.getGroupJoinVerification());
    }

    private void setupYiers() {
        binding.clickViewJoinVerification.setOnClickListener(v -> {
            UiUtil.setViewGroupEnabled(binding.clickViewJoinVerification, false, true);
            boolean changeTo = !binding.switchJoinVerification.isChecked();
            ChangeGroupChannelJoinVerificationPostBody postBody = new ChangeGroupChannelJoinVerificationPostBody(groupChannel.getGroupChannelId(), changeTo);
            GroupChannelApiCaller.changeGroupJoinVerification(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(getActivity(), new int[]{-101, -102, -103}, () -> {
                        binding.switchJoinVerification.setChecked(changeTo);
                    });
                }

                @Override
                public synchronized void complete(Call<OperationStatus> call) {
                    super.complete(call);
                    UiUtil.setViewGroupEnabled(binding.clickViewJoinVerification, true, true);
                }
            });
        });
        binding.clickViewTransferGroupChannelAdmin.setOnClickListener(v -> {

        });
        binding.disbandGroupChannel.setOnClickListener(v -> {

        });
    }
}