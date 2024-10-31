package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.request.RequestAddChannelPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityRequestAddChannelBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.dialog.MessageDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.value.Variables;
import com.longx.intelligent.android.ichat2.yier.TextChangedYier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class RequestAddChannelActivity extends BaseActivity {
    private ActivityRequestAddChannelBinding binding;
    private Channel channel;
    private Self currentUserInfo;
    private ActivityResultLauncher<Intent> resultLauncher;
    private ArrayList<ChannelTag> presetChannelTags = new ArrayList<>();
    private ArrayList<String> newChannelTagNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestAddChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        channel = getIntent().getParcelableExtra(ExtraKeys.CHANNEL);
        currentUserInfo = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
        showContent();
        setupYiers();
        registerForActivityResult();
    }

    private void showContent() {
        binding.messageInput.setText(Variables.getRequestAddChannelDefaultMessage(currentUserInfo.getUsername()));
        binding.noteInput.setText(channel.getNote());
    }

    private void registerForActivityResult(){
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        ArrayList<Parcelable> parcelableArrayListExtra = data.getParcelableArrayListExtra(ExtraKeys.CHANNEL_TAGS);
                        presetChannelTags = Utils.parseParcelableArray(parcelableArrayListExtra);
                        newChannelTagNames = data.getStringArrayListExtra(ExtraKeys.CHANNEL_TAG_NAMES);
                    }
                });
    }

    private void setupYiers() {
        binding.sendRequestButton.setOnClickListener(v -> {
            new ConfirmDialog(this, "是否发送添加频道请求？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        String inputtedMessage = UiUtil.getEditTextString(binding.messageInput);
                        String inputtedNote = UiUtil.getEditTextString(binding.noteInput);
                        if(inputtedNote.isEmpty()) inputtedNote = null;
                        List<String> presetChannelTagIds = new ArrayList<>();
                        presetChannelTags.forEach(presetChannelTag -> presetChannelTagIds.add(presetChannelTag.getTagId()));
                        RequestAddChannelPostBody postBody = new RequestAddChannelPostBody(channel.getIchatIdUser(), inputtedMessage, inputtedNote, newChannelTagNames, presetChannelTagIds);
                        ChannelApiCaller.requestAddChannel(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                super.ok(data, raw, call);
                                data.commonHandleResult(RequestAddChannelActivity.this, new int[]{-101, -102, -103, -104}, () -> {
                                    new MessageDialog(RequestAddChannelActivity.this, "发送请求", "已发送添加频道请求").create().show();
                                });
                            }
                        });
                    })
                    .create().show();
        });
        binding.clickViewPresettingTag.setOnClickListener(v -> {
            Intent intent = new Intent(this, PresettingChannelTagActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL, channel);
            intent.putExtra(ExtraKeys.CHANNEL_TAGS, presetChannelTags);
            intent.putExtra(ExtraKeys.CHANNEL_TAG_NAMES, newChannelTagNames);
            resultLauncher.launch(intent);
        });
        showOrHideNoteHint(binding.noteInput.getText());
        showOrHideMessageHint(binding.messageInput.getText());
        binding.noteInput.addTextChangedListener(new TextChangedYier() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showOrHideNoteHint(s);
            }
        });
        binding.messageInput.addTextChangedListener(new TextChangedYier() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showOrHideMessageHint(s);
            }
        });
    }

    private void showOrHideNoteHint(CharSequence s) {
        if (s.length() > 0) {
            binding.noteInput.setHint("");
        } else {
            binding.noteInput.setHint("备注");
        }
    }

    private void showOrHideMessageHint(CharSequence s) {
        if (s.length() > 0) {
            binding.messageInput.setHint("");
        } else {
            binding.messageInput.setHint("消息");
        }
    }
}