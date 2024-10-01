package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.BroadcastActivity;
import com.longx.intelligent.android.ichat2.activity.ChannelActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastComment;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemBroadcastCommentBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.ui.LinearLayoutViews;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.yier.BroadcastUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/9/25 at 下午2:27.
 */
public class BroadcastCommentsLinearLayoutViews extends LinearLayoutViews<BroadcastComment> {

    public BroadcastCommentsLinearLayoutViews(BroadcastActivity broadcastActivity, LinearLayout linearLayout) {
        super(broadcastActivity, linearLayout);
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
        if(broadcastComment.getReplyCount() > 0){
            binding.layoutGoToReply.setVisibility(View.VISIBLE);
            binding.goToReply.setText(broadcastComment.getReplyCount() + " 条回复");
        }
        setupYiers(binding, broadcastComment, (BroadcastActivity) activity);
        return binding.getRoot();
    }

    private void setupYiers(RecyclerItemBroadcastCommentBinding binding, BroadcastComment broadcastComment, BroadcastActivity broadcastActivity) {
        binding.avatar.setOnClickListener(v -> {
            Intent intent = new Intent(broadcastActivity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.ICHAT_ID, broadcastComment.getFromId());
            broadcastActivity.startActivity(intent);
        });
        binding.deleteComment.setOnClickListener(v -> {
            new ConfirmDialog(broadcastActivity)
                    .setNegativeButton(null)
                    .setPositiveButton(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BroadcastApiCaller.deleteBroadcastComment(broadcastActivity, broadcastComment.getCommentId(), new RetrofitApiCaller.CommonYier<OperationData>(broadcastActivity) {
                                @Override
                                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                                    super.ok(data, raw, call);
                                    data.commonHandleResult(broadcastActivity, new int[]{-101, -102}, () -> {
                                        Broadcast broadcast = data.getData(Broadcast.class);
                                        removeView(broadcastComment);
                                        GlobalYiersHolder.getYiers(BroadcastUpdateYier.class).ifPresent(broadcastUpdateYiers -> {
                                            broadcastUpdateYiers.forEach(broadcastUpdateYier -> {
                                                broadcastUpdateYier.updateOneBroadcast(broadcast);
                                            });
                                        });
                                    });
                                }
                            });
                        }
                    })
                    .forShow();
        });
        binding.reply.setOnClickListener(v -> {
            broadcastActivity.startReply(broadcastComment);
        });
    }
}
