package com.longx.intelligent.android.ichat2.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.databinding.DialogForwardMessagesProcessingBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.procedure.GlideBehaviours;

/**
 * Created by LONG on 2024/10/20 at 下午11:55.
 */
public class ForwardMessagesProcessingDialog extends AbstractDialog{
    private DialogForwardMessagesProcessingBinding binding;

    public ForwardMessagesProcessingDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        return builder.create();
    }

    @Override
    protected View createView(LayoutInflater layoutInflater) {
        binding = DialogForwardMessagesProcessingBinding.inflate(layoutInflater);
        return binding.getRoot();
    }

    public void updateChannelInfo(String channelId){
        Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(channelId);
        String avatarHash = channel.getAvatar() == null ? null : channel.getAvatar().getHash();
        if (avatarHash == null) {
            GlideBehaviours.loadToImageView(getActivity().getApplicationContext(), R.drawable.default_avatar, binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(getActivity().getApplicationContext(), NetDataUrls.getAvatarUrl(getActivity(), avatarHash), binding.avatar);
        }
        binding.name.setText(channel.getName());
    }

    public void updateProgressIndicator(long current, long total){
        if(current == -1 || total == -1){
            binding.progressIndicator.setIndeterminate(true);
        }else {
            binding.progressIndicator.setIndeterminate(false);
            int progress = (int)((current / (double) total) * binding.progressIndicator.getMax());
            binding.progressIndicator.setProgress(progress, true);
        }
    }

    public void updateProgressText(int current, int total){
        binding.progressText.setText(current + " / " + total);
    }
}
