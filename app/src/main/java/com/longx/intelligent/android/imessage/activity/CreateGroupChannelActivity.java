package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.data.request.CreateGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityCreateGroupChannelBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.yier.TextChangedYier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class CreateGroupChannelActivity extends BaseActivity {
    private ActivityCreateGroupChannelBinding binding;
    private ActivityResultLauncher<Intent> resultLauncher;
    private ArrayList<GroupChannelTag> presetGroupChannelTags = new ArrayList<>();
    private ArrayList<String> newGroupChannelTagNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        showContent();
        setupYiers();
        registerForActivityResult();
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

    private void showContent() {

    }

    private void setupYiers() {
        showOrHideNameHint(binding.nameInput.getText());
        showOrHideNoteHint(binding.noteInput.getText());
        binding.nameInput.addTextChangedListener(new TextChangedYier() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showOrHideNameHint(s);
            }
        });
        binding.noteInput.addTextChangedListener(new TextChangedYier() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showOrHideNoteHint(s);
            }
        });
        binding.clickViewPresettingTag.setOnClickListener(v -> {
            Intent intent = new Intent(this, PresettingGroupChannelTagActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL_TAGS, presetGroupChannelTags);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL_TAG_NAMES, newGroupChannelTagNames);
            resultLauncher.launch(intent);
        });
        binding.createGroupChannelButton.setOnClickListener(v -> {
            String inputtedName = UiUtil.getEditTextString(binding.nameInput);
            String inputtedNote = UiUtil.getEditTextString(binding.noteInput);
            if(inputtedName == null || inputtedName.isEmpty()){
                MessageDisplayer.autoShow(this, "请输入群频道名称", MessageDisplayer.Duration.SHORT);
                return;
            }
            if(inputtedNote == null || inputtedNote.isEmpty()) inputtedNote = null;
            String finalInputtedNote = inputtedNote;
            new ConfirmDialog(this, "是否创建群频道？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        List<String> presetGroupChannelTagIds = new ArrayList<>();
                        presetGroupChannelTags.forEach(presetChannelTag -> presetGroupChannelTagIds.add(presetChannelTag.getGroupTagId()));
                        CreateGroupChannelPostBody postBody = new CreateGroupChannelPostBody(inputtedName, finalInputtedNote, newGroupChannelTagNames, presetGroupChannelTagIds);
                        GroupChannelApiCaller.createGroupChannel(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                super.ok(data, raw, call);
                                data.commonHandleResult(CreateGroupChannelActivity.this, new int[]{}, () -> {
                                    new MessageDialog(CreateGroupChannelActivity.this, "已创建群频道").create().show();
                                });
                            }
                        });
                    })
                    .create().show();
        });
    }

    private void showOrHideNameHint(CharSequence s) {
        if (s.length() > 0) {
            binding.nameInput.setHint("");
        } else {
            binding.nameInput.setHint("名称");
        }
    }

    private void showOrHideNoteHint(CharSequence s) {
        if (s.length() > 0) {
            binding.noteInput.setHint("");
        } else {
            binding.noteInput.setHint("备注");
        }
    }
}