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
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelAddition;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupChannelAdditionActivitySendBinding;
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
public class GroupChannelAdditionActivitiesSendRecyclerAdapter extends WrappableRecyclerViewAdapter<GroupChannelAdditionActivitiesSendRecyclerAdapter.ViewHolder, GroupChannelAdditionActivitiesSendRecyclerAdapter.ItemData> {
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

    public GroupChannelAdditionActivitiesSendRecyclerAdapter(Activity activity, List<ItemData> itemDataList) {
        this.activity = activity;
        itemDataList.sort((o1, o2) -> {
            Date o1RequestTime = o1.getGroupChannelAdditionInfo().getRequestTime();
            Date o1RespondTime = o1.getGroupChannelAdditionInfo().getRespondTime();
            Date o1Time = o1RespondTime == null ? o1RequestTime : o1RespondTime;
            Date o2RequestTime = o2.getGroupChannelAdditionInfo().getRequestTime();
            Date o2RespondTime = o2.getGroupChannelAdditionInfo().getRespondTime();
            Date o2Time = o2RespondTime == null ? o2RequestTime : o2RespondTime;
            return -o1Time.compareTo(o2Time);
        });
        this.itemDataList = itemDataList;
    }

    public static class ItemData{
        private final GroupChannelAddition groupChannelAddition;

        public ItemData(GroupChannelAddition groupChannelAddition) {
            this.groupChannelAddition = groupChannelAddition;
        }

        public GroupChannelAddition getGroupChannelAdditionInfo() {
            return groupChannelAddition;
        }
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        private final RecyclerItemGroupChannelAdditionActivitySendBinding binding;

        public ViewHolder(RecyclerItemGroupChannelAdditionActivitySendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemGroupChannelAdditionActivitySendBinding binding = RecyclerItemGroupChannelAdditionActivitySendBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        showItem(holder, position);
        setupYiers(holder, position);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    private void setupYiers(ViewHolder holder, int position) {
        holder.binding.clickView.setOnClickListener(v -> {
            GroupChannelAddition groupChannelAddition = itemDataList.get(position).groupChannelAddition;
            Intent intent = new Intent(activity, GroupChannelAdditionActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL_ADDITION, groupChannelAddition);
            activity.startActivity(intent);
        });
    }

    private void showItem(ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        GroupChannel responderGroupChannel = itemData.groupChannelAddition.getResponderGroupChannel();
        holder.binding.message.setText(itemData.groupChannelAddition.getMessage());
        if(responderGroupChannel.getGroupAvatar() != null && responderGroupChannel.getGroupAvatar().getHash() != null){
            String avatarHash = responderGroupChannel.getGroupAvatar().getHash();
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getGroupAvatarUrl(activity, avatarHash))
                    .into(holder.binding.avatar);
        }else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.group_channel_default_avatar)
                    .into(holder.binding.avatar);
        }
        holder.binding.name.setText(responderGroupChannel.getNote() == null ? responderGroupChannel.getName() : responderGroupChannel.getNote());
        if (itemData.groupChannelAddition.isAccepted()) {
            holder.binding.addedText.setVisibility(View.VISIBLE);
            holder.binding.expiredText.setVisibility(View.GONE);
        } else if (itemData.groupChannelAddition.isExpired()) {
            holder.binding.addedText.setVisibility(View.GONE);
            holder.binding.expiredText.setVisibility(View.VISIBLE);
        }
        checkAndShowTimeText(holder, position, itemData);
        if (!itemData.groupChannelAddition.isViewed()) {
            GroupChannelApiCaller.viewOneAdditionActivity(null, itemData.groupChannelAddition.getUuid(), new RetrofitApiCaller.CommonYier<>(activity, false, true));
        }
        if(!itemData.groupChannelAddition.isViewed()) {
            holder.binding.badgeHost.setVisibility(View.VISIBLE);
            BadgeDisplayer.initIndicatorBadge(activity, holder.binding.badgeHost, Gravity.CENTER);
        }else {
            holder.binding.badgeHost.setVisibility(View.GONE);
        }
    }

    private void checkAndShowTimeText(ViewHolder holder, int position, ItemData itemData) {
        boolean hideTimeText = false;
        Date time = itemData.groupChannelAddition.getRespondTime() == null ? itemData.groupChannelAddition.getRequestTime() : itemData.groupChannelAddition.getRespondTime();
        int timeTextIndex = getTimeTextIndex(time);
        if(timeTextIndex == -1){
            hideTimeText = true;
        }else {
            if (position > 0) {
                ItemData itemDataPrevious = itemDataList.get(position - 1);
                Date timePrevious = itemDataPrevious.groupChannelAddition.getRespondTime() == null ? itemDataPrevious.groupChannelAddition.getRequestTime() : itemDataPrevious.groupChannelAddition.getRespondTime();
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
