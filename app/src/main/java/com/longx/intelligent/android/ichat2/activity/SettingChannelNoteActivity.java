package com.longx.intelligent.android.ichat2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.edituser.ChangeUsernameActivity;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.request.SetNoteToAssociatedChannelPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivitySettingChannelNoteBinding;
import com.longx.intelligent.android.ichat2.dialog.MessageDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class SettingChannelNoteActivity extends BaseActivity {
    private ActivitySettingChannelNoteBinding binding;
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingChannelNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        channel = getIntent().getParcelableExtra(ExtraKeys.CHANNEL);
        setupYiers();
    }

    private void setupYiers() {
        binding.doneButton.setOnClickListener(v -> {
            String inputtedNote = UiUtil.getEditTextString(binding.noteInput);
            SetNoteToAssociatedChannelPostBody postBody = new SetNoteToAssociatedChannelPostBody(channel.getIchatId(), inputtedNote);
            ChannelApiCaller.setNoteToAssociatedChannel(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                    super.ok(data, row, call);
                    data.commonHandleResult(SettingChannelNoteActivity.this, new int[]{-101}, () -> {
                        new MessageDialog(SettingChannelNoteActivity.this, "设置成功").show();
                    });
                }
            });
        });
    }
}