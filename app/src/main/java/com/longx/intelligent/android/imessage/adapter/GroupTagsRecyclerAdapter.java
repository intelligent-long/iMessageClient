package com.longx.intelligent.android.imessage.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.bottomsheet.RenameChannelTagBottomSheet;
import com.longx.intelligent.android.imessage.bottomsheet.RenameGroupChannelTagBottomSheet;
import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupTagBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
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
public class GroupTagsRecyclerAdapter extends WrappableRecyclerViewAdapter<GroupTagsRecyclerAdapter.ViewHolder, List<GroupChannelTag>> {
    private final AppCompatActivity activity;
    private List<GroupChannelTag> pastGroupChannelTags;
    private List<GroupChannelTag> groupChannelTags;
    private boolean dragSortState;

    public GroupTagsRecyclerAdapter(AppCompatActivity activity, List<GroupChannelTag> groupChannelTags) {
        this.activity = activity;
        this.groupChannelTags = groupChannelTags;
        groupChannelTags.sort(Comparator.comparingInt(GroupChannelTag::getOrder).reversed());
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
        return groupChannelTags.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupChannelTag groupChannelTag = groupChannelTags.get(position);
        holder.binding.groupTagName.setText(groupChannelTag.getName());
        if(dragSortState) {
            holder.binding.dragHandle.setVisibility(View.VISIBLE);
        }else {
            holder.binding.dragHandle.setVisibility(View.GONE);
        }
        setUpYiers(holder, position);
    }

    private void setUpYiers(ViewHolder holder, int position) {
        GroupChannelTag groupChannelTag = groupChannelTags.get(position);
        holder.binding.content.setOnClickListener(v -> {

        });
        holder.binding.clickViewRename.setOnClickListener(v -> {
            new RenameGroupChannelTagBottomSheet(activity, groupChannelTag).show();
        });
        holder.binding.clickViewDelete.setOnClickListener(v -> {
            new ConfirmDialog(activity, "是否继续？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        GroupChannelApiCaller.deleteGroupChannelTag(activity, groupChannelTag.getTagId(), new RetrofitApiCaller.CommonYier<OperationStatus>(activity){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                super.ok(data, raw, call);
                                data.commonHandleResult(activity, new int[]{}, () -> {
                                    MessageDisplayer.autoShow(activity, "已删除", MessageDisplayer.Duration.SHORT);
                                });
                            }
                        });
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
        if(pastGroupChannelTags == null) {
            pastGroupChannelTags = new ArrayList<>(groupChannelTags);
        }
        if (from < to) {
            if(to == groupChannelTags.size()) to --;
            for (int i = from; i < to; i++) {
                Collections.swap(groupChannelTags, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(groupChannelTags, i, i - 1);
            }
        }
        for (int i = 0; i < groupChannelTags.size(); i++) {
            groupChannelTags.get(i).setOrder(groupChannelTags.size() - 1 - i);
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void cancelMoveAndShow(){
        if(pastGroupChannelTags != null) {
            groupChannelTags = new ArrayList<>(pastGroupChannelTags);
            pastGroupChannelTags = null;
            notifyDataSetChanged();
        }
    }

    public List<GroupChannelTag> getGroupTags() {
        return groupChannelTags;
    }
}
