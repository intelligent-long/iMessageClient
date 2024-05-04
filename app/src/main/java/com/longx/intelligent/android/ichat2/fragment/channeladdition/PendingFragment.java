package com.longx.intelligent.android.ichat2.fragment.channeladdition;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.adapter.PendingChannelAdditionActivitiesRecyclerAdapter;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionInfo;
import com.longx.intelligent.android.ichat2.databinding.FragmentPendingBinding;
import com.longx.intelligent.android.ichat2.util.JsonUtil;
import com.longx.intelligent.android.ichat2.yier.ChannelAdditionActivitiesFetchYier;

import java.util.ArrayList;
import java.util.List;

public class PendingFragment extends Fragment implements ChannelAdditionActivitiesFetchYier {
    private FragmentPendingBinding binding;
    private boolean fetchingVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPendingBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        showContent();
        return binding.getRoot();
    }

    private void showContent() {
        if(fetchingVisible) toFetchingVisible();
        showCachedContent();
    }

    private void showCachedContent() {
        List<String> channelAdditionActivitiesApiJsons = SharedPreferencesAccessor.ApiJson.getChannelAdditionActivities(requireContext());
        List<ChannelAdditionInfo> channelAdditionInfos = new ArrayList<>();
        channelAdditionActivitiesApiJsons.forEach(channelAdditionActivitiesApiJson -> {
            ChannelAdditionInfo channelAdditionInfo = JsonUtil.toObject(channelAdditionActivitiesApiJson, ChannelAdditionInfo.class);
            channelAdditionInfos.add(channelAdditionInfo);
        });
        List<ChannelAdditionInfo> pendingChannelAdditionInfos = new ArrayList<>();
        channelAdditionInfos.forEach(channelAdditionInfo -> {
            if(channelAdditionInfo.getRespondTime() == null){
                pendingChannelAdditionInfos.add(channelAdditionInfo);
            }
        });
        if(!(pendingChannelAdditionInfos.size() > 0)){
            if(!fetchingVisible) toNoContentVisible();
        }else {
            if(!fetchingVisible) toRecyclerViewVisible();
            ArrayList<PendingChannelAdditionActivitiesRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
            pendingChannelAdditionInfos.forEach(channelAdditionInfo -> {
                itemDataList.add(new PendingChannelAdditionActivitiesRecyclerAdapter.ItemData(channelAdditionInfo));
            });
            PendingChannelAdditionActivitiesRecyclerAdapter recyclerAdapter = new PendingChannelAdditionActivitiesRecyclerAdapter((AppCompatActivity) requireActivity(), itemDataList);
            binding.recyclerView.setAdapter(recyclerAdapter);
        }
    }

    private void toNoContentVisible() {
        if(binding != null) binding.noContentTextView.setVisibility(View.VISIBLE);
        if(binding != null) binding.recyclerView.setVisibility(View.GONE);
        if(binding != null) binding.fetchingIndicatorLayout.setVisibility(View.GONE);
    }

    private void toRecyclerViewVisible() {
        if(binding != null) binding.noContentTextView.setVisibility(View.GONE);
        if(binding != null) binding.recyclerView.setVisibility(View.VISIBLE);
        if(binding != null) binding.fetchingIndicatorLayout.setVisibility(View.GONE);
    }

    private void toFetchingVisible(){
        if(binding != null) binding.noContentTextView.setVisibility(View.GONE);
        if(binding != null) binding.recyclerView.setVisibility(View.VISIBLE);
        if(binding != null) binding.fetchingIndicatorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStartFetch() {
        fetchingVisible = true;
        toFetchingVisible();

    }

    @Override
    public void onFetched(List<ChannelAdditionInfo> channelAdditionInfos) {
        fetchingVisible = false;
        List<ChannelAdditionInfo> pendingChannelAdditionInfos = new ArrayList<>();
        channelAdditionInfos.forEach(channelAdditionInfo -> {
            if(channelAdditionInfo.getRespondTime() == null) pendingChannelAdditionInfos.add(channelAdditionInfo);
        });
        if(pendingChannelAdditionInfos.size() == 0){
            toNoContentVisible();
        }else {
            toRecyclerViewVisible();
            List<PendingChannelAdditionActivitiesRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
            pendingChannelAdditionInfos.forEach(pendingChannelAdditionInfo -> {
                itemDataList.add(new PendingChannelAdditionActivitiesRecyclerAdapter.ItemData(pendingChannelAdditionInfo));
            });
            PendingChannelAdditionActivitiesRecyclerAdapter recyclerAdapter = new PendingChannelAdditionActivitiesRecyclerAdapter(requireActivity(), itemDataList);
            binding.recyclerView.setAdapter(recyclerAdapter);
        }
    }
}