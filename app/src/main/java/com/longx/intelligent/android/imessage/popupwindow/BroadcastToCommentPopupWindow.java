package com.longx.intelligent.android.imessage.popupwindow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ChannelActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.data.BroadcastComment;
import com.longx.intelligent.android.imessage.databinding.PopupWindowBroadcastToCommentBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemBroadcastCommentBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.TimeUtil;

/**
 * Created by LONG on 2024/10/8 at 下午4:26.
 */
public class BroadcastToCommentPopupWindow {
    private final AppCompatActivity activity;
    private final PopupWindow popupWindow;
    private final PopupWindowBroadcastToCommentBinding binding;
    private final BroadcastComment toComment;
    private View.OnClickListener onContentClickYier;
    private boolean skipContentClick;

    public BroadcastToCommentPopupWindow(AppCompatActivity activity, BroadcastComment toComment) {
        this.activity = activity;
        this.toComment = toComment;
        binding = PopupWindowBroadcastToCommentBinding.inflate(activity.getLayoutInflater());
        showContent();
        popupWindow = new PopupWindow(binding.getRoot(),  ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT, true);
        setupYiers();
    }

    private void showContent() {
        if (toComment.getAvatarHash() == null) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(binding.avatar);
        } else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(activity, toComment.getAvatarHash()))
                    .into(binding.avatar);
        }
        binding.name.setText(toComment.getFromNameIncludeNote());
        binding.time.setText(TimeUtil.formatShortRelativeTime(toComment.getCommentTime()));
        showText(toComment, binding, activity);
    }

    private void showText(BroadcastComment broadcastComment, PopupWindowBroadcastToCommentBinding binding, Activity activity) {
        if(broadcastComment.getToCommentId() == null) {
            binding.text.setText(broadcastComment.getText());
        }else {
            String toUserSpan = "@" + broadcastComment.getToComment().getFromNameIncludeNote() + " ";
            SpannableString spannableString = new SpannableString(toUserSpan + broadcastComment.getText());
            ClickableSpan userMentionClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    skipContentClick = true;
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
            binding.text.setHighlightColor(Color.TRANSPARENT);
        }
    }

    private void setupYiers() {
        binding.text.setOnClickListener(v -> {
            if(skipContentClick){
                skipContentClick = false;
                return;
            }
            if(onContentClickYier != null) onContentClickYier.onClick(v);
        });
        getPopupWindow().getContentView().setOnClickListener(v -> {
            if(skipContentClick){
                skipContentClick = false;
                return;
            }
            if(onContentClickYier != null) onContentClickYier.onClick(v);
        });
    }

    public void show(View anchorView) {
        int screenWidth = activity.getWindow().getDecorView().getWidth();
        binding.getRoot().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = binding.getRoot().getMeasuredWidth();
        int xOffset = (screenWidth - popupWidth) / 2;
        popupWindow.showAsDropDown(anchorView, xOffset, 0);
    }

    public void dismiss(){
        popupWindow.dismiss();
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public void setOnContentClickYier(View.OnClickListener onContentClickYier) {
        this.onContentClickYier = onContentClickYier;
    }
}
