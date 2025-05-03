package com.longx.intelligent.android.imessage.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemSettingTagChannelTagBinding;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/6/3 at 8:47 PM.
 */
public class SettingTagChannelTagsRecyclerAdapter extends WrappableRecyclerViewAdapter<SettingTagChannelTagsRecyclerAdapter.ViewHolder, List<ChannelTag>> {
    private final AppCompatActivity activity;
    private final Channel channel;
    private final List<ChannelTag> channelTags;
    private final List<ChannelTag> toAddChannelTags = new ArrayList<>();
    private final List<ChannelTag> toRemoveChannelTags = new ArrayList<>();

    public SettingTagChannelTagsRecyclerAdapter(AppCompatActivity activity, List<ChannelTag> channelTags, Channel channel) {
        this.activity = activity;
        this.channelTags = channelTags;
        this.channel = channel;
        channelTags.sort(Comparator.comparingInt(ChannelTag::getOrder).reversed());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemSettingTagChannelTagBinding binding;
        public ViewHolder(RecyclerItemSettingTagChannelTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemSettingTagChannelTagBinding binding = RecyclerItemSettingTagChannelTagBinding.inflate(activity.getLayoutInflater(), parent, false);
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
        holder.binding.checkBox.setChecked(channelTag.getChannelImessageIdList().contains(channel.getImessageId()));
        setUpYiers(holder, position);
    }

    private void setUpYiers(ViewHolder holder, int position) {
        ChannelTag channelTag = channelTags.get(position);
        boolean isAdded = channelTag.getChannelImessageIdList().contains(channel.getImessageId());
        holder.binding.content.setOnClickListener(v -> {
            holder.binding.checkBox.setChecked(!holder.binding.checkBox.isChecked());
            boolean nowChecked = holder.binding.checkBox.isChecked();
            if (isAdded) {
                if (nowChecked) {
                    toRemoveChannelTags.remove(channelTag);
                } else {
                    toRemoveChannelTags.add(channelTag);
                }
            }else {
                if(nowChecked){
                    toAddChannelTags.add(channelTag);
                }else {
                    toAddChannelTags.remove(channelTag);
                }
            }
        });
    }

    public List<ChannelTag> getChannelTags() {
        return channelTags;
    }

    public List<ChannelTag> getToAddChannelTags() {
        return toAddChannelTags;
    }

    public List<ChannelTag> getToRemoveChannelTags() {
        return toRemoveChannelTags;
    }
}
