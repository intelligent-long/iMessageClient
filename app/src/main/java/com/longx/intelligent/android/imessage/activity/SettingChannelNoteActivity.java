package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.request.SetNoteToAssociatedChannelPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivitySettingChannelNoteBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;

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
        String channelImessageId = getIntent().getStringExtra(ExtraKeys.IMESSAGE_ID);
        channel = ChannelDatabaseManager.getInstance().findOneChannel(channelImessageId);
        binding.noteInput.setText(channel.getNote());
        if (channel.getNote() == null) binding.deleteButton.setVisibility(View.GONE);
        setupYiers();
    }

    private void setupYiers() {
        binding.doneButton.setOnClickListener(v -> {
            String inputtedNote = UiUtil.getEditTextString(binding.noteInput);
            SetNoteToAssociatedChannelPostBody postBody = new SetNoteToAssociatedChannelPostBody(channel.getImessageId(), inputtedNote);
            ChannelApiCaller.setNoteToAssociatedChannel(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(SettingChannelNoteActivity.this, new int[]{-101, -102}, () -> {
                        binding.noteInput.setText(inputtedNote);
                        binding.deleteButton.setVisibility(View.VISIBLE);
                        new MessageDialog(SettingChannelNoteActivity.this, "设置成功").create().show();
                    });
                }
            });
        });
        binding.deleteButton.setOnClickListener(v -> {
            new ConfirmDialog(this, "是否继续？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        ChannelApiCaller.deleteNoteOfAssociatedChannel(this, channel.getImessageId(), new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                super.ok(data, raw, call);
                                data.commonHandleResult(SettingChannelNoteActivity.this, new int[]{-101}, () -> {
                                    binding.noteInput.setText(null);
                                    binding.deleteButton.setVisibility(View.GONE);
                                    new MessageDialog(SettingChannelNoteActivity.this, "已删除").create().show();
                                });
                            }
                        });
                    })
                    .create().show();
        });
    }
}