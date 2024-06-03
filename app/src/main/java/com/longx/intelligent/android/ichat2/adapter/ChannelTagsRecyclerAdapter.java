package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.bottomsheet.RenameChannelTagBottomSheet;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChannelTagBinding;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/6/3 at 8:47 PM.
 */
public class ChannelTagsRecyclerAdapter extends WrappableRecyclerViewAdapter<ChannelTagsRecyclerAdapter.ViewHolder, List<ChannelTag>> {
    private final Activity activity;
    private List<ChannelTag> pastChannelTags;
    private List<ChannelTag> channelTags;
    private boolean dragSortState;

    public ChannelTagsRecyclerAdapter(Activity activity, List<ChannelTag> channelTags) {
        this.activity = activity;
        this.channelTags = channelTags;
        channelTags.sort(Comparator.comparingInt(ChannelTag::getOrder));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemChannelTagBinding binding;
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
        if(dragSortState) {
            holder.binding.dragHandle.setVisibility(View.VISIBLE);
        }else {
            holder.binding.dragHandle.setVisibility(View.GONE);
        }
        setUpYiers(holder, position);
    }

    private void setUpYiers(ViewHolder holder, int position) {
        ChannelTag channelTag = channelTags.get(position);
        holder.binding.clickViewRename.setOnClickListener(v -> {
            new RenameChannelTagBottomSheet((AppCompatActivity) activity, channelTag).show();
        });
        holder.binding.clickViewDelete.setOnClickListener(v -> {

        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void switchDragSortState(boolean dragSortState) {
        this.dragSortState = dragSortState;
        notifyDataSetChanged();
    }

    public boolean isDragSortState() {
        return dragSortState;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void moveAndShow(int from, int to){
        if(pastChannelTags == null) {
            pastChannelTags = new ArrayList<>(channelTags);
        }
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(channelTags, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(channelTags, i, i - 1);
            }
        }
        for (int i = 0; i < channelTags.size(); i++) {
            channelTags.get(i).setOrder(i);
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void cancelMoveAndShow(){
        if(pastChannelTags != null) {
            channelTags = new ArrayList<>(pastChannelTags);
            pastChannelTags = null;
            notifyDataSetChanged();
        }
    }

    public List<ChannelTag> getChannelTags() {
        return channelTags;
    }
}
