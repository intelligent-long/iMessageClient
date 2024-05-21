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
    private Parcelable recyclerViewState;
    private int appBarVerticalOffset;
    private boolean fabExtended;

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
            recyclerViewState = binding.recyclerView.getLayoutManager().onSaveInstanceState();
        }

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            appBarVerticalOffset = behavior.getTopAndBottomOffset();
        }

        fabExtended = binding.startChatFab.isExtended();
    }

    private void restoreState() {
        binding.recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        binding.appbar.post(() -> binding.appbar.setExpanded(appBarVerticalOffset == 0, false));
        if (fabExtended) {
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
            OpenedChat[] openedChats = allShowOpenedChats.toArray(new OpenedChat[0]);
            for (int i = 0; i < 21; i++) {
                allShowOpenedChats.addAll(Arrays.asList(openedChats));
            }
            adapter.changeAllItemsAndShow(allShowOpenedChats);
            if (allShowOpenedChats.size() == 0) {
                toNoContent();
            } else {
                toContent();
            }
        }
    }
}