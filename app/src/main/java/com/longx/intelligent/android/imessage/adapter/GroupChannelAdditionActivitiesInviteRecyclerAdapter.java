package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.GroupChannelActivity;
import com.longx.intelligent.android.imessage.activity.GroupChannelAdditionActivity;
import com.longx.intelligent.android.imessage.activity.GroupChannelInvitationActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelAddition;
import com.longx.intelligent.android.imessage.data.GroupChannelInvitation;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupChannelAdditionActivityInviteBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupChannelAdditionActivitySendBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.ui.BadgeDisplayer;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LONG on 2025/5/15 at 下午10:23.
 */
public class GroupChannelAdditionActivitiesInviteRecyclerAdapter extends WrappableRecyclerViewAdapter<GroupChannelAdditionActivitiesInviteRecyclerAdapter.ViewHolder, GroupChannelAdditionActivitiesInviteRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<GroupChannelAdditionActivitiesInviteRecyclerAdapter.ItemData> itemDataList;
    private static final Date now = new Date();
    private static final List<Pair<Long, String>> timePairs = new ArrayList<>();
    static {
        timePairs.add(new ImmutablePair<>(3 * 24 * 60 * 60 * 1000L, "三天前"));
        timePairs.add(new ImmutablePair<>(7 * 24 * 60 * 60 * 1000L, "一周前"));
        timePairs.add(new ImmutablePair<>(30 * 24 * 60 * 60 * 1000L, "一月前"));
        timePairs.add(new ImmutablePair<>(365 * 24 * 60 * 60 * 1000L, "一年前"));
    }

    public GroupChannelAdditionActivitiesInviteRecyclerAdapter(Activity activity, List<GroupChannelAdditionActivitiesInviteRecyclerAdapter.ItemData> itemDataList) {
        this.activity = activity;
        itemDataList.sort((o1, o2) -> {
            Date o1RequestTime = o1.getGroupChannelInvitation().getRequestTime();
            Date o1RespondTime = o1.getGroupChannelInvitation().getRespondTime();
            Date o1Time = o1RespondTime == null ? o1RequestTime : o1RespondTime;
            Date o2RequestTime = o2.getGroupChannelInvitation().getRequestTime();
            Date o2RespondTime = o2.getGroupChannelInvitation().getRespondTime();
            Date o2Time = o2RespondTime == null ? o2RequestTime : o2RespondTime;
            return -o1Time.compareTo(o2Time);
        });
        this.itemDataList = itemDataList;
    }

    public static class ItemData{
        private final GroupChannelInvitation groupChannelInvitation;

        public ItemData(GroupChannelInvitation groupChannelInvitation) {
            this.groupChannelInvitation = groupChannelInvitation;
        }

        public GroupChannelInvitation getGroupChannelInvitation() {
            return groupChannelInvitation;
        }
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        private final RecyclerItemGroupChannelAdditionActivityInviteBinding binding;

        public ViewHolder(RecyclerItemGroupChannelAdditionActivityInviteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public GroupChannelAdditionActivitiesInviteRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemGroupChannelAdditionActivityInviteBinding binding = RecyclerItemGroupChannelAdditionActivityInviteBinding.inflate(activity.getLayoutInflater());
        return new GroupChannelAdditionActivitiesInviteRecyclerAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChannelAdditionActivitiesInviteRecyclerAdapter.ViewHolder holder, int position) {
        showItem(holder, position);
        setupYiers(holder, position);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    private void setupYiers(GroupChannelAdditionActivitiesInviteRecyclerAdapter.ViewHolder holder, int position) {
        holder.binding.clickView.setOnClickListener(v -> {
            GroupChannelInvitation groupChannelInvitation = itemDataList.get(position).groupChannelInvitation;
            Intent intent = new Intent(activity, GroupChannelInvitationActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL_INVITATION, groupChannelInvitation);
            activity.startActivity(intent);
        });
    }

    private void showItem(GroupChannelAdditionActivitiesInviteRecyclerAdapter.ViewHolder holder, int position) {
        GroupChannelAdditionActivitiesInviteRecyclerAdapter.ItemData itemData = itemDataList.get(position);
        Self currentUserInfo = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity);
        boolean isCurrentUserInviter = currentUserInfo.getImessageId().equals(itemData.groupChannelInvitation.getInviter().getImessageId());
        Channel inviter = itemData.groupChannelInvitation.getInviter();
        Channel invitee = itemData.groupChannelInvitation.getInvitee();
        GroupChannel groupChannelInvitedTo = itemData.groupChannelInvitation.getGroupChannelInvitedTo();
        holder.binding.message.setText(itemData.groupChannelInvitation.getMessage());
        if (isCurrentUserInviter) {
            if (invitee.getAvatar() != null) {
                String avatarHash = invitee.getAvatar().getHash();
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getGroupAvatarUrl(activity, avatarHash), holder.binding.avatar);
            } else {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.group_channel_default_avatar, holder.binding.avatar);
            }
            holder.binding.name.setText(invitee.getNote() == null ? invitee.getUsername() : invitee.getNote());
        }else {
            if (inviter.getAvatar() != null) {
                String avatarHash = inviter.getAvatar().getHash();
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), holder.binding.avatar);
            } else {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, holder.binding.avatar);
            }
            holder.binding.name.setText(inviter.getNote() == null ? inviter.getUsername() : inviter.getNote());
        }
        if (itemData.groupChannelInvitation.isAccepted()) {
            holder.binding.acceptedText.setVisibility(View.VISIBLE);
            holder.binding.expiredText.setVisibility(View.GONE);
        } else if (itemData.groupChannelInvitation.isExpired()) {
            holder.binding.acceptedText.setVisibility(View.GONE);
            holder.binding.expiredText.setVisibility(View.VISIBLE);
        }

        checkAndShowTimeText(holder, position, itemData);
        if (!itemData.groupChannelInvitation.isViewed()) {
            GroupChannelApiCaller.viewOneAdditionActivity(null, itemData.groupChannelInvitation.getUuid(), new RetrofitApiCaller.CommonYier<>(activity, false, true));
        }
        if(!itemData.groupChannelInvitation.isViewed()) {
            holder.binding.badgeHost.setVisibility(View.VISIBLE);
            BadgeDisplayer.initIndicatorBadge(activity, holder.binding.badgeHost, Gravity.CENTER);
        }else {
            holder.binding.badgeHost.setVisibility(View.GONE);
        }
    }

    private void checkAndShowTimeText(GroupChannelAdditionActivitiesInviteRecyclerAdapter.ViewHolder holder, int position, GroupChannelAdditionActivitiesInviteRecyclerAdapter.ItemData itemData) {
        boolean hideTimeText = false;
        Date time = itemData.groupChannelInvitation.getRespondTime() == null ? itemData.groupChannelInvitation.getRequestTime() : itemData.groupChannelInvitation.getRespondTime();
        int timeTextIndex = getTimeTextIndex(time);
        if(timeTextIndex == -1){
            hideTimeText = true;
        }else {
            if (position > 0) {
                GroupChannelAdditionActivitiesInviteRecyclerAdapter.ItemData itemDataPrevious = itemDataList.get(position - 1);
                Date timePrevious = itemDataPrevious.groupChannelInvitation.getRespondTime() == null ? itemDataPrevious.groupChannelInvitation.getRequestTime() : itemDataPrevious.groupChannelInvitation.getRespondTime();
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
