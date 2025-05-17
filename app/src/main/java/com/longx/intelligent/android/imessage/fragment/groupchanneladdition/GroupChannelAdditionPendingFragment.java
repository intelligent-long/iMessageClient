package com.longx.intelligent.android.imessage.fragment.groupchanneladdition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.adapter.GroupChannelAdditionActivitiesPendingRecyclerAdapter;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.GroupChannelActivity;
import com.longx.intelligent.android.imessage.data.GroupChannelAddition;
import com.longx.intelligent.android.imessage.databinding.FragmentGroupChannelAdditionPendingBinding;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.yier.GroupChannelAdditionActivitiesFetchYier;

import java.util.ArrayList;
import java.util.List;

public class GroupChannelAdditionPendingFragment extends Fragment implements GroupChannelAdditionActivitiesFetchYier {
    private FragmentGroupChannelAdditionPendingBinding binding;
    private boolean fetchingVisible;
    private String failureMessage;
    private List<GroupChannelActivity> fetchedGroupChannelActivities;
    private FragmentActivity fragmentActivity;

    public GroupChannelAdditionPendingFragment(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGroupChannelAdditionPendingBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(fragmentActivity));
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
        List<GroupChannelActivity> pendingGroupChannelActivities = new ArrayList<>();
        groupChannelActivities.forEach(groupChannelActivity -> {
            if(groupChannelActivity.getRespondTime() == null && !groupChannelActivity.isExpired()) {
                pendingGroupChannelActivities.add(groupChannelActivity);
            }
        });
        if(pendingGroupChannelActivities.isEmpty()){
            if(!fetchingVisible) toNoContentVisible();
        }else {
            if(!fetchingVisible) toRecyclerViewVisible();
            List<GroupChannelAdditionActivitiesPendingRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
            pendingGroupChannelActivities.forEach(pendingGroupChannelActivity -> {
                itemDataList.add(new GroupChannelAdditionActivitiesPendingRecyclerAdapter.ItemData(pendingGroupChannelActivity));
            });
            GroupChannelAdditionActivitiesPendingRecyclerAdapter recyclerAdapter = new GroupChannelAdditionActivitiesPendingRecyclerAdapter(fragmentActivity, itemDataList);
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