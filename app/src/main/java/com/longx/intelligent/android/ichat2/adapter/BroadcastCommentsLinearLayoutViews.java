package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ChannelActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.BroadcastComment;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemBroadcastCommentBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.ui.LinearLayoutViews;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;

/**
 * Created by LONG on 2024/9/25 at 下午2:27.
 */
public class BroadcastCommentsLinearLayoutViews extends LinearLayoutViews<BroadcastComment> {

    public BroadcastCommentsLinearLayoutViews(Activity activity, LinearLayout linearLayout) {
        super(activity, linearLayout);
    }

    @Override
    public View getView(BroadcastComment broadcastComment, Activity activity) {
        RecyclerItemBroadcastCommentBinding binding = RecyclerItemBroadcastCommentBinding.inflate(activity.getLayoutInflater());
        if (broadcastComment.getAvatarHash() == null) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(binding.avatar);
        } else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(activity, broadcastComment.getAvatarHash()))
                    .into(binding.avatar);
        }
        Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(broadcastComment.getFromId());
        String name;
        if(channel != null){
            name = channel.getName();
        }else {
            name = broadcastComment.getFromName();
        }
        binding.name.setText(name);
        binding.time.setText(TimeUtil.formatRelativeTime(broadcastComment.getCommentTime()));
        binding.text.setText(broadcastComment.getText());
        String currentUserIchatId = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity).getIchatId();
        if(currentUserIchatId.equals(broadcastComment.getFromId())){
            binding.layoutDeleteComment.setVisibility(View.VISIBLE);
            UiUtil.setViewWidth(binding.space, UiUtil.dpToPx(activity, 15));
        }else {
            binding.layoutDeleteComment.setVisibility(View.GONE);
            UiUtil.setViewWidth(binding.space, UiUtil.dpToPx(activity, 21));
        }
        setupYiers(binding, broadcastComment, activity);
        return binding.getRoot();
    }

    private void setupYiers(RecyclerItemBroadcastCommentBinding binding, BroadcastComment broadcastComment, Activity activity) {
        binding.avatar.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.ICHAT_ID, broadcastComment.getFromId());
            activity.startActivity(intent);
        });
    }
}
