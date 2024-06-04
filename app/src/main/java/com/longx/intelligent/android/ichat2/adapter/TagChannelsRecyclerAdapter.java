package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ChannelActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.data.request.RemoveChannelsOfTagPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChannelBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/4/25 at 5:35 PM.
 */
public class TagChannelsRecyclerAdapter extends WrappableRecyclerViewAdapter<TagChannelsRecyclerAdapter.ViewHolder, TagChannelsRecyclerAdapter.ItemData> {
    private final AppCompatActivity activity;
    private final ChannelTag channelTag;
    private final List<ItemData> itemDataList;

    public TagChannelsRecyclerAdapter(AppCompatActivity activity, ChannelTag channelTag, List<Channel> channels) {
        this.activity = activity;
        this.channelTag = channelTag;
        this.itemDataList = new ArrayList<>();
        channels.forEach(channel -> {
            this.itemDataList.add(new ItemData(channel));
        });
        itemDataList.sort(Comparator.comparing(o -> o.indexChar));
    }

    public static class ItemData{
        private Character indexChar;
        private Channel channel;

        public ItemData(Channel channel) {
            indexChar = PinyinUtil.getPinyin(channel.getUsername()).toUpperCase().charAt(0);
            if(!((indexChar >= 65 && indexChar <= 90) || (indexChar >= 97 && indexChar <= 122))){
                indexChar = '#';
            }
            this.channel = channel;
        }

        public Character getIndexChar() {
            return indexChar;
        }

        public Channel getChannel() {
            return channel;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemChannelBinding binding;
        public ViewHolder(RecyclerItemChannelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public TagChannelsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChannelBinding binding = RecyclerItemChannelBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull TagChannelsRecyclerAdapter.ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        String avatarHash = itemData.channel.getAvatar() == null ? null : itemData.channel.getAvatar().getHash();
        if (avatarHash == null) {
            GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, holder.binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), holder.binding.avatar);
        }
        holder.binding.indexBar.setText(String.valueOf(itemData.indexChar));
        int previousPosition = position - 1;
        if(position == 0){
            holder.binding.indexBar.setVisibility(View.VISIBLE);
        } else {
            ItemData previousItemData = itemDataList.get(previousPosition);
            if (previousItemData.indexChar.equals(itemData.indexChar)) {
                holder.binding.indexBar.setVisibility(View.GONE);
            } else {
                holder.binding.indexBar.setVisibility(View.VISIBLE);
            }
        }
        holder.binding.name.setText(itemData.channel.getNote() == null ? itemData.channel.getUsername() : itemData.channel.getNote());
        setupYiers(holder, position);
    }

    private void setupYiers(TagChannelsRecyclerAdapter.ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        holder.binding.clickView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.ICHAT_ID, itemData.channel.getIchatId());
            intent.putExtra(ExtraKeys.CHANNEL, itemData.channel);
            activity.startActivity(intent);
        });
        holder.binding.clickViewRemove.setOnClickListener(v -> {
            new ConfirmDialog(activity, "是否继续？")
                    .setNegativeButton(null)
                    .setPositiveButton((dialog, which) -> {
                        List<String> channelIchatIdList = new ArrayList<>();
                        channelIchatIdList.add(itemData.channel.getIchatId());
                        RemoveChannelsOfTagPostBody postBody = new RemoveChannelsOfTagPostBody(channelTag.getId(), channelIchatIdList);
                        ChannelApiCaller.removeChannelsOfTag(activity, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(activity){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                                super.ok(data, row, call);
                                data.commonHandleResult(activity, new int[]{}, () -> {
                                    MessageDisplayer.autoShow(activity, "已移除", MessageDisplayer.Duration.SHORT);
                                });
                            }
                        });
                    })
                    .show();
        });
    }
}
