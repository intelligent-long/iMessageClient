package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChannelTagBinding;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/6/3 at 8:47 PM.
 */
public class ChannelTagsRecyclerAdapter extends WrappableRecyclerViewAdapter<ChannelTagsRecyclerAdapter.ViewHolder, List<ChannelTag>> {
    private final Activity activity;
    private final List<ChannelTag> channelTags;

    public ChannelTagsRecyclerAdapter(Activity activity, List<ChannelTag> channelTags) {
        this.activity = activity;
        this.channelTags = channelTags;
        channelTags.sort(Comparator.comparing(ChannelTag::getName));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemChannelTagBinding binding;
        public ViewHolder(RecyclerItemChannelTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChannelTagBinding binding = RecyclerItemChannelTagBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return channelTags.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChannelTag channelTag = channelTags.get(position);
        holder.binding.tagName.setText(channelTag.getName());
    }
}
