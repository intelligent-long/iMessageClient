package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.request.target.Target;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ChannelAdditionActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.ChannelAddition;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemChannelAdditionActivityReceiveBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
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
public class ChannelAdditionActivitiesReceiveRecyclerAdapter extends WrappableRecyclerViewAdapter<ChannelAdditionActivitiesReceiveRecyclerAdapter.ViewHolder, ChannelAdditionActivitiesReceiveRecyclerAdapter.ItemData> {
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

    public ChannelAdditionActivitiesReceiveRecyclerAdapter(Activity activity, List<ItemData> itemDataList) {
        this.activity = activity;
        itemDataList.sort((o1, o2) -> {
            Date o1RequestTime = o1.getChannelAdditionInfo().getRequestTime();
            Date o1RespondTime = o1.getChannelAdditionInfo().getRespondTime();
            Date o1Time = o1RespondTime == null ? o1RequestTime : o1RespondTime;
            Date o2RequestTime = o2.getChannelAdditionInfo().getRequestTime();
            Date o2RespondTime = o2.getChannelAdditionInfo().getRespondTime();
            Date o2Time = o2RespondTime == null ? o2RequestTime : o2RespondTime;
            return -o1Time.compareTo(o2Time);
        });
        this.itemDataList = itemDataList;
    }

    public static class ItemData{
        private final ChannelAddition channelAddition;

        public ItemData(ChannelAddition channelAddition) {
            this.channelAddition = channelAddition;
        }

        public ChannelAddition getChannelAdditionInfo() {
            return channelAddition;
        }
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        private final RecyclerItemChannelAdditionActivityReceiveBinding binding;

        public ViewHolder(RecyclerItemChannelAdditionActivityReceiveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChannelAdditionActivityReceiveBinding binding = RecyclerItemChannelAdditionActivityReceiveBinding.inflate(activity.getLayoutInflater());
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
            Intent intent = new Intent(activity, ChannelAdditionActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL_ADDITION, itemDataList.get(position).channelAddition);
            activity.startActivity(intent);
        });
    }

    private void showItem(ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        Self currentUserInfo = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity);
        boolean isCurrentUserResponder = currentUserInfo.getImessageId().equals(itemData.channelAddition.getResponderChannel().getImessageId());
        if(isCurrentUserResponder){
            Channel channel = itemData.channelAddition.getRequesterChannel();
            String username = channel.getUsername();
            String avatarHash = channel.getAvatar() == null ? null : channel.getAvatar().getHash();
            holder.binding.name.setText(channel.getNote() == null ? channel.getUsername() : channel.getNote());
            holder.binding.message.setText(itemData.channelAddition.getMessage());
            if(avatarHash == null){
                GlideApp
                        .with(activity.getApplicationContext())
                        .load(R.drawable.default_avatar)
                        .into(holder.binding.avatar);
            }else {
                GlideApp
                        .with(activity.getApplicationContext())
                        .load(NetDataUrls.getAvatarUrl(activity, avatarHash))
                        .into(holder.binding.avatar);
            }
            if(itemData.channelAddition.isAccepted()){
                holder.binding.addedText.setVisibility(View.VISIBLE);
                holder.binding.expiredText.setVisibility(View.GONE);
            }else if(itemData.channelAddition.isExpired()){
                holder.binding.addedText.setVisibility(View.GONE);
                holder.binding.expiredText.setVisibility(View.VISIBLE);
            }
            if(!itemData.channelAddition.isViewed()) {
                ChannelApiCaller.viewOneAdditionActivity(null, itemData.channelAddition.getUuid(), new RetrofitApiCaller.CommonYier<>((AppCompatActivity) activity, false, true));
            }
        }else {
            holder.binding.getRoot().setVisibility(View.GONE);
        }
        checkAndShowTimeText(holder, position, itemData);
        if(!itemData.channelAddition.isViewed()) {
            holder.binding.badgeHost.setVisibility(View.VISIBLE);
            BadgeDisplayer.initIndicatorBadge(activity, holder.binding.badgeHost, Gravity.CENTER);
        }else {
            holder.binding.badgeHost.setVisibility(View.GONE);
        }
    }

    private void checkAndShowTimeText(ViewHolder holder, int position, ItemData itemData) {
        boolean hideTimeText = false;
        Date time = itemData.channelAddition.getRespondTime() == null ? itemData.channelAddition.getRequestTime() : itemData.channelAddition.getRespondTime();
        int timeTextIndex = getTimeTextIndex(time);
        if(timeTextIndex == -1){
            hideTimeText = true;
        }else {
            if (position > 0) {
                ItemData itemDataPrevious = itemDataList.get(position - 1);
                Date timePrevious = itemDataPrevious.channelAddition.getRespondTime() == null ? itemDataPrevious.channelAddition.getRequestTime() : itemDataPrevious.channelAddition.getRespondTime();
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
