package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.GroupChannelRemoveActivity;
import com.longx.intelligent.android.imessage.activity.GroupMembersActivity;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemChannelBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupMembersBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemRemoveGroupChannelBinding;
import com.longx.intelligent.android.imessage.dialog.FastLocateDialog;
import com.longx.intelligent.android.imessage.fragment.main.ChannelsFragment;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2025/6/9 at 上午2:39.
 */
public class RemoveGroupChannelRecyclerAdapter extends WrappableRecyclerViewAdapter<RemoveGroupChannelRecyclerAdapter.ViewHolder, RemoveGroupChannelRecyclerAdapter.ItemData> {
    private final GroupChannelRemoveActivity activity;
    private final List<RemoveGroupChannelRecyclerAdapter.ItemData> itemDataList;
    private List<Channel> checkedChannel = new ArrayList<>();

    public RemoveGroupChannelRecyclerAdapter(GroupChannelRemoveActivity activity, List<RemoveGroupChannelRecyclerAdapter.ItemData> itemDataList) {
        this.activity = activity;
        this.itemDataList = itemDataList;
        itemDataList.sort((o1, o2) -> {
            if (o1.indexChar == '#' && o2.indexChar != '#') return 1;
            if (o1.indexChar != '#' && o2.indexChar == '#') return -1;
            if (o1.indexChar == '#' && o2.indexChar == '#') return 0;
            int pinyinCompare = o1.fullPinyin.compareToIgnoreCase(o2.fullPinyin);
            if (pinyinCompare != 0) return pinyinCompare;
            return o1.fullPinyin.compareTo(o2.fullPinyin);
        });
    }

    public static class ItemData {
        private final String fullPinyin;
        private final char indexChar;
        private final Channel channel;

        public ItemData(Channel channel) {
            this.channel = channel;
            String name = channel.autoGetName();
            this.fullPinyin = PinyinUtil.getPinyin(name);
            char firstChar = fullPinyin.charAt(0);
            if (Character.isLetter(firstChar)) {
                indexChar = Character.toUpperCase(firstChar);
            } else {
                indexChar = '#';
            }
        }

        public String getFullPinyin() {
            return fullPinyin;
        }

        public char getIndexChar() {
            return indexChar;
        }

        public Channel getChannel() {
            return channel;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemRemoveGroupChannelBinding binding;
        public ViewHolder(RecyclerItemRemoveGroupChannelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemRemoveGroupChannelBinding binding = RecyclerItemRemoveGroupChannelBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        String avatarHash = itemData.channel.getAvatar() == null ? null : itemData.channel.getAvatar().getHash();
        if (avatarHash == null) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(holder.binding.avatar);
        } else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(activity, avatarHash))
                    .into(holder.binding.avatar);
        }
        holder.binding.indexBar.setText(String.valueOf(itemData.indexChar));
        int previousPosition = position - 1;
        if(position == 0){
            holder.binding.indexBar.setVisibility(View.VISIBLE);
        } else {
            ItemData previousItemData = itemDataList.get(previousPosition);
            if (previousItemData.indexChar == itemData.indexChar) {
                holder.binding.indexBar.setVisibility(View.GONE);
            } else {
                holder.binding.indexBar.setVisibility(View.VISIBLE);
            }
        }
        holder.binding.name.setText(itemData.channel.autoGetName());
        setupYiers(holder, position);
    }

    private void setupYiers(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        holder.binding.clickView.setOnClickListener(v -> {
            getOnItemClickYier().onItemClick(position, itemData);
        });
        holder.binding.indexBar.setOnClickListener(v -> {
            FastLocateDialog fastLocateDialog = new FastLocateDialog(activity, FastLocateDialog.LOCATE_HEADER_CHANNEL, getExistTexts());
            fastLocateDialog.setLocateYier((positionSelect, textSelect) -> {
                int locatePosition = -1;
                if(textSelect.equals(".")){
                    locatePosition = 0;
                }else {
                    for (int i = 0; i < itemDataList.size(); i++) {
                        ItemData data = itemDataList.get(i);
                        if (String.valueOf(data.indexChar).equals(textSelect)) {
                            locatePosition = i + 1;
                            break;
                        }
                    }
                }
                if(locatePosition != -1) {
                    activity.getBinding().appBar.setExpanded(locatePosition == 0);
                    activity.getBinding().recyclerView.smoothScrollToPosition(locatePosition);
                }
                fastLocateDialog.dismiss();
            });
            fastLocateDialog.create().show();
        });
        holder.binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                checkedChannel.add(itemData.channel);
            }else {
                checkedChannel.remove(itemData.channel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    private String[] getExistTexts(){
        String[] result = new String[getItemCount() + 1];
        for (int i = 0; i < itemDataList.size(); i++) {
            result[i] = String.valueOf(itemDataList.get(i).indexChar);
        }
        result[result.length - 1] = ".";
        return result;
    }

    public List<Channel> getCheckedChannel() {
        return checkedChannel;
    }
}
