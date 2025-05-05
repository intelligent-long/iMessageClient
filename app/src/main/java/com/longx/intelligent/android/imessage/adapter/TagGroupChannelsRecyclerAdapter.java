package com.longx.intelligent.android.imessage.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.GroupChannelActivity;
import com.longx.intelligent.android.imessage.activity.TagGroupChannelsActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.data.request.RemoveGroupChannelsOfTagPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemTagGroupChannelBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.FastLocateDialog;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
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
public class TagGroupChannelsRecyclerAdapter extends WrappableRecyclerViewAdapter<TagGroupChannelsRecyclerAdapter.ViewHolder, TagGroupChannelsRecyclerAdapter.ItemData> {
    private final TagGroupChannelsActivity tagGroupChannelsActivity;
    private final GroupChannelTag groupChannelTag;
    private final List<ItemData> itemDataList;

    public TagGroupChannelsRecyclerAdapter(TagGroupChannelsActivity tagGroupChannelsActivity, GroupChannelTag groupChannelTag, List<GroupChannel> groupChannels) {
        this.tagGroupChannelsActivity = tagGroupChannelsActivity;
        this.groupChannelTag = groupChannelTag;
        this.itemDataList = new ArrayList<>();
        groupChannels.forEach(channel -> {
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
        private GroupChannel groupChannel;

        public ItemData(GroupChannel groupChannel) {
            indexChar = PinyinUtil.getPinyin(groupChannel.autoGetName()).toUpperCase().charAt(0);
            if(!((indexChar >= 65 && indexChar <= 90) || (indexChar >= 97 && indexChar <= 122))){
                indexChar = '#';
            }
            this.groupChannel = groupChannel;
        }

        public Character getIndexChar() {
            return indexChar;
        }

        public GroupChannel getGroupChannel() {
            return groupChannel;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemTagGroupChannelBinding binding;
        public ViewHolder(RecyclerItemTagGroupChannelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public TagGroupChannelsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemTagGroupChannelBinding binding = RecyclerItemTagGroupChannelBinding.inflate(tagGroupChannelsActivity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull TagGroupChannelsRecyclerAdapter.ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        String avatarHash = itemData.groupChannel.getGroupAvatar() == null ? null : itemData.groupChannel.getGroupAvatar().getHash();
        if (avatarHash == null) {
            GlideBehaviours.loadToImageView(tagGroupChannelsActivity.getApplicationContext(), R.drawable.group_channel_default_avatar, holder.binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(tagGroupChannelsActivity.getApplicationContext(), NetDataUrls.getGroupAvatarUrl(tagGroupChannelsActivity, avatarHash), holder.binding.avatar);
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
        holder.binding.name.setText(itemData.groupChannel.getNote() == null ? itemData.groupChannel.getName() : itemData.groupChannel.getNote());
        setupYiers(holder, position);
    }

    private void setupYiers(TagGroupChannelsRecyclerAdapter.ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        holder.binding.clickView.setOnClickListener(v -> {
            Intent intent = new Intent(tagGroupChannelsActivity, GroupChannelActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL, itemData.groupChannel);
            tagGroupChannelsActivity.startActivity(intent);
        });
        holder.binding.clickViewRemove.setOnClickListener(v -> {
            new ConfirmDialog(tagGroupChannelsActivity, "是否继续？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        List<String> groupChannelIdList = new ArrayList<>();
                        groupChannelIdList.add(itemData.groupChannel.getGroupChannelId());
                        RemoveGroupChannelsOfTagPostBody postBody = new RemoveGroupChannelsOfTagPostBody(groupChannelTag.getTagId(), groupChannelIdList);
                        GroupChannelApiCaller.removeGroupChannelsOfTag(tagGroupChannelsActivity, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(tagGroupChannelsActivity){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                super.ok(data, raw, call);
                                data.commonHandleResult(tagGroupChannelsActivity, new int[]{}, () -> {
                                    MessageDisplayer.autoShow(tagGroupChannelsActivity, "已移除", MessageDisplayer.Duration.SHORT);
                                });
                            }
                        });
                    })
                    .create().show();
        });
        holder.binding.indexBar.setOnClickListener(v -> {
            FastLocateDialog fastLocateDialog = new FastLocateDialog(tagGroupChannelsActivity, FastLocateDialog.LOCATE_CHANNEL, getExistTexts());
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
                    tagGroupChannelsActivity.getBinding().appbar.setExpanded(false);
                    tagGroupChannelsActivity.getBinding().recyclerView.smoothScrollToPosition(locatePosition);
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
