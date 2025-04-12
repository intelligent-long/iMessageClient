package com.longx.intelligent.android.imessage.fragment.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ChannelActivity;
import com.longx.intelligent.android.imessage.activity.ChannelAdditionsActivity;
import com.longx.intelligent.android.imessage.activity.GroupChannelsActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.InstanceStateKeys;
import com.longx.intelligent.android.imessage.activity.ExploreChannelActivity;
import com.longx.intelligent.android.imessage.activity.SearchChannelActivity;
import com.longx.intelligent.android.imessage.activity.TagActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.ChannelAssociation;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.adapter.ChannelsRecyclerAdapter;
import com.longx.intelligent.android.imessage.databinding.FragmentChannelsBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerHeaderChannelBinding;
import com.longx.intelligent.android.imessage.ui.BadgeDisplayer;
import com.longx.intelligent.android.imessage.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;

public class ChannelsFragment extends BaseMainFragment implements WrappableRecyclerViewAdapter.OnItemClickYier<ChannelsRecyclerAdapter.ItemData>, ContentUpdater.OnServerContentUpdateYier, NewContentBadgeDisplayYier {
    private FragmentChannelsBinding binding;
    private RecyclerHeaderChannelBinding headerViewBinding;
    private int channelAdditionActivitiesNewContentCount;
    private Badge newChannelBadge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalYiersHolder.holdYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.holdYier(requireContext(), NewContentBadgeDisplayYier.class, this, ID.CHANNEL_ADDITION_ACTIVITIES);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(requireContext(), ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.removeYier(requireContext(), NewContentBadgeDisplayYier.class, this, ID.CHANNEL_ADDITION_ACTIVITIES);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (binding.recyclerView.getLayoutManager() != null) {
            Parcelable recyclerViewState = binding.recyclerView.getLayoutManager().onSaveInstanceState();
            outState.putParcelable(InstanceStateKeys.ChannelsFragment.RECYCLER_VIEW_STATE, recyclerViewState);
        }

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            int appBarVerticalOffset = behavior.getTopAndBottomOffset();
            outState.putInt(InstanceStateKeys.ChannelsFragment.APP_BAR_LAYOUT_STATE, appBarVerticalOffset);
        }

        outState.putBoolean(InstanceStateKeys.ChannelsFragment.FAB_EXPANDED_STATE, binding.addChannelFab.isExtended());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChannelsBinding.inflate(inflater, container, false);
        setupRecyclerView(inflater);
        restoreState(savedInstanceState);
        return binding.getRoot();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Parcelable recyclerViewState = savedInstanceState.getParcelable(InstanceStateKeys.ChannelsFragment.RECYCLER_VIEW_STATE);
            binding.recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            int appBarVerticalOffset = savedInstanceState.getInt(InstanceStateKeys.ChannelsFragment.APP_BAR_LAYOUT_STATE, 0);
            binding.appbar.post(() -> binding.appbar.setExpanded(appBarVerticalOffset == 0, false));
            boolean isFabExpanded = savedInstanceState.getBoolean(InstanceStateKeys.ChannelsFragment.FAB_EXPANDED_STATE, true);
            if (isFabExpanded) {
                binding.addChannelFab.extend();
            } else {
                binding.addChannelFab.shrink();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupYiers();
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
        Self self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(requireContext());
        Channel selfChannel = new Channel(
                self.getImessageId(),
                self.getImessageIdUser(),
                self.getEmail(),
                self.getUsername(),
                null,
                self.getAvatar(),
                self.getSex(),
                self.getFirstRegion(),
                self.getSecondRegion(),
                self.getThirdRegion(),
                true);
        itemDataList.add(new ChannelsRecyclerAdapter.ItemData(selfChannel));
        List<ChannelAssociation> channelAssociations = ChannelDatabaseManager.getInstance().findAllAssociations();
        channelAssociations.forEach(channelAssociation -> {
            itemDataList.add(new ChannelsRecyclerAdapter.ItemData(channelAssociation.getChannel()));
        });
        ChannelsRecyclerAdapter channelsRecyclerAdapter = new ChannelsRecyclerAdapter(requireActivity(), this, itemDataList);
        channelsRecyclerAdapter.setOnItemClickYier(this);
        binding.recyclerView.setAdapter(channelsRecyclerAdapter);
        headerViewBinding = RecyclerHeaderChannelBinding.inflate(inflater, binding.getRoot(), false);
        newChannelBadge = BadgeDisplayer.initBadge(requireContext(), headerViewBinding.newChannelBadgeHost, channelAdditionActivitiesNewContentCount, Gravity.CENTER);
        binding.recyclerView.setHeaderView(headerViewBinding.getRoot());
    }

    @Override
    public void onItemClick(int position, ChannelsRecyclerAdapter.ItemData data) {
        Intent intent = new Intent(requireContext(), ChannelActivity.class);
        intent.putExtra(ExtraKeys.IMESSAGE_ID, data.getChannel().getImessageId());
        intent.putExtra(ExtraKeys.CHANNEL, data.getChannel());
        startActivity(intent);
    }

    private void setupYiers() {
        binding.recyclerView.addOnThresholdScrollUpDownYier(new RecyclerView.OnThresholdScrollUpDownYier(50){
            @Override
            public void onScrollUp() {
                if(binding.addChannelFab.isExtended()) binding.addChannelFab.shrink();
            }

            @Override
            public void onScrollDown() {
                if(!binding.addChannelFab.isExtended()) binding.addChannelFab.extend();
            }
        });
        binding.addChannelFab.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ExploreChannelActivity.class));
        });
        headerViewBinding.layoutNewChannel.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ChannelAdditionsActivity.class));
        });
        headerViewBinding.layoutTag.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), TagActivity.class));
        });
        headerViewBinding.layoutGroupChat.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), GroupChannelsActivity.class));
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.search){
                startActivity(new Intent(requireContext(), SearchChannelActivity.class));
            }
            return false;
        });
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds) {
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

    public FragmentChannelsBinding getBinding() {
        return binding;
    }
}