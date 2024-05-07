package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.activity.ChannelAdditionActivity;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionInfo;
import com.longx.intelligent.android.ichat2.data.ChannelInfo;
import com.longx.intelligent.android.ichat2.data.SelfInfo;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChannelAdditionActivityPendingBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LONG on 2024/5/3 at 10:53 PM.
 */
public class ChannelAdditionActivitiesPendingRecyclerAdapter extends WrappableRecyclerViewAdapter<ChannelAdditionActivitiesPendingRecyclerAdapter.ViewHolder, ChannelAdditionActivitiesPendingRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;
    private static final Date now = new Date();
    private static final List<Pair<Long, String>> timePairs = new ArrayList<>();
    static {
        timePairs.add(new ImmutablePair<>(3 * 24 * 60 * 60 * 1000L, "三天前"));
    }

    public ChannelAdditionActivitiesPendingRecyclerAdapter(Activity activity, List<ItemData> itemDataList) {
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
        private final RecyclerItemChannelAdditionActivityPendingBinding binding;

        public ViewHolder(RecyclerItemChannelAdditionActivityPendingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChannelAdditionActivityPendingBinding binding = RecyclerItemChannelAdditionActivityPendingBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        showItem(holder, position);
        setupYiers(holder, position);
    }

    private void setupYiers(ViewHolder holder, int position) {
        holder.binding.clickView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelAdditionActivity.class);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    private void showItem(ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        SelfInfo currentUserInfo = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(activity);
        boolean isCurrentUserRequester = currentUserInfo.getIchatId().equals(itemData.channelAdditionInfo.getRequesterChannelInfo().getIchatId());
        ChannelInfo channelInfo;
        if(isCurrentUserRequester){
            channelInfo = itemData.channelAdditionInfo.getResponderChannelInfo();
        }else {
            channelInfo = itemData.channelAdditionInfo.getRequesterChannelInfo();
        }
        String username = channelInfo.getUsername();
        String avatarHash = channelInfo.getAvatarInfo().getHash();
        holder.binding.username.setText(username);
        holder.binding.message.setText(itemData.channelAdditionInfo.getMessage());
        GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), holder.binding.avatar);
        if(isCurrentUserRequester){
            holder.binding.goConfirmButton.setVisibility(View.INVISIBLE);
            holder.binding.pendingConfirmText.setVisibility(View.VISIBLE);
        }else {
            holder.binding.goConfirmButton.setVisibility(View.VISIBLE);
            holder.binding.pendingConfirmText.setVisibility(View.INVISIBLE);
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
