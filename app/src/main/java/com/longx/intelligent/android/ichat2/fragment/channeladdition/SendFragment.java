package com.longx.intelligent.android.ichat2.fragment.channeladdition;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.adapter.ChannelAdditionActivitiesSendRecyclerAdapter;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelAddition;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.FragmentSendBinding;
import com.longx.intelligent.android.ichat2.util.JsonUtil;
import com.longx.intelligent.android.ichat2.yier.ChannelAdditionActivitiesFetchYier;

import java.util.ArrayList;
import java.util.List;

public class SendFragment extends Fragment implements ChannelAdditionActivitiesFetchYier {
    private FragmentSendBinding binding;
    private boolean fetchingVisible;
    private String failureMessage;
    private List<ChannelAddition> fetchedChannelAdditions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSendBinding.inflate(inflater, container, false);
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
        List<String> channelAdditionActivitiesApiJsons = SharedPreferencesAccessor.ApiJson.getChannelAdditionActivities(requireContext());
        List<ChannelAddition> channelAdditions = new ArrayList<>();
        channelAdditionActivitiesApiJsons.forEach(channelAdditionActivitiesApiJson -> {
            ChannelAddition channelAddition = JsonUtil.toObject(channelAdditionActivitiesApiJson, ChannelAddition.class);
            channelAdditions.add(channelAddition);
        });
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
        List<ChannelAddition> sendChannelAdditions = new ArrayList<>();
        Self currentUserInfo = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(requireContext());
        channelAdditions.forEach(channelAdditionInfo -> {
            if ((channelAdditionInfo.getRespondTime() != null || channelAdditionInfo.isExpired())
                    && channelAdditionInfo.getRequesterChannel().getIchatId().equals(currentUserInfo.getIchatId()))
                sendChannelAdditions.add(channelAdditionInfo);
        });
        if (sendChannelAdditions.size() == 0) {
            if(!fetchingVisible) toNoContentVisible();
        } else {
            if(!fetchingVisible) toRecyclerViewVisible();
            List<ChannelAdditionActivitiesSendRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
            sendChannelAdditions.forEach(sendChannelAdditionInfo -> {
                itemDataList.add(new ChannelAdditionActivitiesSendRecyclerAdapter.ItemData(sendChannelAdditionInfo));
            });
            ChannelAdditionActivitiesSendRecyclerAdapter recyclerAdapter = new ChannelAdditionActivitiesSendRecyclerAdapter(requireActivity(), itemDataList);
            binding.recyclerView.setAdapter(recyclerAdapter);
        }
    }

    @Override
    public void onFailure(String failureMessage) {
        this.failureMessage = failureMessage;
        toFetchFailureMessageVisible(failureMessage);
    }
}