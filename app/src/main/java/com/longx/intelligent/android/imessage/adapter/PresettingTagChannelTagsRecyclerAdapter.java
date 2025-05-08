package com.longx.intelligent.android.imessage.adapter;

import android.os.Parcelable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemPresettingTagChannelTagBinding;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/6/3 at 8:47 PM.
 */
public class PresettingTagChannelTagsRecyclerAdapter extends WrappableRecyclerViewAdapter<PresettingTagChannelTagsRecyclerAdapter.ViewHolder, List<ChannelTag>> {
    private final AppCompatActivity activity;
    private final List<ChannelTag> channelTags;
    private final ArrayList<ChannelTag> checkedChannelTags;

    public PresettingTagChannelTagsRecyclerAdapter(AppCompatActivity activity, List<ChannelTag> channelTags, ArrayList<ChannelTag> checkedChannelTags) {
        this.activity = activity;
        this.channelTags = channelTags;
        channelTags.sort(Comparator.comparingInt(ChannelTag::getOrder).reversed());
        this.checkedChannelTags = checkedChannelTags;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemPresettingTagChannelTagBinding binding;
        public ViewHolder(RecyclerItemPresettingTagChannelTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemPresettingTagChannelTagBinding binding = RecyclerItemPresettingTagChannelTagBinding.inflate(activity.getLayoutInflater(), parent, false);
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
        holder.binding.checkBox.setChecked(checkedChannelTags.contains(channelTag));
        setUpYiers(holder, position);
    }

    private void setUpYiers(ViewHolder holder, int position) {
        ChannelTag channelTag = channelTags.get(position);
        holder.binding.content.setOnClickListener(v -> {
            holder.binding.checkBox.setChecked(!holder.binding.checkBox.isChecked());
            boolean nowChecked = holder.binding.checkBox.isChecked();
            if(nowChecked) {
                checkedChannelTags.add(channelTag);
            }else {
                checkedChannelTags.remove(channelTag);
            }
        });
    }

    public List<ChannelTag> getChannelTags() {
        return channelTags;
    }

    public ArrayList<? extends Parcelable> getCheckedChannelTags() {
        return checkedChannelTags;
    }
}
