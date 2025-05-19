package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.GroupChannelAdditionActivity;
import com.longx.intelligent.android.imessage.activity.GroupChannelInvitationActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelActivity;
import com.longx.intelligent.android.imessage.data.GroupChannelAddition;
import com.longx.intelligent.android.imessage.data.GroupChannelInvitation;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupChannelAdditionActivityPendingBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.ui.BadgeDisplayer;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LONG on 2024/5/3 at 10:53 PM.
 */
public class GroupChannelAdditionActivitiesPendingRecyclerAdapter extends WrappableRecyclerViewAdapter<GroupChannelAdditionActivitiesPendingRecyclerAdapter.ViewHolder, GroupChannelAdditionActivitiesPendingRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;
    private static final Date now = new Date();
    private static final List<Pair<Long, String>> timePairs = new ArrayList<>();
    static {
        timePairs.add(new ImmutablePair<>(3 * 24 * 60 * 60 * 1000L, "三天前"));
        timePairs.add(new ImmutablePair<>(7 * 24 * 60 * 60 * 1000L, "一周前"));
        timePairs.add(new ImmutablePair<>(30 * 24 * 60 * 60 * 1000L, "一月前"));
        timePairs.add(new ImmutablePair<>(365 * 24 * 60 * 60 * 1000L, "一年前"));
    }

    public GroupChannelAdditionActivitiesPendingRecyclerAdapter(Activity activity, List<ItemData> itemDataList) {
        this.activity = activity;
        itemDataList.sort((o1, o2) -> {
            Date o1RequestTime = o1.groupChannelActivity.getRequestTime();
            Date o1RespondTime = o1.groupChannelActivity.getRespondTime();
            Date o1Time = o1RespondTime == null ? o1RequestTime : o1RespondTime;
            Date o2RequestTime = o2.groupChannelActivity.getRequestTime();
            Date o2RespondTime = o2.groupChannelActivity.getRespondTime();
            Date o2Time = o2RespondTime == null ? o2RequestTime : o2RespondTime;
            return -o1Time.compareTo(o2Time);
        });
        this.itemDataList = itemDataList;
    }

    public static class ItemData{
        private final GroupChannelActivity groupChannelActivity;

        public ItemData(GroupChannelActivity groupChannelActivity) {
            this.groupChannelActivity = groupChannelActivity;
        }

        public GroupChannelActivity getGroupChannelActivity() {
            return groupChannelActivity;
        }
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        private final RecyclerItemGroupChannelAdditionActivityPendingBinding binding;

        public ViewHolder(RecyclerItemGroupChannelAdditionActivityPendingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemGroupChannelAdditionActivityPendingBinding binding = RecyclerItemGroupChannelAdditionActivityPendingBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        showItem(holder, position);
        setupYiers(holder, position);
    }

    private void setupYiers(ViewHolder holder, int position) {
        View.OnClickListener onClickYier = getOnClickYier(holder, position);
        holder.binding.clickView.setOnClickListener(onClickYier);
        holder.binding.goConfirmButton.setOnClickListener(onClickYier);
    }

    private View.OnClickListener getOnClickYier(ViewHolder viewHolder, int position){
        return v -> {
            if(v.getId() == viewHolder.binding.clickView.getId() || v.getId() == viewHolder.binding.goConfirmButton.getId()){
                GroupChannelActivity groupChannelActivity = itemDataList.get(position).groupChannelActivity;
                if(groupChannelActivity instanceof GroupChannelAddition) {
                    Intent intent = new Intent(activity, GroupChannelAdditionActivity.class);
                    intent.putExtra(ExtraKeys.GROUP_CHANNEL_ADDITION, (GroupChannelAddition) groupChannelActivity);
                    activity.startActivity(intent);
                }else if(groupChannelActivity instanceof GroupChannelInvitation){
                    Intent intent = new Intent(activity, GroupChannelInvitationActivity.class);
                    intent.putExtra(ExtraKeys.GROUP_CHANNEL_INVITATION, (GroupChannelInvitation) groupChannelActivity);
                    activity.startActivity(intent);
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    private void showItem(ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        if (itemData.groupChannelActivity instanceof GroupChannelAddition) {
            GroupChannelAddition groupChannelAddition = (GroupChannelAddition) itemData.groupChannelActivity;
            Self currentUserInfo = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity);
            boolean isCurrentUserRequester = currentUserInfo.getImessageId().equals(groupChannelAddition.getRequesterChannel().getImessageId());
            Channel requesterChannel = groupChannelAddition.getRequesterChannel();
            GroupChannel responderGroupChannel = groupChannelAddition.getResponderGroupChannel();
            holder.binding.message.setText(groupChannelAddition.getMessage());
            if (isCurrentUserRequester) {
                holder.binding.goConfirmButton.setVisibility(View.INVISIBLE);
                holder.binding.pendingConfirmText.setVisibility(View.VISIBLE);
                if(responderGroupChannel.getGroupAvatar() == null){
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(R.drawable.group_channel_default_avatar)
                            .into(holder.binding.avatar);
                }else {
                    String avatarHash = responderGroupChannel.getGroupAvatar().getHash();
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(NetDataUrls.getGroupAvatarUrl(activity, avatarHash))
                            .into(holder.binding.avatar);
                }
                holder.binding.name.setText(responderGroupChannel.getNote() == null ? responderGroupChannel.getName() : responderGroupChannel.getNote());
            } else {
                holder.binding.goConfirmButton.setVisibility(View.VISIBLE);
                holder.binding.pendingConfirmText.setVisibility(View.INVISIBLE);
                if(requesterChannel.getAvatar() == null){
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(R.drawable.default_avatar)
                            .into(holder.binding.avatar);
                }else {
                    String avatarHash = requesterChannel.getAvatar().getHash();
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(NetDataUrls.getAvatarUrl(activity, avatarHash))
                            .into(holder.binding.avatar);
                }
                holder.binding.name.setText(requesterChannel.getNote() == null ? requesterChannel.getUsername() : requesterChannel.getNote());
            }
            checkAndShowTimeText(holder, position, itemData);
            if (!groupChannelAddition.isViewed()) {
                GroupChannelApiCaller.viewOneAdditionActivity(null, groupChannelAddition.getUuid(), new RetrofitApiCaller.CommonYier<>(activity, false, true));
            }
            if (!groupChannelAddition.isViewed()) {
                holder.binding.badgeHost.setVisibility(View.VISIBLE);
                BadgeDisplayer.initIndicatorBadge(activity, holder.binding.badgeHost, Gravity.CENTER);
            } else {
                holder.binding.badgeHost.setVisibility(View.GONE);
            }
        } else if (itemData.groupChannelActivity instanceof GroupChannelInvitation) {
            GroupChannelInvitation groupChannelInvitation = (GroupChannelInvitation) itemData.groupChannelActivity;
            Self currentUserInfo = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity);
            boolean isCurrentUserInviter = currentUserInfo.getImessageId().equals(groupChannelInvitation.getInviter().getImessageId());
            Channel inviter = groupChannelInvitation.getInviter();
            Channel invitee = groupChannelInvitation.getInvitee();
            GroupChannel groupChannelInvitedTo = groupChannelInvitation.getGroupChannelInvitedTo();
            holder.binding.message.setText(groupChannelInvitation.getMessage());
            if (isCurrentUserInviter) {
                holder.binding.goConfirmButton.setVisibility(View.INVISIBLE);
                holder.binding.pendingConfirmText.setVisibility(View.VISIBLE);
                if(invitee.getAvatar() == null){
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(R.drawable.group_channel_default_avatar)
                            .into(holder.binding.avatar);
                }else {
                    String avatarHash = invitee.getAvatar().getHash();
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(NetDataUrls.getGroupAvatarUrl(activity, avatarHash))
                            .into(holder.binding.avatar);
                }
                holder.binding.name.setText(invitee.getNote() == null ? invitee.getUsername() : invitee.getNote());
            } else {
                holder.binding.goConfirmButton.setVisibility(View.VISIBLE);
                holder.binding.pendingConfirmText.setVisibility(View.INVISIBLE);
                if(inviter.getAvatar() == null){
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(R.drawable.default_avatar)
                            .into(holder.binding.avatar);
                }else {
                    String avatarHash = inviter.getAvatar().getHash();
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(NetDataUrls.getAvatarUrl(activity, avatarHash))
                            .into(holder.binding.avatar);
                }
                holder.binding.name.setText(inviter.getNote() == null ? inviter.getUsername() : inviter.getNote());
            }
            checkAndShowTimeText(holder, position, itemData);
            if (!groupChannelInvitation.isViewed()) {
                GroupChannelApiCaller.viewOneAdditionActivity(null, itemData.groupChannelActivity.getUuid(), new RetrofitApiCaller.CommonYier<>(activity, false, true));
            }
            if (!groupChannelInvitation.isViewed()) {
                holder.binding.badgeHost.setVisibility(View.VISIBLE);
                BadgeDisplayer.initIndicatorBadge(activity, holder.binding.badgeHost, Gravity.CENTER);
            } else {
                holder.binding.badgeHost.setVisibility(View.GONE);
            }
        }
    }

    private void checkAndShowTimeText(ViewHolder holder, int position, ItemData itemData) {
        boolean hideTimeText = false;
        Date time = itemData.groupChannelActivity.getRespondTime() == null ? itemData.groupChannelActivity.getRequestTime() : itemData.groupChannelActivity.getRespondTime();
        int timeTextIndex = getTimeTextIndex(time);
        if(timeTextIndex == -1){
            hideTimeText = true;
        }else {
            if (position > 0) {
                ItemData itemDataPrevious = itemDataList.get(position - 1);
                Date timePrevious = itemDataPrevious.groupChannelActivity.getRespondTime() == null ? itemDataPrevious.groupChannelActivity.getRequestTime() : itemDataPrevious.groupChannelActivity.getRespondTime();
                int timeTextIndexPrevious = getTimeTextIndex(timePrevious);
                if (timeTextIndex == timeTextIndexPrevious) hideTimeText = true;
            }
        }
        if(hideTimeText) {
            holder.binding.timeText.setVisibility(View.GONE);
        }else {
            holder.binding.timeText.setText(timePairs.get(timeTextIndex).getValue());
            holder.binding.timeText.setVisibility(View.VISIBLE);
        }
    }

    private static int getTimeTextIndex(Date time) {
        for (int i = timePairs.size() - 1; i >= 0; i--) {
            if(TimeUtil.isDateAfter(time, now, timePairs.get(i).getKey())){
                return i;
            }
        }
        return -1;
    }
}
