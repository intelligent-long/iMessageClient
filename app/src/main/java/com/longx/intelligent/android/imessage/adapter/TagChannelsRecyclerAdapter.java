package com.longx.intelligent.android.imessage.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ChannelActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.TagChannelsActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.data.request.RemoveChannelsOfTagPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemTagChannelBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.FastLocateDialog;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/4/25 at 5:35 PM.
 */
public class TagChannelsRecyclerAdapter extends WrappableRecyclerViewAdapter<TagChannelsRecyclerAdapter.ViewHolder, TagChannelsRecyclerAdapter.ItemData> {
    private final TagChannelsActivity tagChannelsActivity;
    private final ChannelTag channelTag;
    private final List<ItemData> itemDataList;

    public TagChannelsRecyclerAdapter(TagChannelsActivity tagChannelsActivity, ChannelTag channelTag, List<Channel> channels) {
        this.tagChannelsActivity = tagChannelsActivity;
        this.channelTag = channelTag;
        this.itemDataList = new ArrayList<>();
        channels.forEach(channel -> {
            this.itemDataList.add(new ItemData(channel));
        });
        itemDataList.sort((o1, o2) -> {
            if (o1.indexChar == '#') return 1;
            if (o2.indexChar == '#') return -1;
            return Character.compare(o1.indexChar, o2.indexChar);
        });
    }

    public static class ItemData{
        private Character indexChar;
        private Channel channel;

        public ItemData(Channel channel) {
            indexChar = PinyinUtil.getPinyin(channel.autoGetName()).toUpperCase().charAt(0);
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
        private RecyclerItemTagChannelBinding binding;
        public ViewHolder(RecyclerItemTagChannelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public TagChannelsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemTagChannelBinding binding = RecyclerItemTagChannelBinding.inflate(tagChannelsActivity.getLayoutInflater());
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
            GlideBehaviours.loadToImageView(tagChannelsActivity.getApplicationContext(), R.drawable.default_avatar, holder.binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(tagChannelsActivity.getApplicationContext(), NetDataUrls.getAvatarUrl(tagChannelsActivity, avatarHash), holder.binding.avatar);
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
            Intent intent = new Intent(tagChannelsActivity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.IMESSAGE_ID, itemData.channel.getImessageId());
            intent.putExtra(ExtraKeys.CHANNEL, itemData.channel);
            tagChannelsActivity.startActivity(intent);
        });
        holder.binding.clickViewRemove.setOnClickListener(v -> {
            new ConfirmDialog(tagChannelsActivity, "是否继续？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        List<String> channelImessageIdList = new ArrayList<>();
                        channelImessageIdList.add(itemData.channel.getImessageId());
                        RemoveChannelsOfTagPostBody postBody = new RemoveChannelsOfTagPostBody(channelTag.getTagId(), channelImessageIdList);
                        ChannelApiCaller.removeChannelsOfTag(tagChannelsActivity, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(tagChannelsActivity){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                super.ok(data, raw, call);
                                data.commonHandleResult(tagChannelsActivity, new int[]{}, () -> {
                                    MessageDisplayer.autoShow(tagChannelsActivity, "已移除", MessageDisplayer.Duration.SHORT);
                                });
                            }
                        });
                    })
                    .create().show();
        });
        holder.binding.indexBar.setOnClickListener(v -> {
            FastLocateDialog fastLocateDialog = new FastLocateDialog(tagChannelsActivity, FastLocateDialog.LOCATE_CHANNEL, getExistTexts());
            fastLocateDialog.setLocateYier((positionSelect, textSelect) -> {
                int locatePosition = -1;
                for (int i = 0; i < itemDataList.size(); i++) {
                    ItemData data = itemDataList.get(i);
                    if (String.valueOf(data.indexChar).equals(textSelect)) {
                        locatePosition = i;
                        break;
                    }
                }
                if(locatePosition != -1) {
                    tagChannelsActivity.getBinding().appbar.setExpanded(false);
                    tagChannelsActivity.getBinding().recyclerView.smoothScrollToPosition(locatePosition);
                }
                fastLocateDialog.dismiss();
            });
            fastLocateDialog.create().show();
        });
    }

    private String[] getExistTexts(){
        String[] result = new String[getItemCount()];
        for (int i = 0; i < itemDataList.size(); i++) {
            result[i] = String.valueOf(itemDataList.get(i).indexChar);
        }
        return result;
    }
}
