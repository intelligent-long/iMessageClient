package com.longx.intelligent.android.imessage.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.request.target.Target;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.databinding.DialogForwardMessagesProcessingBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;

/**
 * Created by LONG on 2024/10/20 at 下午11:55.
 */
public class ForwardMessagesProcessingDialog extends AbstractDialog{
    private DialogForwardMessagesProcessingBinding binding;

    public ForwardMessagesProcessingDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected AlertDialog onCreate(MaterialAlertDialogBuilder builder) {
        return builder.create();
    }

    @Override
    protected View onCreateView(LayoutInflater layoutInflater) {
        binding = DialogForwardMessagesProcessingBinding.inflate(layoutInflater);
        return binding.getRoot();
    }

    public void updateChannelInfo(String channelId){
        Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(channelId);
        String avatarHash = channel.getAvatar() == null ? null : channel.getAvatar().getHash();
        if (avatarHash == null) {
            GlideApp
                    .with(getActivity().getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(binding.avatar);
        } else {
            GlideApp
                    .with(getActivity().getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(getActivity(), avatarHash))
                    .into(binding.avatar);
        }
        binding.name.setText(channel.autoGetName());
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
