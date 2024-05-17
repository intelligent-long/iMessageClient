package com.longx.intelligent.android.ichat2.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ChannelActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.data.MessageViewed;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChatMessageBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.popupwindow.ChatMessageActionsPopupWindow;
import com.longx.intelligent.android.ichat2.ui.RecyclerViewScrollDisabler;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/5/15 at 1:11 PM.
 */
public class ChatMessagesRecyclerAdapter extends WrappableRecyclerViewAdapter<ChatMessagesRecyclerAdapter.ViewHolder, ChatMessagesRecyclerAdapter.ItemData> {
    private final AppCompatActivity activity;
    private final com.longx.intelligent.android.lib.recyclerview.RecyclerView recyclerView;
    private final List<ChatMessagesRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();

    public ChatMessagesRecyclerAdapter(AppCompatActivity activity, com.longx.intelligent.android.lib.recyclerview.RecyclerView recyclerView) {
        this.activity = activity;
        this.recyclerView = recyclerView;
    }
    private RecyclerViewScrollDisabler scrollDisabler;

    public static class ItemData{
        private final ChatMessage chatMessage;

        public ItemData(ChatMessage chatMessage) {
            this.chatMessage = chatMessage;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemChatMessageBinding binding;
        public ViewHolder(RecyclerItemChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChatMessageBinding binding = RecyclerItemChatMessageBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        //Yier
        setupYiers(holder, position);
        //时间
        if(itemData.chatMessage.isShowTime()) {
            holder.binding.time.setVisibility(View.VISIBLE);
            String timeText = TimeUtil.formatRelativeTime(itemData.chatMessage.getTime());
            holder.binding.time.setText(timeText);
        }else {
            holder.binding.time.setVisibility(View.GONE);
        }
        //发送还是接收
        if(itemData.chatMessage.isSelfSender(activity)){
            holder.binding.layoutReceive.setVisibility(View.GONE);
            holder.binding.layoutSend.setVisibility(View.VISIBLE);
            //头像
            holder.binding.avatarSend.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ChannelActivity.class);
                intent.putExtra(ExtraKeys.ICHAT_ID, itemData.chatMessage.getFrom());
                activity.startActivity(intent);
            });
            String avatarHash = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(activity).getAvatar().getHash();
            if (avatarHash == null) {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, holder.binding.avatarSend);
            } else {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), holder.binding.avatarSend);
            }
            //不同消息类型
            switch (itemData.chatMessage.getType()){
                case ChatMessage.TYPE_TEXT:{
                    holder.binding.textSend.setText(itemData.chatMessage.getText());
                }
            }
        }else {
            holder.binding.layoutReceive.setVisibility(View.VISIBLE);
            holder.binding.layoutSend.setVisibility(View.GONE);
            //头像
            holder.binding.avatarReceive.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ChannelActivity.class);
                intent.putExtra(ExtraKeys.ICHAT_ID, itemData.chatMessage.getFrom());
                activity.startActivity(intent);
            });
            String avatarHash = ChannelDatabaseManager.getInstance().findOneChannel(itemData.chatMessage.getFrom()).getAvatar().getHash();
            if (avatarHash == null) {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, holder.binding.avatarReceive);
            } else {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), holder.binding.avatarReceive);
            }
            //不同消息类型
            switch (itemData.chatMessage.getType()){
                case ChatMessage.TYPE_TEXT:{
                    holder.binding.textReceive.setText(itemData.chatMessage.getText());
                }
            }
        }
    }

    private void setupYiers(@NonNull ViewHolder holder, int position) {
        scrollDisabler = new RecyclerViewScrollDisabler();
        recyclerView.addOnItemTouchListener(scrollDisabler);
        ChatMessageActionsPopupWindow popupWindow = new ChatMessageActionsPopupWindow(activity);
        holder.binding.layoutTextReceive.setOnLongClickListener(v -> {
            scrollDisabler.setScrollingDisabled(true);
            popupWindow.show(holder.binding.layoutTextReceive, false);
            return true;
        });
        holder.binding.layoutTextSend.setOnLongClickListener(v -> {
            scrollDisabler.setScrollingDisabled(true);
            popupWindow.show(holder.binding.layoutTextSend, true);
            return true;
        });
        popupWindow.getPopupWindow().setOnDismissListener(() -> {
            scrollDisabler.setScrollingDisabled(false);
        });
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    private static void sort(List<ItemData> itemDataList) {
        itemDataList.sort(Comparator.comparing(o -> o.chatMessage.getTime()));
    }

    public synchronized void addItemToEndAndShow(ChatMessage chatMessage){
        ItemData itemData = new ItemData(chatMessage);
        itemDataList.add(itemData);
        notifyItemInserted(getItemCount() - 1);
    }

    public synchronized void addAllToStartAndShow(List<ChatMessage> chatMessages){
        List<ItemData> itemDatas = new ArrayList<>();
        chatMessages.forEach(chatMessage -> {
            itemDatas.add(new ItemData(chatMessage));
        });
        sort(itemDatas);
        itemDataList.addAll(0, itemDatas);
        notifyItemRangeInserted(0, itemDatas.size());
    }

    public void clearAndShow(){
        int size = itemDataList.size();
        itemDataList.clear();
        notifyItemRangeRemoved(0, size);
    }

}
