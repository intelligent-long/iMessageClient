package com.longx.intelligent.android.imessage.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.data.GroupTag;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemChannelTagBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupTagBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/6/3 at 8:47 PM.
 */
public class GroupTagsRecyclerAdapter extends WrappableRecyclerViewAdapter<GroupTagsRecyclerAdapter.ViewHolder, List<GroupTag>> {
    private final AppCompatActivity activity;
    private List<GroupTag> pastGroupTags;
    private List<GroupTag> groupTags;
    private boolean dragSortState;

    public GroupTagsRecyclerAdapter(AppCompatActivity activity, List<GroupTag> groupTags) {
        this.activity = activity;
        this.groupTags = groupTags;
        groupTags.sort(Comparator.comparingInt(GroupTag::getOrder).reversed());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemGroupTagBinding binding;
        public ViewHolder(RecyclerItemGroupTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemGroupTagBinding binding = RecyclerItemGroupTagBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return groupTags.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupTag groupTag = groupTags.get(position);
        holder.binding.groupTagName.setText(groupTag.getName());
        if(dragSortState) {
            holder.binding.dragHandle.setVisibility(View.VISIBLE);
        }else {
            holder.binding.dragHandle.setVisibility(View.GONE);
        }
        setUpYiers(holder, position);
    }

    private void setUpYiers(ViewHolder holder, int position) {
        GroupTag groupTag = groupTags.get(position);
        holder.binding.content.setOnClickListener(v -> {

        });
        holder.binding.clickViewRename.setOnClickListener(v -> {

        });
        holder.binding.clickViewDelete.setOnClickListener(v -> {
            new ConfirmDialog(activity, "是否继续？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {

                    })
                    .create().show();
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
        if(pastGroupTags == null) {
            pastGroupTags = new ArrayList<>(groupTags);
        }
        if (from < to) {
            if(to == groupTags.size()) to --;
            for (int i = from; i < to; i++) {
                Collections.swap(groupTags, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(groupTags, i, i - 1);
            }
        }
        for (int i = 0; i < groupTags.size(); i++) {
            groupTags.get(i).setOrder(i);
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void cancelMoveAndShow(){
        if(pastGroupTags != null) {
            groupTags = new ArrayList<>(pastGroupTags);
            pastGroupTags = null;
            notifyDataSetChanged();
        }
    }

    public List<GroupTag> getGroupTags() {
        return groupTags;
    }
}
