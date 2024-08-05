package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemSendBroadcastMediasBinding;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.List;

/**
 * Created by LONG on 2024/8/5 at 下午2:45.
 */
public class SendBroadcastMediasRecyclerAdapter extends WrappableRecyclerViewAdapter<SendBroadcastMediasRecyclerAdapter.ViewHolder, Uri> {
    private final Activity activity;
    private final List<Uri> uriList;

    public SendBroadcastMediasRecyclerAdapter(Activity activity, List<Uri> uriList) {
        this.activity = activity;
        this.uriList = uriList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemSendBroadcastMediasBinding binding;
        public ViewHolder(RecyclerItemSendBroadcastMediasBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemSendBroadcastMediasBinding binding = RecyclerItemSendBroadcastMediasBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = uriList.get(position);
        GlideApp
                .with(activity.getApplicationContext())
                .load(uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.image);
    }
}
