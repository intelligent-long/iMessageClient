package com.longx.intelligent.android.imessage.activity;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.ActivityInviteJoinGroupChannelBinding;
import com.longx.intelligent.android.imessage.dialog.ChooseOneChannelDialog;
import com.longx.intelligent.android.imessage.dialog.ChooseOneGroupChannelDialog;
import com.longx.intelligent.android.imessage.util.ErrorLogger;

import java.util.ArrayList;
import java.util.List;

public class InviteJoinGroupChannelActivity extends BaseActivity {
    private ActivityInviteJoinGroupChannelBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInviteJoinGroupChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        showContent();
        setupYiers();
    }

    private void showContent() {

    }

    private void setupYiers() {
        binding.chooseChannelButton.setOnClickListener(v -> {
            List<Channel> channels = new ArrayList<>();
            ChannelDatabaseManager.getInstance().findAllAssociations().forEach(channelAssociation -> {
                channels.add(channelAssociation.getChannel());
            });
            ChooseOneChannelDialog chooseOneChannelDialog = new ChooseOneChannelDialog(this, "选择频道", channels);
            chooseOneChannelDialog
                    .setPositiveButton("确定", (dialog, which) -> {
                        Channel choseChannel = chooseOneChannelDialog.getAdapter().getSelected();
                        ErrorLogger.log(choseChannel);
                    })
                    .setNegativeButton(null)
                    .create().show();
        });
        binding.chooseGroupChannelButton.setOnClickListener(v -> {
            List<GroupChannel> allAssociations = GroupChannelDatabaseManager.getInstance().findAllAssociations();
            ChooseOneGroupChannelDialog chooseOneGroupChannelDialog = new ChooseOneGroupChannelDialog(this, "选择群频道", allAssociations);
            chooseOneGroupChannelDialog
                    .setNegativeButton(null)
                    .setPositiveButton("确定", (dialog, which) -> {
                        GroupChannel choseGroupChannel = chooseOneGroupChannelDialog.getAdapter().getSelected();
                        ErrorLogger.log(choseGroupChannel);
                    })
                    .create().show();

        });
    }
}