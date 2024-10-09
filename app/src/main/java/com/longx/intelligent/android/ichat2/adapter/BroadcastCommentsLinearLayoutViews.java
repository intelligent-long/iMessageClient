package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

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
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemBroadcastCommentBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.popupwindow.BroadcastCommentActionsPopupWindow;
import com.longx.intelligent.android.ichat2.popupwindow.BroadcastToCommentPopupWindow;
import com.longx.intelligent.android.ichat2.ui.LinearLayoutViews;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.yier.BroadcastUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.lang.Filter;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/9/25 at 下午2:27.
 */
public class BroadcastCommentsLinearLayoutViews extends LinearLayoutViews<BroadcastComment> {
    private int lastReplyIndex = -1;

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
        binding.name.setText(broadcastComment.getFromNameIncludeNote());
        binding.time.setText(TimeUtil.formatShortRelativeTime(broadcastComment.getCommentTime()));
        showText(broadcastComment, binding, activity);

        setupYiers(binding, broadcastComment, (BroadcastActivity) activity);
        return binding.getRoot();
    }

    private void showText(BroadcastComment broadcastComment, RecyclerItemBroadcastCommentBinding binding, Activity activity) {
        if(broadcastComment.getToCommentId() == null) {
            binding.text.setText(broadcastComment.getText());
        }else {
            String toUserSpan = "@" + broadcastComment.getToComment().getFromNameIncludeNote() + " ";
            SpannableString spannableString = new SpannableString(toUserSpan + broadcastComment.getText());
            ClickableSpan userMentionClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(activity, ChannelActivity.class);
                    intent.putExtra(ExtraKeys.ICHAT_ID, broadcastComment.getToComment().getFromId());
                    activity.startActivity(intent);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(activity.getColor(R.color.ichat));
                }
            };
            spannableString.setSpan(userMentionClickableSpan, 0, toUserSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.text.setMovementMethod(LinkMovementMethod.getInstance());
            binding.text.setText(spannableString);
        }
    }

    private void setupYiers(RecyclerItemBroadcastCommentBinding binding, BroadcastComment broadcastComment, BroadcastActivity broadcastActivity) {
        binding.avatar.setOnClickListener(v -> {
            Intent intent = new Intent(broadcastActivity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.ICHAT_ID, broadcastComment.getFromId());
            broadcastActivity.startActivity(intent);
        });
        View.OnLongClickListener longClickYier = v -> {
            new BroadcastCommentActionsPopupWindow(broadcastActivity, broadcastComment)
                    .setDeleteYier(v1 -> {
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
                    })
                    .setViewToCommentYier(v1 -> {
                        new BroadcastToCommentPopupWindow(broadcastActivity, broadcastComment.getToComment())
                                .show(binding.getRoot());
                    })
                    .show(binding.getRoot());
            return true;
        };
        binding.getRoot().setOnLongClickListener(longClickYier);
        binding.text.setOnLongClickListener(longClickYier);
        binding.avatar.setOnLongClickListener(longClickYier);
        binding.reply.setOnLongClickListener(longClickYier);
        binding.reply.setOnClickListener(v -> {
            if(lastReplyIndex != -1){
                getLinearLayout().getChildAt(lastReplyIndex).setBackgroundColor(ColorUtil.getColor(broadcastActivity, R.color.transparent));
            }
            lastReplyIndex = getAllItems().indexOf(broadcastComment);
            binding.getRoot().setBackgroundColor(ColorUtil.getAttrColor(broadcastActivity, com.google.android.material.R.attr.colorSurfaceContainerLow));
            broadcastActivity.startReply(broadcastComment, results -> binding.getRoot().setBackgroundColor(ColorUtil.getColor(broadcastActivity, R.color.transparent)));
        });
    }
}
