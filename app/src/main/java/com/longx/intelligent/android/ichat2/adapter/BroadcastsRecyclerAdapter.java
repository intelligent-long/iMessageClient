package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemBroadcastBinding;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/7/29 at 下午12:13.
 */
public class BroadcastsRecyclerAdapter extends WrappableRecyclerViewAdapter<BroadcastsRecyclerAdapter.ViewHolder, BroadcastsRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;

    public BroadcastsRecyclerAdapter(Activity activity, List<ItemData> itemDataList) {
        this.activity = activity;
        sortItemDataList(itemDataList);
        this.itemDataList = itemDataList;
    }

    public static class ItemData{
        private Broadcast broadcast;
        public ItemData(Broadcast broadcast) {
            this.broadcast = broadcast;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemBroadcastBinding binding;
        public ViewHolder(RecyclerItemBroadcastBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemBroadcastBinding binding = RecyclerItemBroadcastBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        String name = null;
        Self currentUserProfile = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity);
        if(currentUserProfile.getIchatId().equals(itemData.broadcast.getIchatId())){
            name = currentUserProfile.getUsername();
        }else {
            Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(itemData.broadcast.getIchatId());
            if(channel != null) name = channel.getName();
        }
        holder.binding.name.setText(name);
        holder.binding.time.setText(TimeUtil.formatRelativeTime(itemData.broadcast.getTime()));
        holder.binding.text.setText(itemData.broadcast.getText());
    }

    private void sortItemDataList(List<ItemData> itemDataList){
        itemDataList.sort(Comparator.comparing(o -> o.broadcast.getTime()));
    }

    public void addItemsAndShow(List<ItemData> items){
        int insertBeginPosition = itemDataList.size();
        sortItemDataList(items);
        itemDataList.addAll(items);
        notifyItemRangeInserted(insertBeginPosition, items.size());
    }
}
