package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.BroadcastComment;
import com.longx.intelligent.android.ichat2.data.BroadcastLike;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemBroadcastCommentBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/9/24 at 上午5:50.
 */
public class BroadcastCommentsRecyclerAdapter extends WrappableRecyclerViewAdapter<BroadcastCommentsRecyclerAdapter.ViewHolder, BroadcastCommentsRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList = new ArrayList<>();

    public BroadcastCommentsRecyclerAdapter(Activity activity) {
        this.activity = activity;
    }

    public static class ItemData {
        private final BroadcastComment broadcastComment;

        public ItemData(BroadcastComment broadcastComment) {
            this.broadcastComment = broadcastComment;
        }

        public BroadcastComment getBroadcastComment() {
            return broadcastComment;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerItemBroadcastCommentBinding binding;

        public ViewHolder(RecyclerItemBroadcastCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemBroadcastCommentBinding binding = RecyclerItemBroadcastCommentBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BroadcastComment broadcastComment = itemDataList.get(position).broadcastComment;
        if (broadcastComment.getAvatarHash() == null) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(holder.binding.avatar);
        } else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(activity, broadcastComment.getAvatarHash()))
                    .into(holder.binding.avatar);
        }
        Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(broadcastComment.getFromId());
        String name;
        if(channel != null){
            name = channel.getName();
        }else {
            name = broadcastComment.getFromName();
        }
        holder.binding.name.setText(name);
        holder.binding.time.setText(TimeUtil.formatRelativeTime(broadcastComment.getCommentTime()));
        holder.binding.text.setText(broadcastComment.getText());
        setupYiers(holder, position);
    }

    private void setupYiers(ViewHolder holder, int position) {

    }

    public List<ItemData> getItemDataList() {
        return itemDataList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItemsToEndAndShow(List<BroadcastComment> broadcastComments){
        broadcastComments.sort((o1, o2) -> - o1.getCommentTime().compareTo(o2.getCommentTime()));
        broadcastComments.forEach(broadcastComment -> itemDataList.add(new ItemData(broadcastComment)));
        notifyDataSetChanged();
    }
}
