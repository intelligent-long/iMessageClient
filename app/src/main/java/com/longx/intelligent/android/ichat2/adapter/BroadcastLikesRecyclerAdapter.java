package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.data.BroadcastLike;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemBroadcastLikeBinding;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/9/21 at 下午6:46.
 */
public class BroadcastLikesRecyclerAdapter extends WrappableRecyclerViewAdapter<BroadcastLikesRecyclerAdapter.ViewHolder, BroadcastLikesRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList = new ArrayList<>();

    public BroadcastLikesRecyclerAdapter(Activity activity) {
        this.activity = activity;
    }

    public static class ItemData {
        private final BroadcastLike broadcastLike;

        public ItemData(BroadcastLike broadcastLike) {
            this.broadcastLike = broadcastLike;
        }

        public BroadcastLike getBroadcastLike() {
            return broadcastLike;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerItemBroadcastLikeBinding binding;

        public ViewHolder(RecyclerItemBroadcastLikeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemBroadcastLikeBinding binding = RecyclerItemBroadcastLikeBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }
}
