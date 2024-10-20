package com.longx.intelligent.android.ichat2.fragment.forwardmessage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.longx.intelligent.android.ichat2.adapter.ChannelsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.adapter.ForwardMessageChannelsLinearLayoutViews;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChannelAssociation;
import com.longx.intelligent.android.ichat2.databinding.FragmentForwardMessageChannelsBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LONG on 2024/10/19 at 上午10:05.
 */
public class ForwardMessageChannelsFragment extends Fragment {
    private FragmentForwardMessageChannelsBinding binding;
    private ForwardMessageChannelsLinearLayoutViews linearLayoutViews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForwardMessageChannelsBinding.inflate(inflater, container, false);
        init();
        showContent();
        return binding.getRoot();
    }

    private void init() {
        linearLayoutViews = new ForwardMessageChannelsLinearLayoutViews(requireActivity(), binding.linearLayoutViews, binding.scrollView);
    }

    private void showContent() {
        List<ForwardMessageChannelsLinearLayoutViews.ItemData> itemDataList = new ArrayList<>();
        List<ChannelAssociation> channelAssociations = ChannelDatabaseManager.getInstance().findAllAssociations();
        channelAssociations.forEach(channelAssociation -> {
            itemDataList.add(new ForwardMessageChannelsLinearLayoutViews.ItemData(channelAssociation.getChannel()));
        });
        if(itemDataList.isEmpty()){
            binding.noContent.setVisibility(View.VISIBLE);
            binding.linearLayoutViews.setVisibility(View.GONE);
        }else {
            linearLayoutViews.addItemsAndShow(itemDataList);
        }
    }

    public Set<String> getCheckedChannelIds() {
        if(linearLayoutViews == null) return new HashSet<>();
        return linearLayoutViews.getCheckedChannelIds();
    }
}
