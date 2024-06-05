package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.TagChannelActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.bottomsheet.RenameChannelTagBottomSheet;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChannelTagBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemSettingTagChannelTagBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

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
        channelTags.sort(Comparator.comparingInt(ChannelTag::getOrder));
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
        holder.binding.checkBox.setChecked(channelTag.getChannelIchatIdList().contains(channel.getIchatId()));
        setUpYiers(holder, position);
    }

    private void setUpYiers(ViewHolder holder, int position) {
        ChannelTag channelTag = channelTags.get(position);
        boolean isAdded = channelTag.getChannelIchatIdList().contains(channel.getIchatId());
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
