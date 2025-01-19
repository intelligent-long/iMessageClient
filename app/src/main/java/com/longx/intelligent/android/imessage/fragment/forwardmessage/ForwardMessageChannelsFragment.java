package com.longx.intelligent.android.imessage.fragment.forwardmessage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.longx.intelligent.android.imessage.adapter.ForwardMessageChannelsLinearLayoutViews;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.ChannelAssociation;
import com.longx.intelligent.android.imessage.databinding.FragmentForwardMessageChannelsBinding;

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
            itemDataList.sort((o1, o2) -> {
                if (o1.getIndexChar() == '#') return 1;
                if (o2.getIndexChar() == '#') return -1;
                return Character.compare(o1.getIndexChar(), o2.getIndexChar());
            });
            linearLayoutViews.addItemsAndShow(itemDataList);
        }
    }

    public Set<String> getCheckedChannelIds() {
        if(linearLayoutViews == null) return new HashSet<>();
        return linearLayoutViews.getCheckedChannelIds();
    }
}
