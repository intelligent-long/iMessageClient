package com.longx.intelligent.android.imessage.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy.A_TO_Z;
import static com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy.Z_TO_A;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ChannelActivity;
import com.longx.intelligent.android.imessage.activity.ChannelCollectionActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.GroupChannelActivity;
import com.longx.intelligent.android.imessage.activity.GroupChannelCollectionActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemChannelCollectionBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupChannelCollectionBinding;
import com.longx.intelligent.android.imessage.dialog.FastLocateDialog;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by LONG on 2025/6/26 at 上午3:40.
 */
public class GroupChannelCollectionAdapter extends WrappableRecyclerViewAdapter<GroupChannelCollectionAdapter.ViewHolder, GroupChannelCollectionAdapter.ItemData> {
    private final GroupChannelCollectionActivity activity;
    private final com.longx.intelligent.android.lib.recyclerview.RecyclerView recyclerView;
    private List<ItemData> itemDataList;
    private SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy sortBy;
    private List<ItemData> pastItemDatas;
    private boolean dragSortState;

    public GroupChannelCollectionAdapter(GroupChannelCollectionActivity activity, List<ItemData> itemDataList) {
        this.activity = activity;
        this.recyclerView = activity.getBinding().recyclerView;
        this.itemDataList = itemDataList;
    }

    public static class ItemData {
        private final String fullPinyin;
        private final char indexChar;
        private final GroupChannel groupChannel;
        private final Date addedAt;
        private int order;
        private final String uuid;

        public ItemData(GroupChannel groupChannel, Date addedAt, int order, String uuid) {
            this.groupChannel = groupChannel;
            this.addedAt = addedAt;
            String name = groupChannel.autoGetName();
            this.fullPinyin = PinyinUtil.getPinyin(name);
            char firstChar = fullPinyin.charAt(0);
            if (Character.isLetter(firstChar)) {
                indexChar = Character.toUpperCase(firstChar);
            } else {
                indexChar = '#';
            }
            this.order = order;
            this.uuid = uuid;
        }

        public String getFullPinyin() {
            return fullPinyin;
        }

        public char getIndexChar() {
            return indexChar;
        }

        public GroupChannel getGroupChannel() {
            return groupChannel;
        }

        public Date getAddedAt() {
            return addedAt;
        }

        public int getOrder() {
            return order;
        }

        public String getUuid() {
            return uuid;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemGroupChannelCollectionBinding binding;
        public ViewHolder(RecyclerItemGroupChannelCollectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemGroupChannelCollectionBinding binding = RecyclerItemGroupChannelCollectionBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        String avatarHash = itemData.groupChannel.getAvatarHash() == null ? null : itemData.groupChannel.getAvatarHash();
        if (avatarHash == null) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(holder.binding.avatar);
        } else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getGroupAvatarUrl(activity, avatarHash))
                    .into(holder.binding.avatar);
        }
        holder.binding.name.setText(itemData.groupChannel.autoGetName());
        if(sortBy != null && !sortBy.equals(A_TO_Z) && !sortBy.equals(Z_TO_A)){
            holder.binding.indexBar.setVisibility(GONE);
        }else {
            holder.binding.indexBar.setText(String.valueOf(itemData.indexChar));
            int previousPosition = position - 1;
            if(position == 0){
                holder.binding.indexBar.setVisibility(VISIBLE);
            } else {
                ItemData previousItemData = itemDataList.get(previousPosition);
                if (previousItemData.indexChar == itemData.indexChar) {
                    holder.binding.indexBar.setVisibility(GONE);
                } else {
                    holder.binding.indexBar.setVisibility(VISIBLE);
                }
            }
        }
        if(dragSortState) {
            holder.binding.dragHandle.setVisibility(View.VISIBLE);
        }else {
            holder.binding.dragHandle.setVisibility(View.GONE);
        }
        setupYiers(holder, position);
    }

    private void setupYiers(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        holder.binding.clickView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, GroupChannelActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL, itemData.groupChannel);
            activity.startActivity(intent);
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
                    activity.getBinding().appbar.setExpanded(locatePosition == 0);
                    recyclerView.smoothScrollToPosition(locatePosition);
                }
                fastLocateDialog.dismiss();
            });
            fastLocateDialog.create().show();
        });
    }

    private String[] getExistTexts(){
        String[] result = new String[getItemCount() + 1];
        for (int i = 0; i < itemDataList.size(); i++) {
            result[i] = String.valueOf(itemDataList.get(i).indexChar);
        }
        result[result.length - 1] = ".";
        return result;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void sort(SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy sortBy){
        this.sortBy = sortBy;
        switch (sortBy) {
            case CUSTOM -> {
                itemDataList.sort((o1, o2) -> Integer.compare(o2.order, o1.order));
            }
            case NEW_TO_OLD -> {
                itemDataList.sort((o1, o2) -> {
                    long t1 = o1.getAddedAt().getTime();
                    long t2 = o2.getAddedAt().getTime();
                    return Long.compare(t2, t1);
                });
            }
            case OLD_TO_NEW -> {
                itemDataList.sort((o1, o2) -> {
                    long t1 = o1.getAddedAt().getTime();
                    long t2 = o2.getAddedAt().getTime();
                    return Long.compare(t1, t2);
                });
            }
            case A_TO_Z -> {
                itemDataList.sort((o1, o2) -> {
                    if (o1.indexChar == '#' && o2.indexChar != '#') return 1;
                    if (o1.indexChar != '#' && o2.indexChar == '#') return -1;
                    if (o1.indexChar == '#' && o2.indexChar == '#') return 0;
                    int pinyinCompare = o1.fullPinyin.compareToIgnoreCase(o2.fullPinyin);
                    if (pinyinCompare != 0) return pinyinCompare;
                    return o1.fullPinyin.compareTo(o2.fullPinyin);
                });
            }
            case Z_TO_A -> {
                itemDataList.sort((o1, o2) -> {
                    if (o1.indexChar == '#' && o2.indexChar != '#') return 1;
                    if (o1.indexChar != '#' && o2.indexChar == '#') return -1;
                    if (o1.indexChar == '#' && o2.indexChar == '#') return 0;
                    int pinyinCompare = o2.fullPinyin.compareToIgnoreCase(o1.fullPinyin);
                    if (pinyinCompare != 0) return pinyinCompare;
                    return o2.fullPinyin.compareTo(o1.fullPinyin);
                });
            }
        }
        notifyDataSetChanged();
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
        if(pastItemDatas == null) {
            pastItemDatas = new ArrayList<>(itemDataList);
        }
        if (from < to) {
            if(to == itemDataList.size()) to --;
            for (int i = from; i < to; i++) {
                Collections.swap(itemDataList, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(itemDataList, i, i - 1);
            }
        }
        for (int i = 0; i < itemDataList.size(); i++) {
            itemDataList.get(i).order = itemDataList.size() - 1 - i;
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void cancelMoveAndShow(){
        if(pastItemDatas != null) {
            itemDataList = new ArrayList<>(pastItemDatas);
            pastItemDatas = null;
            notifyDataSetChanged();
        }
    }

    public List<ItemData> getItemDataList() {
        return itemDataList;
    }
}
