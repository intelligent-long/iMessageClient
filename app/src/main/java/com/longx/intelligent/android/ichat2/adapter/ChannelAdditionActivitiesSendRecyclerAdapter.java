package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionInfo;
import com.longx.intelligent.android.ichat2.data.ChannelInfo;
import com.longx.intelligent.android.ichat2.data.SelfInfo;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChannelAdditionActivityPendingBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChannelAdditionActivitySendBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LONG on 2024/5/3 at 10:53 PM.
 */
public class ChannelAdditionActivitiesSendRecyclerAdapter extends WrappableRecyclerViewAdapter<ChannelAdditionActivitiesSendRecyclerAdapter.ViewHolder, ChannelAdditionActivitiesSendRecyclerAdapter.ItemData> {
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

    public ChannelAdditionActivitiesSendRecyclerAdapter(Activity activity, List<ItemData> itemDataList) {
        this.activity = activity;
        itemDataList.sort((o1, o2) -> {
            Date o1RequestTime = o1.getChannelAdditionInfo().getRequestTime();
            Date o1RespondTime = o1.getChannelAdditionInfo().getRespondTime();
            Date o1Time = o1RespondTime == null ? o1RequestTime : o1RespondTime;
            Date o2RequestTime = o2.getChannelAdditionInfo().getRequestTime();
            Date o2RespondTime = o2.getChannelAdditionInfo().getRespondTime();
            Date o2Time = o2RespondTime == null ? o2RequestTime : o2RespondTime;
            return o1Time.compareTo(o2Time);
        });
        this.itemDataList = itemDataList;
    }

    public static class ItemData{
        private final ChannelAdditionInfo channelAdditionInfo;

        public ItemData(ChannelAdditionInfo channelAdditionInfo) {
            this.channelAdditionInfo = channelAdditionInfo;
        }

        public ChannelAdditionInfo getChannelAdditionInfo() {
            return channelAdditionInfo;
        }
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        private final RecyclerItemChannelAdditionActivitySendBinding binding;

        public ViewHolder(RecyclerItemChannelAdditionActivitySendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChannelAdditionActivitySendBinding binding = RecyclerItemChannelAdditionActivitySendBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        showItem(holder, position);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    private void showItem(ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        SelfInfo currentUserInfo = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(activity);
        boolean isCurrentUserRequester = currentUserInfo.getIchatId().equals(itemData.channelAdditionInfo.getRequesterChannelInfo().getIchatId());
        if(isCurrentUserRequester){
            ChannelInfo channelInfo = itemData.channelAdditionInfo.getResponderChannelInfo();
            String username = channelInfo.getUsername();
            String avatarHash = channelInfo.getAvatarInfo().getHash();
            holder.binding.username.setText(username);
            holder.binding.message.setText(itemData.channelAdditionInfo.getMessage());
            GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), holder.binding.avatar);
            if(itemData.channelAdditionInfo.isAccepted()){
                holder.binding.addedText.setVisibility(View.VISIBLE);
                holder.binding.expiredText.setVisibility(View.GONE);
            }else if(itemData.channelAdditionInfo.isExpired()){
                holder.binding.addedText.setVisibility(View.GONE);
                holder.binding.expiredText.setVisibility(View.VISIBLE);
            }
        }else {
            holder.binding.getRoot().setVisibility(View.GONE);
        }
        checkAndShowTimeText(holder, position, itemData);
        ChannelApiCaller.viewOneAdditionActivity(null, itemData.channelAdditionInfo.getUuid(), new RetrofitApiCaller.CommonYier<>((AppCompatActivity) activity, false, true));
    }

    private void checkAndShowTimeText(ViewHolder holder, int position, ItemData itemData) {
        boolean hideTimeText = false;
        Date time = itemData.channelAdditionInfo.getRespondTime() == null ? itemData.channelAdditionInfo.getRequestTime() : itemData.channelAdditionInfo.getRespondTime();
        int timeTextIndex = getTimeTextIndex(time);
        if(timeTextIndex == -1){
            hideTimeText = true;
        }else {
            if (position > 0) {
                ItemData itemDataPrevious = itemDataList.get(position - 1);
                Date timePrevious = itemDataPrevious.channelAdditionInfo.getRespondTime() == null ? itemDataPrevious.channelAdditionInfo.getRequestTime() : itemDataPrevious.channelAdditionInfo.getRespondTime();
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
        for (int i = 0; i < timePairs.size(); i++) {
            if(TimeUtil.isDateAfter(time, now, timePairs.get(i).getKey())){
                return i;
            }
        }
        return -1;
    }
}
