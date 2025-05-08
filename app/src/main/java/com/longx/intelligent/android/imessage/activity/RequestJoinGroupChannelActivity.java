package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.data.request.RequestAddGroupChannelPostBody;
import com.longx.intelligent.android.imessage.databinding.ActivityRequestJoinGroupChannelBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.value.Mutables;
import com.longx.intelligent.android.imessage.yier.TextChangedYier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestJoinGroupChannelActivity extends BaseActivity {
    private ActivityRequestJoinGroupChannelBinding binding;
    private GroupChannel groupChannel;
    private Self currentUserInfo;
    private ArrayList<GroupChannelTag> presetGroupChannelTags = new ArrayList<>();
    private ArrayList<String> newGroupChannelTagNames = new ArrayList<>();
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestJoinGroupChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        initData();
        showContent();
        registerForActivityResult();
        setupYiers();
    }

    private void initData() {
        groupChannel = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL);
        currentUserInfo = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
    }

    private void showContent() {
        binding.messageInput.setText(Mutables.getRequestJoinGroupChannelDefaultMessage(currentUserInfo.getUsername()));
    }

    private void registerForActivityResult(){
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        ArrayList<Parcelable> parcelableArrayListExtra = data.getParcelableArrayListExtra(ExtraKeys.GROUP_CHANNEL_TAGS);
                        presetGroupChannelTags = Utils.parseParcelableArray(parcelableArrayListExtra);
                        newGroupChannelTagNames = data.getStringArrayListExtra(ExtraKeys.GROUP_CHANNEL_TAG_NAMES);
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
                        if(inputtedNote == null || inputtedNote.isEmpty()) inputtedNote = null;
                        List<String> presetGroupChannelTagIds = new ArrayList<>();
                        presetGroupChannelTags.forEach(presetGroupChannelTag -> presetGroupChannelTagIds.add(presetGroupChannelTag.getTagId()));
                        RequestAddGroupChannelPostBody postBody = new RequestAddGroupChannelPostBody(groupChannel.getGroupChannelIdUser(), inputtedMessage, inputtedNote, newGroupChannelTagNames, presetGroupChannelTagIds);

                    })
                    .create().show();
        });
        binding.clickViewPresettingTag.setOnClickListener(v -> {
            Intent intent = new Intent(this, PresetGroupChannelTagActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL_TAGS, presetGroupChannelTags);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL_TAG_NAMES, newGroupChannelTagNames);
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