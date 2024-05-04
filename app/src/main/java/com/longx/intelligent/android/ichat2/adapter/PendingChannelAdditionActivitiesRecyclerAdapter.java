package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.signature.ObjectKey;
import com.longx.intelligent.android.ichat2.da.cachefile.CacheFilesAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionInfo;
import com.longx.intelligent.android.ichat2.data.ChannelInfo;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemPendingChannelAdditionActivityBinding;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.io.File;
import java.util.List;

/**
 * Created by LONG on 2024/5/3 at 10:53 PM.
 */
public class PendingChannelAdditionActivitiesRecyclerAdapter extends WrappableRecyclerViewAdapter<PendingChannelAdditionActivitiesRecyclerAdapter.ViewHolder, PendingChannelAdditionActivitiesRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;

    public PendingChannelAdditionActivitiesRecyclerAdapter(Activity activity, List<ItemData> itemDataList) {
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
        private final RecyclerItemPendingChannelAdditionActivityBinding binding;

        public ViewHolder(RecyclerItemPendingChannelAdditionActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemPendingChannelAdditionActivityBinding binding = RecyclerItemPendingChannelAdditionActivityBinding.inflate(activity.getLayoutInflater());
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
        String currentUserIchatId = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(activity).getIchatId();
        boolean isCurrentUserRequester = currentUserIchatId.equals(itemData.channelAdditionInfo.getRequesterChannelInfo().getIchatId());
        ChannelInfo channelInfo;
        if(isCurrentUserRequester){
            channelInfo = itemData.channelAdditionInfo.getResponderChannelInfo();
        }else {
            channelInfo = itemData.channelAdditionInfo.getRequesterChannelInfo();
        }
        String username = channelInfo.getUsername();
        String avatarHash = channelInfo.getAvatarInfo().getHash();
        String avatarExtension = channelInfo.getAvatarInfo().getExtension();
        String ichatId = channelInfo.getIchatId();
        holder.binding.username.setText(username);
        holder.binding.message.setText(itemData.channelAdditionInfo.getMessage());
        File avatarCache = CacheFilesAccessor.getAvatarCache(activity, ichatId, avatarExtension);
        GlideApp.with(activity.getApplicationContext())
                .load(avatarCache)
                .signature(new ObjectKey(avatarHash))
                .into(holder.binding.avatar);
        CacheFilesAccessor.cacheAvatarTempFromServer(activity, avatarHash, ichatId, results -> {
            File avatarCacheFile = (File) results[0];
            GlideApp.with(activity.getApplicationContext())
                    .load(avatarCacheFile)
                    .signature(new ObjectKey(avatarHash))
                    .into(holder.binding.avatar);
        });
    }
}
