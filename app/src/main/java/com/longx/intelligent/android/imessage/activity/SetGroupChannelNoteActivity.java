package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.request.SetNoteToAssociatedGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivitySetGroupChannelNoteBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class SetGroupChannelNoteActivity extends BaseActivity {
    private ActivitySetGroupChannelNoteBinding binding;
    private GroupChannel groupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetGroupChannelNoteBinding.inflate(getLayoutInflater());
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
        binding.noteInput.setText(groupChannel.getNote());
        if (groupChannel.getNote() == null) binding.toolbar.getMenu().findItem(R.id.delete).setVisible(false);
    }

    private void setupYiers() {
        binding.doneButton.setOnClickListener(v -> {
            String inputtedNote = UiUtil.getEditTextString(binding.noteInput);
            SetNoteToAssociatedGroupChannelPostBody postBody = new SetNoteToAssociatedGroupChannelPostBody(groupChannel.getGroupChannelId(), inputtedNote);
            GroupChannelApiCaller.setNoteToAssociatedGroupChannel(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(SetGroupChannelNoteActivity.this, new int[]{-101, -102}, () -> {
                        binding.noteInput.setText(inputtedNote);
                        binding.toolbar.getMenu().findItem(R.id.delete).setVisible(true);
                        new CustomViewMessageDialog(SetGroupChannelNoteActivity.this, "设置成功").create().show();
                    });
                }
            });
        });
        binding.toolbar.getMenu().findItem(R.id.delete).setOnMenuItemClickListener(item -> {
            new ConfirmDialog(this, "是否继续？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        GroupChannelApiCaller.deleteNoteOfAssociatedGroupChannel(this, groupChannel.getGroupChannelId(), new RetrofitApiCaller.CommonYier<OperationStatus>(this) {
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                super.ok(data, raw, call);
                                data.commonHandleResult(SetGroupChannelNoteActivity.this, new int[]{-101}, () -> {
                                    binding.noteInput.setText(null);
                                    binding.toolbar.getMenu().findItem(R.id.delete).setVisible(false);
                                    new CustomViewMessageDialog(SetGroupChannelNoteActivity.this, "已删除").create().show();
                                });
                            }
                        });
                    })
                    .create().show();
            return false;
        });
    }
}