package com.longx.intelligent.android.ichat2.fragment.channeladdition;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ChannelAdditionActivitiesActivity;
import com.longx.intelligent.android.ichat2.adapter.ChannelAdditionActivitiesReceiveRecyclerAdapter;
import com.longx.intelligent.android.ichat2.adapter.ChannelAdditionActivitiesSendRecyclerAdapter;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionInfo;
import com.longx.intelligent.android.ichat2.data.SelfInfo;
import com.longx.intelligent.android.ichat2.databinding.FragmentReceiveBinding;
import com.longx.intelligent.android.ichat2.databinding.FragmentSendBinding;
import com.longx.intelligent.android.ichat2.util.JsonUtil;
import com.longx.intelligent.android.ichat2.yier.ChannelAdditionActivitiesFetchYier;

import java.util.ArrayList;
import java.util.List;

public class ReceiveFragment extends Fragment implements ChannelAdditionActivitiesFetchYier {
    private FragmentReceiveBinding binding;
    private boolean fetchingVisible;
    private String failureMessage;
    private List<ChannelAdditionInfo> fetchedChannelAdditionInfos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReceiveBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        showContent();
        return binding.getRoot();
    }

    private void showContent() {
        if(fetchingVisible) toFetchingVisible();
        if(failureMessage != null) toFetchFailureMessageVisible(failureMessage);
        if(fetchedChannelAdditionInfos == null) {
            showCachedContent();
        }else {
            setupRecyclerView(fetchedChannelAdditionInfos);
        }
    }

    private void showCachedContent() {
        List<String> channelAdditionActivitiesApiJsons = SharedPreferencesAccessor.ApiJson.getChannelAdditionActivities(requireContext());
        List<ChannelAdditionInfo> channelAdditionInfos = new ArrayList<>();
        channelAdditionActivitiesApiJsons.forEach(channelAdditionActivitiesApiJson -> {
            ChannelAdditionInfo channelAdditionInfo = JsonUtil.toObject(channelAdditionActivitiesApiJson, ChannelAdditionInfo.class);
            channelAdditionInfos.add(channelAdditionInfo);
        });
        setupRecyclerView(channelAdditionInfos);
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
    public void onFetched(List<ChannelAdditionInfo> channelAdditionInfos) {
        fetchingVisible = false;
        if(binding == null) {
            fetchedChannelAdditionInfos = channelAdditionInfos;
        }else {
            setupRecyclerView(channelAdditionInfos);
        }
    }

    private void setupRecyclerView(List<ChannelAdditionInfo> channelAdditionInfos) {
        List<ChannelAdditionInfo> sendChannelAdditionInfos = new ArrayList<>();
        SelfInfo currentUserInfo = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(requireContext());
        channelAdditionInfos.forEach(channelAdditionInfo -> {
            if ((channelAdditionInfo.getRespondTime() != null || channelAdditionInfo.isExpired())
                    && channelAdditionInfo.getResponderChannelInfo().getIchatId().equals(currentUserInfo.getIchatId()))
                sendChannelAdditionInfos.add(channelAdditionInfo);
        });
        if (sendChannelAdditionInfos.size() == 0) {
            if(!fetchingVisible) toNoContentVisible();
        } else {
            if(!fetchingVisible) toRecyclerViewVisible();
            List<ChannelAdditionActivitiesReceiveRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
            sendChannelAdditionInfos.forEach(sendChannelAdditionInfo -> {
                itemDataList.add(new ChannelAdditionActivitiesReceiveRecyclerAdapter.ItemData(sendChannelAdditionInfo));
            });
            ChannelAdditionActivitiesReceiveRecyclerAdapter recyclerAdapter = new ChannelAdditionActivitiesReceiveRecyclerAdapter(requireActivity(), itemDataList);
            binding.recyclerView.setAdapter(recyclerAdapter);
        }
    }

    @Override
    public void onFailure(String failureMessage) {
        this.failureMessage = failureMessage;
        toFetchFailureMessageVisible(failureMessage);
    }
}