package com.longx.intelligent.android.imessage.fragment.groupchanneladdition;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.imessage.adapter.GroupChannelAdditionActivitiesSendRecyclerAdapter;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.GroupChannelActivity;
import com.longx.intelligent.android.imessage.data.GroupChannelAddition;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.databinding.FragmentGroupChannelAdditionSendBinding;
import com.longx.intelligent.android.imessage.yier.GroupChannelAdditionActivitiesFetchYier;

import java.util.ArrayList;
import java.util.List;

public class GroupChannelAdditionSendFragment extends Fragment implements GroupChannelAdditionActivitiesFetchYier {
    private FragmentGroupChannelAdditionSendBinding binding;
    private boolean fetchingVisible;
    private String failureMessage;
    private List<GroupChannelActivity> fetchedGroupChannelActivities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGroupChannelAdditionSendBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        showContent();
        return binding.getRoot();
    }

    private void showContent() {
        if(fetchingVisible) toFetchingVisible();
        if(failureMessage != null) toFetchFailureMessageVisible(failureMessage);
        if(fetchedGroupChannelActivities == null) {
            showCachedContent();
        }else {
            setupRecyclerView(fetchedGroupChannelActivities);
        }
    }

    private void showCachedContent() {
        List<GroupChannelActivity> groupChannelActivities = SharedPreferencesAccessor.ApiJson.GroupChannelAdditionActivities.getAllRecords(requireContext());
        setupRecyclerView(groupChannelActivities);
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

    private void setupRecyclerView(List<GroupChannelActivity> groupChannelActivities) {
        List<GroupChannelAddition> sendGroupChannelAdditions = new ArrayList<>();
        Self currentUserInfo = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(requireContext());
        groupChannelActivities.forEach(groupChannelActivity -> {
            if(groupChannelActivity instanceof GroupChannelAddition){
                GroupChannelAddition groupChannelAddition = (GroupChannelAddition) groupChannelActivity;
                if ((groupChannelAddition.getRespondTime() != null || groupChannelAddition.isExpired())
                        && groupChannelAddition.getRequesterChannel().getImessageId().equals(currentUserInfo.getImessageId()))
                    sendGroupChannelAdditions.add(groupChannelAddition);
            }
        });
        if (sendGroupChannelAdditions.isEmpty()) {
            if(!fetchingVisible) toNoContentVisible();
        } else {
            if(!fetchingVisible) toRecyclerViewVisible();
            List<GroupChannelAdditionActivitiesSendRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
            sendGroupChannelAdditions.forEach(sendGroupChannelAddition -> {
                itemDataList.add(new GroupChannelAdditionActivitiesSendRecyclerAdapter.ItemData(sendGroupChannelAddition));
            });
            GroupChannelAdditionActivitiesSendRecyclerAdapter recyclerAdapter = new GroupChannelAdditionActivitiesSendRecyclerAdapter(requireActivity(), itemDataList);
            binding.recyclerView.setAdapter(recyclerAdapter);
        }
    }

    @Override
    public void onStartFetch() {
        fetchingVisible = true;
        toFetchingVisible();
    }

    @Override
    public void onFetched(List<GroupChannelActivity> groupChannelActivities) {
        fetchingVisible = false;
        if(binding == null) {
            fetchedGroupChannelActivities = groupChannelActivities;
        }else {
            setupRecyclerView(groupChannelActivities);
        }
    }

    @Override
    public void onFailure(String failureMessage) {
        this.failureMessage = failureMessage;
        toFetchFailureMessageVisible(failureMessage);
    }
}