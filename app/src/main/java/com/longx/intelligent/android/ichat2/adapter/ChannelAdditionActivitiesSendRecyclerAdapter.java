package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionInfo;
import com.longx.intelligent.android.ichat2.data.ChannelInfo;
import com.longx.intelligent.android.ichat2.data.SelfInfo;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChannelAdditionActivityPendingBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChannelAdditionActivitySendBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.List;

/**
 * Created by LONG on 2024/5/3 at 10:53 PM.
 */
public class ChannelAdditionActivitiesSendRecyclerAdapter extends WrappableRecyclerViewAdapter<ChannelAdditionActivitiesSendRecyclerAdapter.ViewHolder, ChannelAdditionActivitiesSendRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;

    public ChannelAdditionActivitiesSendRecyclerAdapter(Activity activity, List<ItemData> itemDataList) {
        this.activity = activity;
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
    }
}
