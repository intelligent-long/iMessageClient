package com.longx.intelligent.android.imessage.fragment.channeladdition;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.imessage.adapter.ChannelAdditionActivitiesPendingRecyclerAdapter;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.ChannelAddition;
import com.longx.intelligent.android.imessage.databinding.FragmentChannelAdditionPendingBinding;
import com.longx.intelligent.android.imessage.yier.ChannelAdditionActivitiesFetchYier;

import java.util.ArrayList;
import java.util.List;

public class ChannelAdditionPendingFragment extends Fragment implements ChannelAdditionActivitiesFetchYier {
    private FragmentChannelAdditionPendingBinding binding;
    private boolean fetchingVisible;
    private String failureMessage;
    private List<ChannelAddition> fetchedChannelAdditions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChannelAdditionPendingBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        showContent();
        return binding.getRoot();
    }

    private void showContent() {
        if(fetchingVisible) toFetchingVisible();
        if(failureMessage != null) toFetchFailureMessageVisible(failureMessage);
        if(fetchedChannelAdditions == null) {
            showCachedContent();
        }else {
            setupRecyclerView(fetchedChannelAdditions);
        }
    }

    private void showCachedContent() {
        List<ChannelAddition> channelAdditions = SharedPreferencesAccessor.ApiJson.ChannelAdditionActivities.getAllRecords(requireContext());
        setupRecyclerView(channelAdditions);
    }

    private void toNoContentVisible() {
        if(binding != null) binding.noContentTextView.setVisibility(View.VISIBLE);
        if(binding != null) binding.recyclerView.setVisibility(View.GONE);
        if(binding != null) binding.fetchingIndicatorLayout.setVisibility(View.GONE);
        if(binding != null) binding.fetchFailureMessageLayout.setVisibility(View.GONE);
    }

    private void toRecyclerViewVisible() {
        if(binding != null) binding.noContentTextView.setVisibility(View.GONE);
        if(binding != null) binding.recyclerView.setVisibility(View.VISIBLE);
        if(binding != null) binding.fetchingIndicatorLayout.setVisibility(View.GONE);
        if(binding != null) binding.fetchFailureMessageLayout.setVisibility(View.GONE);
    }

    private void toFetchingVisible(){
        if(binding != null) binding.noContentTextView.setVisibility(View.GONE);
        if(binding != null) binding.recyclerView.setVisibility(View.VISIBLE);
        if(binding != null) binding.fetchingIndicatorLayout.setVisibility(View.VISIBLE);
        if(binding != null) binding.fetchFailureMessageLayout.setVisibility(View.GONE);
    }

    private void toFetchFailureMessageVisible(String message){
        if(binding != null) binding.noContentTextView.setVisibility(View.GONE);
        if(binding != null) binding.recyclerView.setVisibility(View.VISIBLE);
        if(binding != null) binding.fetchingIndicatorLayout.setVisibility(View.GONE);
        if(binding != null) binding.fetchFailureMessageLayout.setVisibility(View.VISIBLE);
        if(binding != null) binding.fetchFailureMessage.setText(message);
    }

    @Override
    public void onStartFetch() {
        fetchingVisible = true;
        toFetchingVisible();
    }

    @Override
    public void onFetched(List<ChannelAddition> channelAdditions) {
        fetchingVisible = false;
        if(binding == null) {
            fetchedChannelAdditions = channelAdditions;
        }else {
            setupRecyclerView(channelAdditions);
        }
    }

    private void setupRecyclerView(List<ChannelAddition> channelAdditions) {
        List<ChannelAddition> pendingChannelAdditions = new ArrayList<>();
        channelAdditions.forEach(channelAdditionInfo -> {
            if(channelAdditionInfo.getRespondTime() == null && !channelAdditionInfo.isExpired()) {
                pendingChannelAdditions.add(channelAdditionInfo);
            }
        });
        if(pendingChannelAdditions.isEmpty()){
            if(!fetchingVisible) toNoContentVisible();
        }else {
            if(!fetchingVisible) toRecyclerViewVisible();
            List<ChannelAdditionActivitiesPendingRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
            pendingChannelAdditions.forEach(pendingChannelAdditionInfo -> {
                itemDataList.add(new ChannelAdditionActivitiesPendingRecyclerAdapter.ItemData(pendingChannelAdditionInfo));
            });
            ChannelAdditionActivitiesPendingRecyclerAdapter recyclerAdapter = new ChannelAdditionActivitiesPendingRecyclerAdapter(requireActivity(), itemDataList);
            binding.recyclerView.setAdapter(recyclerAdapter);
        }
    }

    @Override
    public void onFailure(String failureMessage) {
        this.failureMessage = failureMessage;
        toFetchFailureMessageVisible(failureMessage);
    }
}