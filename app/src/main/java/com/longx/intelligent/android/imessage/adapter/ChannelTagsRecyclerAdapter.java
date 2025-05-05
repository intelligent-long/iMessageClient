package com.longx.intelligent.android.imessage.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.TagChannelsActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.bottomsheet.RenameChannelTagBottomSheet;
import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemChannelTagBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
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
public class  ChannelTagsRecyclerAdapter extends WrappableRecyclerViewAdapter<ChannelTagsRecyclerAdapter.ViewHolder, List<ChannelTag>> {
    private final AppCompatActivity activity;
    private List<ChannelTag> pastChannelTags;
    private List<ChannelTag> channelTags;
    private boolean dragSortState;

    public ChannelTagsRecyclerAdapter(AppCompatActivity activity, List<ChannelTag> channelTags) {
        this.activity = activity;
        this.channelTags = channelTags;
        channelTags.sort(Comparator.comparingInt(ChannelTag::getOrder).reversed());
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
        holder.binding.content.setOnClickListener(v -> {
            Intent intent = new Intent(activity, TagChannelsActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL_TAG_ID, channelTag.getTagId());
            activity.startActivity(intent);
        });
        holder.binding.clickViewRename.setOnClickListener(v -> {
            new RenameChannelTagBottomSheet(activity, channelTag).show();
        });
        holder.binding.clickViewDelete.setOnClickListener(v -> {
            new ConfirmDialog(activity, "是否继续？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        ChannelApiCaller.deleteChannelTag(activity, channelTag.getTagId(), new RetrofitApiCaller.CommonYier<OperationStatus>(activity){
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
        if(pastChannelTags == null) {
            pastChannelTags = new ArrayList<>(channelTags);
        }
        if (from < to) {
            if(to == channelTags.size()) to --;
            for (int i = from; i < to; i++) {
                Collections.swap(channelTags, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(channelTags, i, i - 1);
            }
        }
        for (int i = 0; i < channelTags.size(); i++) {
            channelTags.get(i).setOrder(channelTags.size() - 1 - i);
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
