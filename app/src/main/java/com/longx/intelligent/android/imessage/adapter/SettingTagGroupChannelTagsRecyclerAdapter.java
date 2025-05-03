package com.longx.intelligent.android.imessage.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemSettingTagGroupChannelTagBinding;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/6/3 at 8:47 PM.
 */
public class SettingTagGroupChannelTagsRecyclerAdapter extends WrappableRecyclerViewAdapter<SettingTagGroupChannelTagsRecyclerAdapter.ViewHolder, List<ChannelTag>> {
    private final AppCompatActivity activity;
    private final GroupChannel groupChannel;
    private final List<GroupChannelTag> groupChannelTags;
    private final List<GroupChannelTag> toAddGroupChannelTags = new ArrayList<>();
    private final List<GroupChannelTag> toRemoveGroupChannelTags = new ArrayList<>();

    public SettingTagGroupChannelTagsRecyclerAdapter(AppCompatActivity activity, List<GroupChannelTag> groupChannelTags, GroupChannel groupChannel) {
        this.activity = activity;
        this.groupChannelTags = groupChannelTags;
        this.groupChannel = groupChannel;
        groupChannelTags.sort(Comparator.comparingInt(GroupChannelTag::getOrder));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemSettingTagGroupChannelTagBinding binding;
        public ViewHolder(RecyclerItemSettingTagGroupChannelTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemSettingTagGroupChannelTagBinding binding = RecyclerItemSettingTagGroupChannelTagBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return groupChannelTags.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupChannelTag groupChannelTag = groupChannelTags.get(position);
        holder.binding.tagName.setText(groupChannelTag.getName());
        holder.binding.checkBox.setChecked(groupChannelTag.getGroupChannelIdList().contains(groupChannel.getGroupChannelId()));
        setUpYiers(holder, position);
    }

    private void setUpYiers(ViewHolder holder, int position) {
        GroupChannelTag groupChannelTag = groupChannelTags.get(position);
        boolean isAdded = groupChannelTag.getGroupChannelIdList().contains(groupChannel.getGroupChannelId());
        holder.binding.content.setOnClickListener(v -> {
            holder.binding.checkBox.setChecked(!holder.binding.checkBox.isChecked());
            boolean nowChecked = holder.binding.checkBox.isChecked();
            if (isAdded) {
                if (nowChecked) {
                    toRemoveGroupChannelTags.remove(groupChannelTag);
                } else {
                    toRemoveGroupChannelTags.add(groupChannelTag);
                }
            }else {
                if(nowChecked){
                    toAddGroupChannelTags.add(groupChannelTag);
                }else {
                    toAddGroupChannelTags.remove(groupChannelTag);
                }
            }
        });
    }

    public List<GroupChannelTag> getGroupChannelTags() {
        return groupChannelTags;
    }

    public List<GroupChannelTag> getToAddGroupChannelTags() {
        return toAddGroupChannelTags;
    }

    public List<GroupChannelTag> getToRemoveGroupChannelTags() {
        return toRemoveGroupChannelTags;
    }
}
