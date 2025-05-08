package com.longx.intelligent.android.imessage.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemPresettingTagGroupChannelTagBinding;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2025/4/20 at 上午2:57.
 */
public class PresettingTagGroupChannelTagsRecyclerAdapter extends WrappableRecyclerViewAdapter<PresettingTagGroupChannelTagsRecyclerAdapter.ViewHolder, List<GroupChannelTag>> {
    private final AppCompatActivity activity;
    private final List<GroupChannelTag> groupChannelTags;
    private final ArrayList<GroupChannelTag> checkedGroupChannelTags;

    public PresettingTagGroupChannelTagsRecyclerAdapter(AppCompatActivity activity, List<GroupChannelTag> groupChannelTags, ArrayList<GroupChannelTag> checkedGroupChannelTags) {
        this.activity = activity;
        this.groupChannelTags = groupChannelTags;
        groupChannelTags.sort(Comparator.comparingInt(GroupChannelTag::getOrder).reversed());
        this.checkedGroupChannelTags = checkedGroupChannelTags;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemPresettingTagGroupChannelTagBinding binding;
        public ViewHolder(RecyclerItemPresettingTagGroupChannelTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemPresettingTagGroupChannelTagBinding binding = RecyclerItemPresettingTagGroupChannelTagBinding.inflate(activity.getLayoutInflater(), parent, false);
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
        holder.binding.checkBox.setChecked(checkedGroupChannelTags.contains(groupChannelTag));
        setUpYiers(holder, position);
    }

    private void setUpYiers(ViewHolder holder, int position) {
        GroupChannelTag groupChannelTag = groupChannelTags.get(position);
        holder.binding.content.setOnClickListener(v -> {
            holder.binding.checkBox.setChecked(!holder.binding.checkBox.isChecked());
            boolean nowChecked = holder.binding.checkBox.isChecked();
            if(nowChecked) {
                checkedGroupChannelTags.add(groupChannelTag);
            }else {
                checkedGroupChannelTags.remove(groupChannelTag);
            }
        });
    }

    public List<GroupChannelTag> getGroupChannelTags() {
        return groupChannelTags;
    }

    public ArrayList<GroupChannelTag> getCheckedGroupChannelTags() {
        return checkedGroupChannelTags;
    }
}
