package com.longx.intelligent.android.ichat2.fragment.forwardmessage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.longx.intelligent.android.ichat2.adapter.ForwardMessageMessagesLinearLayoutViews;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.databinding.FragmentForwardMessageMessagesBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/10/19 at 上午10:05.
 */
public class ForwardMessageMessagesFragment extends Fragment {
    private FragmentForwardMessageMessagesBinding binding;
    private ForwardMessageMessagesLinearLayoutViews linearLayoutViews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForwardMessageMessagesBinding.inflate(inflater, container, false);
        init();
        showContent();
        return binding.getRoot();
    }

    private void init() {
        linearLayoutViews = new ForwardMessageMessagesLinearLayoutViews(requireActivity(), binding.linearLayoutViews, binding.scrollView);
    }

    private void showContent() {
        List<OpenedChat> allShowOpenedChats = OpenedChatDatabaseManager.getInstance().findAllShow();
        List<OpenedChat> toHideOpenedChats = new ArrayList<>();
        allShowOpenedChats.forEach(openedChat -> {
            String channelIchatId = openedChat.getChannelIchatId();
            if(ChannelDatabaseManager.getInstance().findOneChannel(channelIchatId) == null){
                toHideOpenedChats.add(openedChat);
            }
        });
        allShowOpenedChats.removeAll(toHideOpenedChats);
        allShowOpenedChats.forEach(openedChat -> {
            ChatMessageDatabaseManager chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(requireContext(), openedChat.getChannelIchatId());
            List<ChatMessage> limit = chatMessageDatabaseManager.findLimit(0, 1, true);
            if (limit.size() == 1) {
                openedChat.setNewestChatMessage(limit.get(0));
                Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(openedChat.getChannelIchatId());
                openedChat.setChannel(channel);
            }
        });
        allShowOpenedChats.sort((o1, o2) -> -o1.getNewestChatMessage().getTime().compareTo(o2.getNewestChatMessage().getTime()));
        if(allShowOpenedChats.isEmpty()){
            binding.noContent.setVisibility(View.VISIBLE);
            binding.linearLayoutViews.setVisibility(View.GONE);
        }else {
            linearLayoutViews.addItemsAndShow(allShowOpenedChats);
        }
    }

}
