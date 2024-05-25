package com.longx.intelligent.android.ichat2.fragment.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.ichat2.activity.InstanceStateKeys;
import com.longx.intelligent.android.ichat2.adapter.OpenedChatsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.databinding.FragmentMessagesBinding;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class MessagesFragment extends BaseMainFragment implements OpenedChatsUpdateYier {
    private FragmentMessagesBinding binding;
    private OpenedChatsRecyclerAdapter adapter;
    private boolean stopped;
    private static final Bundle instanceState = new Bundle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(requireContext(), OpenedChatsUpdateYier.class, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        setupYiers();
        setupRecyclerView();
        GlobalYiersHolder.holdYier(requireContext(), OpenedChatsUpdateYier.class, this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbarNavIcon(binding.toolbar);
    }

    public void saveState() {
        if (binding.recyclerView.getLayoutManager() != null) {
            Parcelable recyclerViewState = binding.recyclerView.getLayoutManager().onSaveInstanceState();
            instanceState.putParcelable(InstanceStateKeys.MessagesFragment.RECYCLER_VIEW_STATE, recyclerViewState);
        }

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            int appBarVerticalOffset = behavior.getTopAndBottomOffset();
            instanceState.putInt(InstanceStateKeys.MessagesFragment.APP_BAR_LAYOUT_STATE, appBarVerticalOffset);
        }

        instanceState.putBoolean(InstanceStateKeys.MessagesFragment.FAB_EXPANDED_STATE, binding.startChatFab.isExtended());
    }

    private void restoreState() {
        binding.recyclerView.getLayoutManager().onRestoreInstanceState(instanceState.getParcelable(InstanceStateKeys.MessagesFragment.RECYCLER_VIEW_STATE));
        int appBarVerticalOffset = instanceState.getInt(InstanceStateKeys.MessagesFragment.APP_BAR_LAYOUT_STATE, 0);
        binding.appbar.post(() -> binding.appbar.setExpanded(appBarVerticalOffset == 0, false));
        boolean isFabExpanded = instanceState.getBoolean(InstanceStateKeys.MessagesFragment.FAB_EXPANDED_STATE, true);
        if (isFabExpanded) {
            binding.startChatFab.extend();
        } else {
            binding.startChatFab.shrink();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        saveState();
        stopped = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(stopped) {
            restoreState();
        }
        stopped = false;
    }

    private void setupYiers() {
        binding.recyclerView.addOnThresholdScrollUpDownYier(new RecyclerView.OnThresholdScrollUpDownYier(50){

            @Override
            public void onScrollUp() {
                if(binding.startChatFab.isExtended()) binding.startChatFab.shrink();
            }

            @Override
            public void onScrollDown() {
                if(!binding.startChatFab.isExtended()) binding.startChatFab.extend();
            }
        });
        binding.startChatFab.setOnClickListener((View.OnClickListener) getActivity());
    }

    private void setupRecyclerView() {
        adapter = new OpenedChatsRecyclerAdapter(requireActivity());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public Toolbar getToolbar() {
        return binding == null ? null : binding.toolbar;
    }

    private void toNoContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
        binding.noContentLayout.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    private void toContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        binding.noContentLayout.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOpenedChatsUpdate() {
        if(adapter != null) {
            List<OpenedChat> allShowOpenedChats = OpenedChatDatabaseManager.getInstance().findAllShow();
            adapter.changeAllItemsAndShow(allShowOpenedChats);
            if (allShowOpenedChats.size() == 0) {
                toNoContent();
            } else {
                toContent();
            }
        }
    }
}