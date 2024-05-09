package com.longx.intelligent.android.ichat2.fragment.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.activity.ChannelActivity;
import com.longx.intelligent.android.ichat2.activity.ChannelAdditionActivitiesActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.SearchChannelActivity;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelsDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelAssociation;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.adapter.ChannelsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.databinding.FragmentChannelsBinding;
import com.longx.intelligent.android.ichat2.databinding.LayoutChannelRecyclerViewHeaderBinding;
import com.longx.intelligent.android.ichat2.ui.BadgeInitiator;
import com.longx.intelligent.android.ichat2.yier.ChannelsUpdateYier;
import com.longx.intelligent.android.ichat2.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;

public class ChannelsFragment extends BaseMainFragment implements WrappableRecyclerViewAdapter.OnItemClickYier<ChannelsRecyclerAdapter.ItemData>, ContentUpdater.OnServerContentUpdateYier, NewContentBadgeDisplayYier, ChannelsUpdateYier {
    private FragmentChannelsBinding binding;
    private LayoutChannelRecyclerViewHeaderBinding headerViewBinding;
    private int channelAdditionActivitiesNewContentCount;
    private Badge newChannelBadge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalYiersHolder.holdYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.holdYier(requireContext(), NewContentBadgeDisplayYier.class, this, ID.CHANNEL_ADDITION_ACTIVITIES);
        GlobalYiersHolder.holdYier(requireContext(), ChannelsUpdateYier.class, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.removeYier(requireContext(), NewContentBadgeDisplayYier.class, this, ID.CHANNEL_ADDITION_ACTIVITIES);
        GlobalYiersHolder.removeYier(requireContext(), ChannelsUpdateYier.class, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChannelsBinding.inflate(inflater, container, false);
        setupRecyclerView(inflater);
        setupYiers();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbarNavIcon(binding.toolbar);
    }

    @Override
    public Toolbar getToolbar() {
        return binding == null ? null : binding.toolbar;
    }

    private void setupRecyclerView(LayoutInflater inflater) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        ArrayList<ChannelsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        Self self = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(requireContext());
        Channel selfChannel = new Channel(
                self.getIchatId(),
                self.getIchatIdUser(),
                self.getEmail(),
                self.getUsername(),
                self.getAvatar(),
                self.getSex(),
                self.getFirstRegion(),
                self.getSecondRegion(),
                self.getThirdRegion(),
                true);
        itemDataList.add(new ChannelsRecyclerAdapter.ItemData(selfChannel));
        List<ChannelAssociation> channelAssociations = ChannelsDatabaseManager.getInstance().findAll();
        channelAssociations.forEach(channelAssociation -> {
            itemDataList.add(new ChannelsRecyclerAdapter.ItemData(channelAssociation.getChannel()));
        });
        ChannelsRecyclerAdapter channelsRecyclerAdapter = new ChannelsRecyclerAdapter(requireActivity(), itemDataList);
        channelsRecyclerAdapter.setOnItemClickYier(this);
        binding.recyclerView.setAdapter(channelsRecyclerAdapter);
        headerViewBinding = LayoutChannelRecyclerViewHeaderBinding.inflate(inflater);
        newChannelBadge = BadgeInitiator.initBadge(requireContext(), headerViewBinding.newChannelBadgeHost, channelAdditionActivitiesNewContentCount, Gravity.CENTER);
        binding.recyclerView.setHeaderView(headerViewBinding.getRoot());
        binding.recyclerView.addOnScrollUpDownYier(new RecyclerView.OnScrollUpDownYier() {
            @Override
            public void onScrollUp() {
                binding.fab.shrink();
            }

            @Override
            public void onScrollDown() {
                binding.fab.extend();
            }
        });
    }

    @Override
    public void onItemClick(int position, ChannelsRecyclerAdapter.ItemData data) {
        Intent intent = new Intent(requireContext(), ChannelActivity.class);
        intent.putExtra(ExtraKeys.ICHAT_ID, data.getChannel().getIchatId());
        intent.putExtra(ExtraKeys.IS_NETWORK_FETCHED, false);
        intent.putExtra(ExtraKeys.CHANNEL_INFO, data.getChannel());
        startActivity(intent);
    }

    private void setupYiers() {
        binding.fab.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SearchChannelActivity.class));
        });
        headerViewBinding.layoutNewChannel.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ChannelAdditionActivitiesActivity.class));
        });
    }

    @Override
    public void onStartUpdate(String id) {

    }

    @Override
    public void onUpdateComplete(String id) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CURRENT_USER_INFO)){
            setupRecyclerView(getLayoutInflater());
        }else if (id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CHANNELS)){
            setupRecyclerView(getLayoutInflater());
        }
    }

    @Override
    public void showNewContentBadge(ID id, int newContentCount) {
        if(id.equals(ID.CHANNEL_ADDITION_ACTIVITIES)){
            channelAdditionActivitiesNewContentCount = newContentCount;
            if(newChannelBadge != null){
                newChannelBadge.setBadgeNumber(newContentCount);
            }
        }
    }

    @Override
    public void onChannelsUpdate() {

    }
}